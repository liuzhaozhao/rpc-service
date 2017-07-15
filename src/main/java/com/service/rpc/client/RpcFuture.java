package com.service.rpc.client;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import com.service.rpc.transport.RpcRequest;
import com.service.rpc.transport.RpcResponse;

public class RpcFuture implements Future<Object> {
	private Sync sync;
	private RpcRequest request;
	private RpcResponse response;
	private Date startRequest;// 开始请求时间，用于计算响应超时
	
	public RpcFuture(int readTimeoutMills, RpcRequest request) {
//		this.readTimeoutMills = readTimeoutMills;
		this.request = request;
		this.sync = new Sync();
	}
	
	@Override
    public boolean isDone() {
        return sync.isDone();
    }
	
	@Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        return response;
    }
	
	/**
	 * 有些问题，sync.tryAcquireNanos不会等待，TODO:不要使用
	 */
	@Deprecated
	@Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (response == null || response.getResponseCode() != RpcResponse.CODE_SUCCESS) {
                return this.response.getData();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("Timeout exception. Request id: " + this.request.getRequestId());
        }
    }
	
	@Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }
	
	public void setResponse(RpcResponse response) {
		this.response = response;
		sync.release(1);
	}
	
//	public RpcResponse getResponse() {
//		return response;
//	}
	
	static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        //future status
        private final int done = 1;
        private final int pending = 0;

        protected boolean tryAcquire(int acquires) {
            return getState() == done ? true : false;
        }

        protected boolean tryRelease(int releases) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone() {
            getState();
            return getState() == done;
        }
    }
	
	public RpcRequest getRequest() {
		return request;
	}
	
	public void setStartRequest(Date startRequest) {
		this.startRequest = startRequest;
	}
	
	public Date getStartRequest() {
		return startRequest;
	}
}
