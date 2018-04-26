package Ch8Protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/3/26 12:07
 * @desc :
 */
public class TestSubscribeReqProto {
    private static byte[] encode(SubscribeReqProto.SubscribeReq req){
        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body){
        try {
            return SubscribeReqProto.SubscribeReq.parseFrom(body);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq(){
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("tom");
        builder.setProductName("java");

        builder.setAddress("earth");
        return builder.build();
    }

    public static void main(String[] args) throws IOException {
        SubscribeReqProto.SubscribeReq req = createSubscribeReq();
        System.out.println("Before encode:\n " + req.toString());

        SubscribeReqProto.SubscribeReq req2 = decode(encode(req));
        System.out.println("After decode:\n " + req.toString());

        System.out.println(req2.equals(req));

        System.out.println(req2.toByteArray().length);

        /*ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oostream = new ObjectOutputStream(bos);
        oostream.writeObject(req);
        oostream.flush();
        oostream.close();
        System.out.println(bos.toByteArray().length);*/
    }
}
