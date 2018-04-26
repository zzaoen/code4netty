package Demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/4/25 20:49
 * @desc :
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {


        //在这里，直接写一个TimeObject是不行的，因为需要编码成ByteBuf才可以，所以增加一个handler，用来编码
        final ChannelFuture future = ctx.writeAndFlush(new TimeObject());

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                assert future == future;
                ctx.close();
            }
        });

        /*final ByteBuf time = ctx.alloc().buffer(4);

        time.writeInt((int)(System.currentTimeMillis()/1000L + 2208988800L));

        final ChannelFuture future = ctx.writeAndFlush(time);

        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                assert future == future;
                ctx.close();
            }
        });*/
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ((ByteBuf)msg).release();

       /* ByteBuf in = (ByteBuf) msg;
        try{
            while(in.isReadable()){
                System.out.println((char) in.readByte());
                System.out.flush();
            }
        }finally {
            ReferenceCountUtil.release(msg);
        }*/

       ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
