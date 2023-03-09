package voctrals.study.netty.longConn;

import io.netty.channel.Channel;
import io.netty.util.concurrent.ScheduledFuture;

/**
 * 一个channel对应一个scheduleFuture，不用的时候及时关闭链接
 */
public class MyChannelCache {

    Channel channel;
    ScheduledFuture<?> scheduledFuture;

    public MyChannelCache(Channel channel, ScheduledFuture<?> scheduledFuture) {
        this.channel = channel;
        this.scheduledFuture = scheduledFuture;
    }

}
