package org.codeanywhere.echannel.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelServerImpl implements ChannelServer, Runnable {

    private int                     port;

    private ConnAuthValidator       connAuthValidator;

    private EventLoopGroup          bossGroup;
    private EventLoopGroup          workerGroup;

    private final static Logger     logger      = LoggerFactory.getLogger(ChannelServerImpl.class);

    private Thread                  run;

    private Map<String, Connection> connections = new ConcurrentHashMap<String, Connection>();

    public ChannelServerImpl(int port, ConnAuthValidator connAuthValidator) {
        this.port = port;
        this.connAuthValidator = connAuthValidator;
    }

    @Override
    public void sendMsg(String[] uids, String msg) {
        if (uids == null || uids.length == 0 || StringUtils.isEmpty(msg)) {
            logger.error("the uids is null or the msg is null");
            return;
        }

        for (String uid : uids) {
            sendMsgToSingle(uid, msg);
        }
    }

    private void sendMsgToSingle(String uid, String msg) {
        Connection conn = connections.get(uid);
        if (conn == null) {
            logger.error("this uid connection is null:" + uid);
            return;
        }

        if (!conn.isValidate()) {
            logger.error("this uid connection is not validate:" + uid);
            return;
        }

        Channel channel = conn.getChannel();

        System.out.println("uid"+uid);
        ByteBuf buf = channel.alloc().buffer();
        buf.writeBytes(msg.getBytes());
        channel.write(buf);
        channel.flush();
    }

    @Override
    public void start() {
        run = new Thread(this);
        run.setDaemon(false);
        run.start();
    }

    @Override
    public void stop() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
        run.interrupt();
    }

    @Override
    public void sendMsgToAll(String msg) {
        Set<String> keys = connections.keySet();
        String[] uids = new String[keys.size()];
        keys.toArray(uids);
        sendMsg(uids, msg);
    }

    @Override
    public void run() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new JsonMsgDecoder(),
                                    new ChannelServerHandler(connAuthValidator, connections));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("netty thread interrypted :", e);
        }
    }

}
