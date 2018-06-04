package UdpImpl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/6/3 19:44
 * @desc :
 */
public class LogEventEncoder extends MessageToMessageEncoder<LogEvent> {

    private final InetSocketAddress remoteAddress;
    public LogEventEncoder(InetSocketAddress remoteAddress){
        this.remoteAddress = remoteAddress;
    }

    protected void encode(ChannelHandlerContext ctx, LogEvent logEventMsg, List<Object> out) throws Exception {
        byte[] file = logEventMsg.getLogfile().getBytes(CharsetUtil.UTF_8);
        byte[] msg = logEventMsg.getMsg().getBytes(CharsetUtil.UTF_8);
        ByteBuf buf = ctx.alloc().buffer(file.length + msg.length + 1);
        buf.writeBytes(file);
        buf.writeByte(LogEvent.SEPARATOR);
        buf.writeBytes(msg);
        buf.writeByte(LogEvent.SEPARATOR);
        ByteBuf duplicate = buf.duplicate();
        byte[] tmp = new byte[buf.readableBytes()];
        duplicate.readBytes(tmp);
        System.out.println("server buf:" + new String(tmp));
        out.add(new DatagramPacket(buf, remoteAddress));

    }
}
