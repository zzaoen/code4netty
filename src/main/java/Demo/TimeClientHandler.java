package Demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.Date;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/4/25 21:13
 * @desc :
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    /*private ByteBuf buf;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        buf = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        buf.release();
        buf = null;
    }*/

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        TimeObject object = (TimeObject) msg;
        System.out.println(object);
        ctx.close();

        /*ByteBuf m = (ByteBuf) msg;
        if(m.readableBytes() >= 4) {
            try {
                long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
                System.out.println(new Date(currentTimeMillis));
                ctx.close();
            } finally {
                m.release();
            }
        }*/


        /*ByteBuf m = (ByteBuf) msg;
        buf.writeBytes(m);
        m.release();
        if(buf.readableBytes() >= 4) {
            try {
                long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
                System.out.println(new Date(currentTimeMillis));
                ctx.close();
            } finally {

            }
        }*/
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
