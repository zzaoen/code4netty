package Ch2Nio;

/**
 * @author: Bruce Zhao
 * @email: zhzh402@163.com
 * @date: 2018/3/19 20:15
 */
public class TimeClient {
    public static void main(String[] args){
        int port = 8080;

        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient").start();
    }
}
