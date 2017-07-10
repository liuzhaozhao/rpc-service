package com.service.rpc.client.netty;

import com.service.rpc.serialize.ISerialize;
import com.service.rpc.transport.RpcRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 对象转字节
 * @author liuzhao
 *
 */
public class ClientEncoder extends MessageToByteEncoder<Object> {
	private ISerialize serialize;
	
	public ClientEncoder(ISerialize serialize) {
		this.serialize = serialize;
	}

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
    	if(serialize.isJson() && in instanceof RpcRequest) {
    		RpcRequest data = (RpcRequest)in;
    		Object[] oldParams = data.getArgs();
    		String[] paramsJson = new String[oldParams==null?0:oldParams.length];
    		if(oldParams != null) {
    			for(int i=0; i<oldParams.length; i++) {
    				paramsJson[i] = serialize.toStr(oldParams[i]);
    			}
    		}
    		data.setArgs(paramsJson);
    	}
        byte[] data = serialize.toByte(in);
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
