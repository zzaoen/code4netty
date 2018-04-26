package Ch2Nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author: Bruce Zhao
 * @email: zhzh402@163.com
 * @date: 2018/3/18 21:21
 */
public class MultiplexerTimeServer implements Runnable{
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try{
            selector = Selector.open(); //创建多路复用器Selector
            serverSocketChannel = ServerSocketChannel.open(); //创建ServerSocketChannel

            serverSocketChannel.configureBlocking(false); //设置为异步非阻塞模式
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); //将channel注册到Selector，监听ACCEPT操作位

            System.out.println("NIO TimeServer is start in port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void stop(){
        this.stop = true;
    }

    @Override
    public void run() {
        while(!stop){
            try{
                selector.select(1000); // selector的休眠时间是1s，每隔1s被唤醒一次
                Set<SelectionKey> selectedKeys = selector.selectedKeys(); //轮询遍历selector
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;

                while(it.hasNext()){
                    key = it.next();
                    it.remove();
                    try{
                        handleInput(key);
                    } catch (Exception e){
                        if(key != null){
                            key.cancel();
                            if(key.channel() != null)
                                key.channel().close();
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Throwable e){
                e.printStackTrace();
            }
        }

        if(selector != null){
            try{
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException{
        if(key.isValid()){
            if(key.isAcceptable()){
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);

                sc.register(selector, SelectionKey.OP_READ);
            }

            if(key.isReadable()){
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024); //1MB的缓冲区
                int readBytes = sc.read(readBuffer);
                if(readBytes > 0){
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];

                    readBuffer.get(bytes); //把ReadBuffer中数据读到byte数组中

                    String body = new String(bytes, "utf-8");
                    System.out.println("NIO TimeServer receive order: " + body);

                    String currentTime = "Query Time Order".equalsIgnoreCase(body) ? new java.util.Date(System.currentTimeMillis()).toString() : "Bad Order";
                    doWrite(sc, currentTime);
                }else if(readBytes < 0){
                    key.cancel();
                    sc.close();
                }else{
                    ;
                }
            } //end isReadable
        }// end isValid
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        if(response != null && response.trim().length() != 0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer); //SocketChannel是异步非阻塞的，不能保证一次能把需要发送的字符数组发送完。需要写操作，然后轮询Selector有没有发送完。
        }

    }


}
