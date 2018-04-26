package Demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/4/25 21:06
 * @desc :
 */
public class Client {


    public static void main(String[] args) throws InterruptedException {

        new Client().run("127.0.0.1",8080);
        return;
    }

    public void run(String host, int port) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeDecoder());
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });

            ChannelFuture future = b.connect(host, port).sync();
            future.channel().closeFuture().sync();

        }finally {
            workerGroup.shutdownGracefully();
        }
    }
}
