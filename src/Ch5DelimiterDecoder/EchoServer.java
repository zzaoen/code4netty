package Ch5DelimiterDecoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/3/25 19:00
 * @desc :
 */
public class EchoServer {

    public static void main(String[] args){
        int port = 8080;
        try {
            new EchoServer().bind(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bind(int port) throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {


//                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes()); //"$_"作为缓冲区的分隔符
//                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));

                            socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(20));

                            socketChannel.pipeline().addLast(new StringDecoder());;
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            //绑定端口，等待成功，这个是同步的
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

        }
    }
}
