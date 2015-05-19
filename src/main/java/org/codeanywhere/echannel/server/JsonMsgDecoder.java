package org.codeanywhere.echannel.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class JsonMsgDecoder extends ByteToMessageDecoder {
    private static byte JSON_PREFIX = 123; // {
    private static byte JSON_SUFIX  = 125; // }
    private static byte LF          = 10; // 换行
    private static byte CR          = 13; // 回车

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        ByteBuf tmp = ctx.alloc().buffer();
        if (in.isReadable()) {
            byte b = in.readByte();
            if (b != JSON_PREFIX) {
                MsgHelper.writeFaildMsgToClient(buf, ctx, "request msg is not json format .");
                return;
            }
        }

        if (in.readableBytes() < 3) {
            MsgHelper.writeFaildMsgToClient(buf, ctx, "request msg is not json format .");
            return;
        }

        int remainReadableBytes = 0;
        tmp.writeByte(JSON_PREFIX);
        while ((remainReadableBytes = in.readableBytes()) > 0) {
            if (remainReadableBytes == 3) {
                byte[] last = new byte[3];
                in.readBytes(last);
                if (last[0] != JSON_SUFIX || last[1] != CR || last[2] != LF) {
                    MsgHelper.writeFaildMsgToClient(buf, ctx, "request msg is not json format .");
                    return;
                }
            } else {
                tmp.writeByte(in.readByte());
            }
        }

        tmp.writeByte(JSON_SUFIX);

        byte[] bts = new byte[tmp.readableBytes()];
        tmp.getBytes(0, bts);
        out.add(new String(bts));
    }

}
