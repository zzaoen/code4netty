package UdpImpl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/6/3 19:53
 * @desc :
 */
public class LogEventBroadcaster {
    private final EventLoopGroup group;
    private final Bootstrap bootstrap;
    private final File file;

    public LogEventBroadcaster(InetSocketAddress address, File file){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
//                .handler(new LogEventEncoder(address));
                .handler(new LogBroadInitializer(address));
        this.file = file;

    }

    public static void main(String[] args) throws Exception {
        /*File file = new File("LogEventFile.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(new InetSocketAddress("255.255.255.255", 8888), new File("LogEventFile.txt"));

        try{
            broadcaster.run();
        }finally {
            broadcaster.stop();
        }

        return;
    }

    public void run() throws Exception{
        Channel channel = bootstrap.bind(0).sync().channel();
        long pointer = 0;
        for(;;){
            long len = file.length();
            if(len < pointer){
                pointer = len;
            } else if(len > pointer){
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                raf.seek(pointer);
                String line;
                while((line = raf.readLine()) != null){
//                    System.out.println("server msg: " + line);
                    channel.writeAndFlush(new LogEvent(null, -1, file.getAbsolutePath(), line));
                }

                pointer = raf.getFilePointer();
                raf.close();
            }
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                Thread.interrupted();
                e.printStackTrace();
                break;
            }
        }
    }

    public void stop(){
        group.shutdownGracefully();
    }

    private class LogBroadInitializer extends ChannelInitializer<Channel> {
        InetSocketAddress address;
        public LogBroadInitializer(InetSocketAddress address) {
            this.address = address;
        }

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new LogEventEncoder(address));

        }
    }
}
