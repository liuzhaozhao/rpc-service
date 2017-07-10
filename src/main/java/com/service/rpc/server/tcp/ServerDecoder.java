package com.service.rpc.server.tcp;

import java.util.List;

import org.apache.log4j.Logger;

import com.service.rpc.serialize.ISerialize;
import com.service.rpc.transport.RpcRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 服务器端字节转对象（收到消息时执行）
 * @author liuzhao
 *
 */
public class ServerDecoder extends ByteToMessageDecoder {
	private Logger log = Logger.getLogger(this.getClass());
	
	private ISerialize serialize;
	
	public ServerDecoder(ISerialize serialize) {
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

        RpcRequest rpcData = serialize.toBean(data, RpcRequest.class);
        if(rpcData.getArgs() != null && serialize.isJson()) {// 如果是json序列化，则参数数据为json字符串，需要再次转换json字符串为对应类型的对象
        	MethodInfo methodInfo = RpcServer.server.getMethodInfo(rpcData.getMethodIdentify());
        	if(methodInfo == null || rpcData.getArgs().length != methodInfo.getMethodParams().size()) {
        		log.warn("无法获取method");
        	} else {
        		List<MethodParam> methodParams = methodInfo.getMethodParams();
        		Object[] params = new Object[methodParams.size()];
        		Object[] args = rpcData.getArgs();
        		for(int i=0; i<methodParams.size(); i++) {
        			params[i] = serialize.toBean(args[i].toString(), methodParams.get(i).getType());
        		}
        		rpcData.setArgs(params);
        	}
        }
        out.add(rpcData);
    }

}
