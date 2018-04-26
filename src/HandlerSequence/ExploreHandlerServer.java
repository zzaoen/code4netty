package HandlerSequence;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/4/26 20:17
 * @desc :
 */
public class ExploreHandlerServer {

    public static void main(String[] args) throws InterruptedException {

        new ExploreHandlerServer().bind(8080);
        return;
    }

    public void bind(int port) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {


                            //Inbound是输入时执行的，顺序是先1后2
                            ch.pipeline().addLast(new InboundHandler1());
//Outbound是输出时执行的，顺序是先2后1

                            ch.pipeline().addLast(new InboundHandler2());
                            ch.pipeline().addLast(new OutboundHandler1());
                            ch.pipeline().addLast(new OutboundHandler2());

                        }
                    });
            ChannelFuture future = b.bind(port).sync();

            future.channel().closeFuture().sync();

        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
