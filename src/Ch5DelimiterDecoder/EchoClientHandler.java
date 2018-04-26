package Ch5DelimiterDecoder;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.logging.Logger;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/3/25 19:24
 * @desc :
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {
    private int counter;
//    static final String ECHO_REQ_DELIMITER = "Welcome to netty echo service$_";
    private static final Logger logger = Logger.getLogger(EchoClientHandler.class.getName());

    static final String ECHO_REQ_FIXEDLENGTH = "0123456789";

    public EchoClientHandler(){}

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for(int i = 0; i < 10; i++){
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ_FIXEDLENGTH.getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Received from server counter: " + ++counter + ",  msg: " + (String)msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
