package HandlerSequence;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/4/26 20:26
 * @desc :
 */
public class InboundHandler1 extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("InboundHandler1.channelRead; " + ctx);
        System.out.println("InboundHandler1.channelRead; ");
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("InboundHandler1.channelReadComplete; " + ctx);
        System.out.println("InboundHandler1.channelReadComplete; ");
        ctx.flush();
    }
}
