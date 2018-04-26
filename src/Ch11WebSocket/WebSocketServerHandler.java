package Ch11WebSocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;


/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/3/27 14:25
 * @desc :
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //http接入
        if(msg instanceof FullHttpRequest){
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }
        //WebSocket接入
        else if(msg instanceof WebSocketFrame){
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        //http解码失败，返回http 400异常
        if(!request.decoderResult().isSuccess() || (!"websocket".equalsIgnoreCase(request.headers().get("Upgrade")))){
            sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //本机测试，构造握手响应
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
        handshaker = wsFactory.newHandshaker(request);
        if(handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else{
            handshaker.handshake(ctx.channel(), request);
        }
    }


    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //如果是关闭链路的指令
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame);
            return;
        }
        //如果是ping消息
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        //非文本消息抛出异常
        if(!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        String reqestMsg = ((TextWebSocketFrame) frame).text();
        /*if(logger.isLoggable(Level.FINE)){
            logger.fine(String.format("%s received %s", ctx.channel(), reqestMsg));
        }*/


        ctx.channel().write(new TextWebSocketFrame(reqestMsg + ", Welcome, now: " + new java.util.Date().toString()));
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, DefaultFullHttpResponse response) {
        if(response.status().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            setContentLength(response, response.content().readableBytes());
        }

        ChannelFuture future = ctx.channel().writeAndFlush(response);
        if(!isKeepAlive(request) || response.status().code() != 200){
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
