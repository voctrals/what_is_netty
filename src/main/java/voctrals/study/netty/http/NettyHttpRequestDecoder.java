package voctrals.study.netty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequestDecoder;

/**
 *
 */
class NettyHttpRequestDecoder extends HttpRequestDecoder {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("NettyHttpRequestDecoder read");
        super.channelRead(ctx, msg);
    }

}
