package voctrals.study.netty.longConn;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 解码器
 */
public class MyDecoder extends ReplayingDecoder<MyDecoder.LiveState> {

    public enum LiveState {
        TYPE,
        LENGTH,
        CONTENT
    }

    private MyTransmit message;

    public MyDecoder() {
        super(LiveState.TYPE);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        LiveState state = state();
        System.out.println("state:" + state + " message:" + message);
        switch (state) {
            case TYPE:
                message = new MyTransmit();
                byte type = byteBuf.readByte();
                System.out.println("type:" + type);
                message.setType(type);
                checkpoint(LiveState.LENGTH);
                break;
            case LENGTH:
                int length = byteBuf.readInt();
                message.setLength(length);
                if (length > 0) {
                    checkpoint(LiveState.CONTENT);
                } else {
                    list.add(message);
                    checkpoint(LiveState.TYPE);
                }
                break;
            case CONTENT:
                byte[] bytes = new byte[message.getLength()];
                byteBuf.readBytes(bytes);
                String content = new String(bytes);
                message.setContent(content);
                list.add(message);
                checkpoint(LiveState.TYPE);
                break;
            default:
                throw new IllegalStateException("invalid state:" + state);
        }
        System.out.println("end state:" + state + " list:" + list);
    }

}