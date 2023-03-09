package voctrals.study.netty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 *
 */
class NettyHttpResponseEncoder extends HttpResponseEncoder {


    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        System.out.println("NettyHttpResponseEncoder read");
        super.read(ctx);
    }
}
