package com.service.rpc.server.http.nettyChannel;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 初始化http handler
 * @author liuzhao
 *
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(1024*1024));//在处理 POST消息体时需要加上
//        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new HttpRequestHandler());
	}

}
