package com.service.rpc.client.netty;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;

import com.service.rpc.client.ServiceProxy;
import com.service.rpc.serialize.ISerialize;
import com.service.rpc.transport.RpcResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 字节转对象
 * @author liuzhao
 *
 */
public class ClientDecoder extends ByteToMessageDecoder {
	private Logger log = Logger.getLogger(this.getClass());
	private ISerialize serialize;
	
	public ClientDecoder(ISerialize serialize) {
		this.serialize = serialize;
	}

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        /*if (dataLength <= 0) {
            ctx.close();
        }*/
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        RpcResponse rpcData = serialize.toBean(data, RpcResponse.class);
        if(rpcData.getData() != null && serialize.isJson()) {
        	Method method = ServiceProxy.getMethod(rpcData.getMethodIdentify());
        	if(method == null) {
        		log.warn("无法获取method");
        	} else {
        		rpcData.setData(serialize.toBean(rpcData.getData().toString(), method.getGenericReturnType()));
        	}
        }
        out.add(rpcData);
    }

}
