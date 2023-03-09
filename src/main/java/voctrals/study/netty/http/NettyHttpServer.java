package voctrals.study.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * HttpServer
 */
public class NettyHttpServer {

    private final int port;

    public NettyHttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        NettyHttpServer nettyHttpServer = new NettyHttpServer(8899);
        nettyHttpServer.start();
    }

    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                System.out.println("initChannel ch:" + ch);
                                ch.pipeline()
                                        // HttpRequestDecoder用于解码request
                                        .addLast("decoder", new NettyHttpRequestDecoder())
                                        // HttpResponseEncoder用于编码response
                                        .addLast("encoder", new NettyHttpResponseEncoder())
                                        // 为什么能有FullHttpRequest这个东西，就是因为有他，HttpObjectAggregator
                                        // 如果没有他，就不会有那个消息是FullHttpRequest的那段Channel，同样也不会有FullHttpResponse。
                                        .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                        // ** 正儿八经的业务处理器
                                        .addLast("handler", new NettyHttpHandler());
                            }
                        }
                )
                .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .bind(port)
                .sync();

    }

}


