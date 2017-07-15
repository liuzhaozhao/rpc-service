package test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Lock {
	private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    
    public static void main(String[] args) throws InterruptedException {
		Lock lock = new Lock();
		Thread t = new Thread(){
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock.weakupWaitConnect();
			}
		};
		t.start();
		System.err.println(lock.waitConnect());
	}
    
	private void weakupWaitConnect() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitConnect() throws InterruptedException {
        lock.lock();
        try {
            return connected.await(3000, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }
}
