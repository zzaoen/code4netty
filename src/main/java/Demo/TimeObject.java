package Demo;

import java.util.Date;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 2018/4/25 21:30
 * @desc :
 */
public class TimeObject {

    private final long value;

    public TimeObject(){
        this(System.currentTimeMillis() / 1000L + 2208988800L);
    }

    public TimeObject(long value){
        this.value = value;
    }

    public long value(){
        return value;
    }

    @Override
    public String toString() {
        return new Date((value() - 2208988800L) * 1000L).toString();
    }
}
