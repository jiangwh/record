package com.jiangwh.asynchronizedMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;

public class SendWork implements Runnable {
	
	@Override
	public void run() {
		BlockingQueue<MessageObject> messageQueue = MessageCollection.getMessageQueue();
		while (true) {
			try {
				MessageObject message = messageQueue.take();
				DatagramPacket packet = message.getRequestContent();
				if (null != packet) {
					try {
						RadiusClientSocket.getDatagramSocketInstance().send(packet);
					} catch (IOException ioException) {
						message.setTryNum(message.getTryNum() + 1);
						RadiusClientSocket.reset();
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
