package UdpImpl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/6/3 21:04
 * @desc :
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket packet, List<Object> out) throws Exception {
        ByteBuf data = packet.content();

        /*
         * Netty实战书上没有这段处理，结果就是当udp收到一个大的数据之后，后面收到的都是错误的。因为数组扩容后面的内容还存在
         * 例如
         * 当收到一个"123123123"之后，再次收到一个服务端发送"hello"，客户端收到的数据是"hello3123"
        */
        /*int idx = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR);
        String filename = data.slice(0, idx).toString(CharsetUtil.UTF_8);
        String logMsg = data.slice(idx + 1, data.readableBytes()).toString(CharsetUtil.UTF_8);*/

        int idxOne = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR);
        String filename = data.slice(0, idxOne).toString(CharsetUtil.UTF_8);
        int idxTwo = data.indexOf(idxOne+1, data.readableBytes(), LogEvent.SEPARATOR);
        String logMsg = data.slice(idxOne+1, idxTwo-idxOne-1).toString(CharsetUtil.UTF_8);

        LogEvent event = new LogEvent(packet.sender(), System.currentTimeMillis(), filename, logMsg);
        System.out.println("client logmsg: " + logMsg);
        out.add(event);
    }
}
