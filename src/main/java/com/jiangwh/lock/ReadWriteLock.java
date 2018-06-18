package com.jiangwh.lock;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.google.common.util.concurrent.RateLimiter;

public class ReadWriteLock {

	static ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
	static Map<String, String> map;

	public static void main(String[] args) throws TimeoutException, InterruptedException {
//		TimeLimiter t= SimpleTimeLimiter.create(Executors.newCachedThreadPool());
//		
//		t.runWithTimeout(()->{
//			
//			long beg = System.currentTimeMillis();
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				
//			}
//			System.out.println((System.currentTimeMillis()-beg));
//		}, 1000, TimeUnit.MILLISECONDS);
		
		RateLimiter rateLimiter = RateLimiter.create(2);
		while(true){
			double token = rateLimiter.acquire();
			if(null==Double.valueOf(token)){
				break;
			}else{
				System.out.println(token);
			}
			
		}
		map = new HashMap<>();
		map.put("123", "1234");
		new Thread(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			WriteLock lock = reentrantReadWriteLock.writeLock();
			lock.lock();
			map.put("123", "123w");
			System.out.println("change");
			lock.unlock();
		}).start();
		new Thread(() -> {
			ReadLock lock = reentrantReadWriteLock.readLock();
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				
			}
//			lock.tryLock(timeout, unit)
			lock.lock();
			System.out.println(map.get("123"));
			lock.unlock();
		}).start();
		System.out.println("main over");
	}
}
