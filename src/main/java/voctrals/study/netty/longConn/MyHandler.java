package voctrals.study.netty.longConn;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 消息处理器
 */
public class MyHandler extends SimpleChannelInboundHandler<MyTransmit> {

    public static final Map<Integer, MyChannelCache> channelCacheMap = new HashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyTransmit msg) throws Exception {

        Channel channel = ctx.channel();
        int channelHashcode = channel.hashCode();

        // channel过期的时候，回调
        channel.closeFuture().addListener(future -> {
            System.out.println("channel close, remove key:" + channelHashcode);
            channelCacheMap.remove(channelHashcode);
        });

        if (!channelCacheMap.containsKey(channelHashcode)) {
            // channel和scheduleFuture放一起
            MyChannelCache myChannelCache = new MyChannelCache(channel, ctx.executor().schedule(
                    () -> {
                        System.out.println("schedule runs, close channel:" + channelHashcode);
                        channel.close();
                    }, 10, TimeUnit.SECONDS));
            channelCacheMap.put(channelHashcode, myChannelCache);
        }

        switch (msg.getType()) {
            case MyTransmit.TYPE_HEART: {
                MyChannelCache cache = channelCacheMap.get(channelHashcode);
                ScheduledFuture scheduledFuture = ctx.executor().schedule(() -> channel.close(), 5, TimeUnit.SECONDS);
                cache.scheduledFuture.cancel(true);
                cache.scheduledFuture = scheduledFuture;
                ctx.channel().writeAndFlush(msg);
                break;
            }
            case MyTransmit.TYPE_MESSAGE: {
                channelCacheMap.entrySet().stream().forEach(entry -> {
                    Channel otherChannel = entry.getValue().channel;
                    otherChannel.writeAndFlush(msg);
                });
                break;
            }
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete");
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
        if (null != cause) cause.printStackTrace();
        if (null != ctx) ctx.close();
    }

}
