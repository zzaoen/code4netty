package Demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/4/25 21:39
 * @desc :
 */

/*public class TimeEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        TimeObject object = (TimeObject) msg;
        ByteBuf buf = ctx.alloc().buffer(4);
        buf.writeInt((int) object.value());
        ctx.write(buf, promise);
    }
}*/

//上面已经可以实现功能了，但是可以更加简化
public class TimeEncoder extends MessageToByteEncoder<TimeObject> {
    @Override
    protected void encode(ChannelHandlerContext ctx, TimeObject msg, ByteBuf out) {
//        System.out.println(msg.value());
//        System.out.println(msg.toString());
        out.writeInt((int) msg.value());
    }
}
