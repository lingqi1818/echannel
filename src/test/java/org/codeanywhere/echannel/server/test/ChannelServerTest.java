package org.codeanywhere.echannel.server.test;

import org.codeanywhere.echannel.server.ChannelServer;
import org.codeanywhere.echannel.server.ChannelServer.ConnAuthValidator;
import org.codeanywhere.echannel.server.ChannelServerImpl;

public class ChannelServerTest {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //        Message msg = new Message(true);
        //        msg.setMsgType(MsgType.Auth);
        //        System.out.println(JSON.toJSON(msg));
        //Message message = JSON.parseObject("{}", Message.class);
        //System.out.println(message.getMsgType());
        ChannelServer server = new ChannelServerImpl(7001, new ConnAuthValidator() {
            @Override
            public boolean validate(String uid, String pwd) {
                System.out.println("uid:" + uid);
                System.out.println("pwd:" + pwd);
                return true;
            }
        });

        server.start();
        Thread.sleep(10000);
        server.sendMsg(new String[] { "ck" }, "thank u!");
        server.sendMsgToAll("thank u!");
        Thread.sleep(10000);
        System.out.println("haha");
    }

}
