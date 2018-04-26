package Ch2Aio;

/**
 * @author: Bruce Zhao
 * @email: zhzh402@163.com
 * @date: 2018/3/18 21:19
 */
public class TimeServer {
    public static void main(String[] args){
        int port = 8080;

        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread(timeServer, "AIO-AsyncTimeServer").start();
    }
}
