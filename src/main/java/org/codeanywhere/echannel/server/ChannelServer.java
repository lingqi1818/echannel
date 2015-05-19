package org.codeanywhere.echannel.server;

import io.netty.channel.Channel;

/**
 * 长连接通道服务器
 * 
 * @author chenke
 * @date 2015-5-18 下午12:53:37
 */
public interface ChannelServer {

    public void sendMsg(String[] uids, String msg);

    public void sendMsgToAll(String msg);

    public void start();

    public void stop();

    /**
     * 客户端长连接
     * 
     * @author chenke
     * @date 2015-5-19 上午11:32:37
     */
    public static class Connection {
        private Channel channel;
        private boolean isValidate = false;

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        public boolean isValidate() {
            return isValidate;
        }

        public void setValidate(boolean isValidate) {
            this.isValidate = isValidate;
        }

    }

    /**
     * 服务端发送给客户端的消息
     * 
     * @author chenke
     * @date 2015-5-19 上午11:35:32
     */
    public static class Message {
        public static enum MsgType {
            Auth,
            Normal
        }

        private MsgType msgType = MsgType.Normal;
        private boolean isSuccess;
        private String  failedResult;
        private String  body;

        public Message() {
            super();
        }

        public Message(boolean isSuccess) {
            super();
            this.isSuccess = isSuccess;
        }

        public MsgType getMsgType() {
            return msgType;
        }

        public void setMsgType(MsgType msgType) {
            this.msgType = msgType;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public void setSuccess(boolean isSuccess) {
            this.isSuccess = isSuccess;
        }

        public String getFailedResult() {
            return failedResult;
        }

        public void setFailedResult(String failedResult) {
            this.failedResult = failedResult;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public static Message createSuccessMessage() {
            return new Message(true);
        }

        public static Message createFailedMessage() {
            return new Message(false);
        }

    }

    /**
     * 连接权限校验器
     * 
     * @author chenke
     * @date 2015-5-19 下午3:22:19
     */
    public static interface ConnAuthValidator {

        public boolean validate(String uid, String pwd);

    }

}
