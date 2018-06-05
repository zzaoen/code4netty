/*
 * Copyright 2013-2018 Lilinfeng.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain SuperClass copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package Ch10HttpFileServer;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;
import static io.netty.handler.codec.rtsp.RtspHeaderNames.CONNECTION;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

/**
 * @author lilinfeng
 * @version 1.0
 * @date 2014年2月14日
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final String CRLF = "\r\n";
    private final String localDir; //服务器文件地址
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
    public static final HttpVersion HTTP_1_1 = new HttpVersion("HTTP", 1, 1, true);


    public HttpFileServerHandler(String localDir) {
        this.localDir = localDir;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!request.decoderResult().isSuccess()) {
            sendErrorToClient(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if (request.method().compareTo(HttpMethod.GET) != 0) {
            sendErrorToClient(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        String uri = request.uri();
        uri = URLDecoder.decode(uri, "utf-8");

        String filePath = getAbsoluteFilePath(uri);
        File file = new File(filePath);

        if (!file.exists()) {
            sendErrorToClient(ctx, NOT_FOUND);
            return;
        }

        if (file.isDirectory()) { //如果点击的是目录
            sendDirListToClient(ctx, file, uri);
            return;
        }

        if (file.isFile()) { //如果点击的是文件
            sendFileToClient(ctx, request, file, uri);
            return;
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
//        if (ctx.channel().isActive()) {
//            sendError(ctx, INTERNAL_SERVER_ERROR);
//        }
        ctx.close();
    }



    private String getAbsoluteFilePath(String uri) {
        //System.out.println(System.getProperty("user.dir"));
        return localDir + uri;

    }

    private void sendErrorToClient(ChannelHandlerContext ctx, HttpResponseStatus status) {
        ByteBuf buf = Unpooled.copiedBuffer(("Error: " + status.toString() + CRLF).getBytes());
        FullHttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, status, buf);
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }


    private void sendFileToClient(ChannelHandlerContext ctx, FullHttpRequest request, File file, String uri) throws IOException {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");// 以只读的方式打开文件
        } catch (FileNotFoundException fnfe) {
            sendErrorToClient(ctx, NOT_FOUND);
            return;
        }
        long fileLength = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);

        setContentLength(response, fileLength);

        setContentTypeHeader(response, file); //自己实现的设置response头部信息

        if (isKeepAlive(request)) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);

        ChannelFuture sendFileFuture;
        sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0,
                fileLength, 8192), ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future,
                                            long progress, long total) {
                if (total < 0) { // total unknown
                    System.err.println("Transfer progress: " + progress);
                } else {
                    System.err.println("Transfer progress: " + progress + " / "
                            + total);
                }
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future)
                    throws Exception {
                System.out.println("Transfer complete.");
            }
        });
        ChannelFuture lastContentFuture = ctx
                .writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!isKeepAlive(request)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void sendFileToClientSimple(ChannelHandlerContext ctx, File file, String uri) throws IOException {
        ByteBuf buf = Unpooled.copiedBuffer(Files.readAllBytes(file.toPath()));
        FullHttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, buf);
        MimetypesFileTypeMap mimeTypeMap = new MimetypesFileTypeMap();
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypeMap.getContentType(file));
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendDirListToClient(ChannelHandlerContext ctx, File dir, String uri) throws IOException {
        StringBuffer sb = new StringBuffer("");
        String dirpath = dir.getPath();
        sb.append("<!DOCTYPE HTML>" + CRLF);
        sb.append("<html><head><title>");
        sb.append(dirpath);
        sb.append("目录：");
        sb.append("</title></head><body>" + CRLF);
        sb.append("<h3>");
        sb.append("当前目录:" + dirpath);
        sb.append("</h3>");
        sb.append("<table>");
        sb.append("<tr><td colspan='3'>上一级:<SuperClass href=\"../\">..</SuperClass>  </td></tr>");
        sb.append("<tr><td>日期</td><td>大小</td><td>名称</td></tr>");
        if (uri.equals("/")) {
            uri = "";
        } else {
            if (uri.charAt(0) == '/') {
                uri = uri.substring(0);
            }
            uri += "/";
        }

        String fnameShow;
        for (File f : dir.listFiles()) {
            if (f.isHidden() || !f.canRead()) {
                continue;
            }
            String fileName = f.getName();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(f.lastModified());
            String lastModified = dateFormat.format(cal.getTime());
            sb.append("<tr>");
            if (f.isFile()) {
                fnameShow = "<font color='green'>" + fileName + "</font>";
            } else {
                fnameShow = "<font color='red'>" + fileName + "</font>";
            }
            sb.append("<td style='width:600px'> " + lastModified + "</td><td style='width:100px'>" + Files.size(f.toPath()) + "</td><td><a href=\"" + uri + fileName + "\">" + fnameShow + "</SuperClass></td>");
            sb.append("</tr>");

        }
        sb.append("</table>");
        ByteBuf buffer = Unpooled.copiedBuffer(sb.toString(), CharsetUtil.UTF_8);
        FullHttpResponse resp = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, buffer);
        resp.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }

    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE,
                mimeTypesMap.getContentType(file.getPath()));
    }








    /*private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    private String getRelativeFilePath(String uri) {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }
        if (!uri.startsWith(localDir)) {
            return null;
        }
        if (!uri.startsWith("/")) {
            return null;
        }
        uri = uri.replace('/', File.separatorChar);
        if (uri.contains(File.separator + '.')
                || uri.contains('.' + File.separator) || uri.startsWith(".")
                || uri.endsWith(".") || INSECURE_URI.matcher(uri).matches()) {
            return null;
        }
        return System.getProperty("user.dir") + File.separator + uri;
    }*/

    /*
    private static final Pattern ALLOWED_FILE_NAME = Pattern
            .compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
    private static void sendDirListToClient(ChannelHandlerContext ctx, File dir, String uri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        StringBuilder buf = new StringBuilder();
        String dirPath = dir.getPath();
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append(" 目录：");
        buf.append("</title></head><body>\r\n");
        buf.append("<h3>");
        buf.append(dirPath).append(" 目录：");
        buf.append("</h3>\r\n");
        buf.append("<ul>");
        buf.append("<li>链接：<SuperClass href=\"../\">..</SuperClass></li>\r\n");
        for (File f : dir.listFiles()) {
            if (f.isHidden() || !f.canRead()) {
                continue;
            }
            String name = f.getName();
            if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                continue;
            }
            buf.append("<li>链接：<SuperClass href=\"");
            buf.append(name);
            buf.append("\">");
            buf.append(name);
            buf.append("</SuperClass></li>\r\n");
        }
        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }*/

    /*private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, FOUND);
        response.headers().set(LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }*/

}
