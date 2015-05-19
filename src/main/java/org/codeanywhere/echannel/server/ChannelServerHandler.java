package org.codeanywhere.echannel.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codeanywhere.echannel.server.ChannelServer.ConnAuthValidator;
import org.codeanywhere.echannel.server.ChannelServer.Connection;
import org.codeanywhere.echannel.server.ChannelServer.Message;
import org.codeanywhere.echannel.server.ChannelServer.Message.MsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ChannelServerHandler extends ChannelInboundHandlerAdapter {

    private ConnAuthValidator   connAuthValidator;

    Map<String, Connection>     connections;

    private final static Logger logger = LoggerFactory.getLogger(ChannelServerHandler.class);

    public ChannelServerHandler(ConnAuthValidator connAuthValidator,
                                Map<String, Connection> connections) {
        super();
        this.connAuthValidator = connAuthValidator;
        this.connections = connections;

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            Message message = JSON.parseObject((String) msg, Message.class);
            if (message == null) {
                logger.error("the receive message is null:" + msg);
                MsgHelper.writeFaildMsgToClient(ctx.alloc().buffer(), ctx,
                        "the receive message is null.");
                return;
            }
            if (message.getMsgType() == MsgType.Auth) {
                if (StringUtils.isEmpty(message.getBody())) {
                    logger.error("validate connection failed:" + msg);
                    MsgHelper.writeFaildMsgToClient(ctx.alloc().buffer(), ctx,
                            "validate connection failed.");
                    return;
                }

                JSONObject auth = JSON.parseObject(message.getBody());
                String uid = auth.getString("uid");
                String pwd = auth.getString("pwd");
                Connection conn = new Connection();
                conn.setChannel(ctx.channel());
                if (!connAuthValidator.validate(uid, pwd)) {
                    logger.error("validate connection failed:" + msg);
                    MsgHelper.writeFaildMsgToClient(ctx.alloc().buffer(), ctx,
                            "validate connection failed.");
                    return;

                }
                conn.setValidate(true);
                connections.put(uid, conn);
            }
        } catch (Exception ex) {
            logger.error("parse json object failed:" + msg);
            MsgHelper.writeFaildMsgToClient(ctx.alloc().buffer(), ctx, "parse json object failed.");
        }
    }
}
