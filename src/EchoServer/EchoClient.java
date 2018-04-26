package EchoServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * author : Bruce Zhao
 * email  : zhzh402@163.com
 * date   : 2018/4/16 14:34
 * desc   :
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        int port = 8888;
        new EchoClient(host, port).start();
        return;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());

                        }
                    });
            ChannelFuture future = bootstrap.connect().sync();

            future.channel().closeFuture().sync();


        }finally {
            group.shutdownGracefully().sync();
        }

    }
}
