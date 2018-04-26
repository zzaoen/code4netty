package Ch10HttpFileServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author : Bruce Zhao
 * @email  : zhzh402@163.com
 * @date   : 2018/3/26 22:23
 * @desc   : Netty http的文件服务器，
 */
public class HttpFileServer {
    private static final String DEFAULT_DIR = "d:";

    public static void main(String[] args) throws Exception {
        int port = 80;
        String localDir = DEFAULT_DIR;

        new HttpFileServer().run(port, localDir);
    }

    public void run(final int port, final String localDir) throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup wokerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, wokerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("http-decoder", new HttpRequestDecoder()); //http请求消息的解码器
                            socketChannel.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536)); //http解码器会把http消息生成多个消息对象，这个把多个消息合并为一个单一的Http请求

                            socketChannel.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler()); //用来支持异步发送大的码流，比如文件传输
                            socketChannel.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(localDir));
                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("Netty File Server started at: http://127.0.0.1:" + port);

            future.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            wokerGroup.shutdownGracefully();
        }
    }

}

