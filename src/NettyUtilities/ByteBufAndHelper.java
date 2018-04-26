package NettyUtilities;

import io.netty.buffer.*;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ByteProcessor;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

/**
 * author : Bruce Zhao
 * email  : zhzh402@163.com
 * date   : 2018/4/8 12:57
 * desc   :
 */
public class ByteBufAndHelper {
    public static void main(String[] args){

        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("Hello".getBytes());
        out.println(buffer.toString());
        buffer.flip();
        byte[] array = new byte[buffer.remaining()];
        out.println(buffer.toString());
        buffer.get(array);
        out.println(buffer.toString());
        out.println(new String(array));

//        buffer.clear();
//        buffer.put("Wo".getBytes());
//        out.println(buffer.toString());


//        ByteBuf byteBuf = new PooledByteBufAllocator().buffer();
        ByteBuf byteBuf = Unpooled.buffer(1024);
        byteBuf.writeBytes("abcd".getBytes());

        array = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0, array);
        out.println(new String(array));
        out.println(byteBuf);


        int i = byteBuf.readInt();
        out.println(i);
        out.println(byteBuf);


        byteBuf.discardReadBytes();
        out.println(byteBuf);

        StringBuilder sb = new StringBuilder();
        for(int j = 0; j < 100; j++){
            sb.append("1234567890");
        }
        byteBuf.writeBytes(sb.toString().getBytes());




        ByteProcessor a = ByteProcessor.FIND_CRLF;
        ByteProcessor b = ByteProcessor.FIND_CR;



        /*i = byteBuf.readInt();
        out.println(i);
        out.println(byteBuf);*/


//        ByteBuf byteBuf = UnpooledHeapByteBuf
//        ByteBuf byteBuf = UnpooledDirectByteBuf





//        NioSocketChannel

        return;
    }
}
