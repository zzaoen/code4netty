package Ch4StickPackageFixed;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

/**
 * @author: Bruce Zhao
 * @email: zhzh402@163.com
 * @date: 2018/3/21 19:23
 */
public class NettyTimeClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = Logger.getLogger(NettyTimeClientHandler.class.getName());

    private int counter;
    private byte[] req;


    public NettyTimeClientHandler() {
//        String order = "QUERY TIME ORDER" + System.getProperty("line.separator"); //可以
        String order = "QUERY TIME ORDER\n"; //可以，相当于上面的
//        String order = "QUERY TIME ORDER"; //不可以，结尾没有分割符，LineBasedFrameDecoder无法成功编解码
        req = order.getBytes();

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        ByteBuf message = null;
        for(int i = 0; i < 100; i++){
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{

        String body = (String) msg;

        System.out.println("Now is: " + body + ", the counter is: " + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.warning("Unexpected exception from downstream: " + cause.getMessage());
        ctx.close();
    }
}
