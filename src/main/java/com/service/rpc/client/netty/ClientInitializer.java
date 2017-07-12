package com.service.rpc.client.netty;

import com.service.rpc.client.ServiceFactory;
import com.service.rpc.serialize.ISerialize;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        ISerialize serialize = ServiceFactory.getSerialize();
        cp.addLast(new ClientEncoder(serialize));
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
        cp.addLast(new ClientDecoder(serialize));
        cp.addLast(new ClientHandler());
    }
}
