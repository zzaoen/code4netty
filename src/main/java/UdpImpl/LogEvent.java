package UdpImpl;

import java.net.InetSocketAddress;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/6/3 19:40
 * @desc :
 */
public class LogEvent {
    public static final byte SEPARATOR = (byte) '-';

    public final InetSocketAddress source;
    public final String logfile;
    public final String msg;
    private final long reveived;

    public LogEvent(String logfile, String msg){
        this(null, -1, logfile, msg);
    }

    public LogEvent(InetSocketAddress source, long reveived, String logfile, String msg){
        this.source = source;
        this.reveived = reveived;
        this.logfile = logfile;
        this.msg = msg;
    }

    public static byte getSEPARATOR() {
        return SEPARATOR;
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public String getLogfile() {
        return logfile;
    }

    public String getMsg() {
        return msg;
    }

    public long getReveived() {
        return reveived;
    }
}
