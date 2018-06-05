package Ch2Pio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Bruce Zhao
 * @email: zhzh402@163.com
 * @date: 2018/3/17 12:01
 * @desc: Pseudo-Asynchronized IO
 */

public class TimeServer {

    private static ExecutorService executorService = Executors.newFixedThreadPool(60);

    public static void main(String[] args) throws IOException {
        int port = 8080;


        ServerSocket server = null;
        try{
            server = new ServerSocket(port);
            System.out.println("Pseudo-Asynchronized IO Time server started in port: " + port);
            TimeServerHandlerExecutePool executePool = new TimeServerHandlerExecutePool(50, 1000);

            Socket socket = null;
            while (true){
                socket = server.accept();

                executorService.execute(new TimeServerHandler(socket));

//                executePool.execute(new TimeServerHandler(socket));
//                new Thread(new TimeServerHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(server != null){
                server.close();
                System.out.println("Time server closed");
                server = null;
            }
        }
    }
}
