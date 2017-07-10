package test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
	private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
	
	public static void main(String[] args) throws Exception {
//		Server.start(8080, TestService.class);
		Main main = new Main();
//		Thread t1 = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					main.waitingForHandler();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		});
//		Thread t2 = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				main.signalAvailableHandler();
//			}
//		});
//		t2.start();
//		t1.start();
//		t1.join();
//		t2.join();
		
//		main.waitingForHandler();
//		main.signalAvailableHandler();
		
		
		
//		MethodInfo methodInfo = HttpMethod.getMethodInfo("/test_1/123", "post");
//		Object data = null;
//		if(methodInfo != null) {
//			data = methodInfo.invoke(new Object[]{123, "arg", 456, new DataBean<Integer>("msg")});
//		}
//		System.err.println(">>>>>>>>>"+data);
//		Object obj = "12qq3";
//		Object obj = new DataBean<Integer>("error msg");
//		IJson json = new FastJson();
//		System.err.println(json.toStr(obj));
		
//		System.err.println(new String(json.toByte(obj)));
		
	}
	
	private void signalAvailableHandler() {
		System.err.println("enter 1");
        lock.lock();
        System.err.println("enter 1 lock");
        try {
            connected.signalAll();
            System.err.println("enter 1 weak up");
        } finally {
            lock.unlock();
            System.err.println("enter 1 unlock");
        }
    }

    private boolean waitingForHandler() throws InterruptedException {
    	System.err.println("enter 2");
        lock.lock();
        System.err.println("enter 2 lock");
        try {
        	System.err.println("enter 2 await");
            return connected.await(6000, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
            System.err.println("enter 2 unlock");
        }
    }
}
