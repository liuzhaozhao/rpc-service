package com.service.rpc.server.rpc.netty;

import com.service.rpc.serialize.ISerialize;
import com.service.rpc.transport.RpcResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 对象转字节，服务器返回消息时执行
 * @author liuzhao
 *
 */
public class ServerEncoder extends MessageToByteEncoder<Object> {
	private ISerialize serialize;
	
	public ServerEncoder(ISerialize serialize) {
		this.serialize = serialize;
	}

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
    	if(serialize.isJson() && in instanceof RpcResponse) {// 如果是json序列化，则将数据部分再做一次序列化，否则会在反序列化时数据序列化类型错误
    		RpcResponse response = (RpcResponse)in;
    		Object data = response.getData();
    		if(data != null) {
    			response.setData(serialize.toStr(data));
    		}
    	}
        byte[] data = serialize.toByte(in);
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
