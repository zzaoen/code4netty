package Ch8Protobuf.proto;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.lang.System.out;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/3/25 21:00
 * @desc :
 */
public class UserInfo implements Serializable{
    private static final long serialVersionUID = 1L;
    private String userName;

    public void setUserName(String userName){
        this.userName = userName;
    }

    public byte[] codeSize(){
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        out.println(buffer.remaining());

        byte[] value = this.userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.flip();

        value = null;
        out.println(buffer.remaining());
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    public static void main(String[] args){
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("zhaozhao");
        out.println(Arrays.toString(userInfo.codeSize()));
    }
}
