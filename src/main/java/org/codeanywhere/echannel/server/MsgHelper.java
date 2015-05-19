package org.codeanywhere.echannel.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import org.codeanywhere.echannel.server.ChannelServer.Message;

import com.alibaba.fastjson.JSON;

public class MsgHelper {

    public static void writeFaildMsgToClient(ByteBuf buf, ChannelHandlerContext ctx,
                                             String failedResult) {
        Message msg = Message.createFailedMessage();
        msg.setFailedResult(failedResult);
        buf.writeBytes(JSON.toJSONString(msg).getBytes());
        buf.writeBytes("\r\n".getBytes());
        ctx.write(buf);
        ctx.flush();
    }

}
