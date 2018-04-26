package Ch2Nio;

/**
 * @author: Bruce Zhao
 * @email: zhzh402@163.com
 * @date: 2018/3/18 21:19
 */
public class TimeServer {
    public static void main(String[] args){
        int port = 8080;

        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);

        new Thread(timeServer, "NIO-MultiplexerTimerServer").start();
    }
}
