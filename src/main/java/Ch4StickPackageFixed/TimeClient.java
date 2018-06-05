package Ch4StickPackageFixed;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author: Bruce Zhao
 * @email: zhzh402@163.com
 * @date: 2018/3/21 17:27
 */
public class TimeClient {

    public static void main(String[] args) throws Exception{
        int port = 8080;
        new TimeClient().connect("127.0.0.1", port);
    }

    public void connect(String host, int port) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                          @Override
                          protected void initChannel(SocketChannel socketChannel) throws Exception {
                              socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                              socketChannel.pipeline().addLast(new StringDecoder());
                              socketChannel.pipeline().addLast(new NettyTimeClientHandler());
                          }
                    });
            //异步连接操作
            ChannelFuture future = bootstrap.connect(host, port).sync();
            //等待客户端链路关闭
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }
    }

}
