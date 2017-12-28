package com.jiangwh.asynchronizedMessage;

import java.net.DatagramPacket;
import java.util.Hashtable;

public class RecWork implements Runnable {

	@Override
	public void run() {
		Hashtable<String, MessageObject> messageTable = MessageCollection.getMessageTable();
		while (true) {
			DatagramPacket datagramPacket = new DatagramPacket(new byte[4096], 4096);
			try {
				RadiusClientSocket.getDatagramSocketInstance().receive(datagramPacket);
				PacketIdProcess packetProcess = MessageProcess.getProcessInstance().getPacketIdProcess();
				if (null == packetProcess) {
					throw new NullPointerException("");
				} else {
					String id = packetProcess.getPacketId(datagramPacket);
					if (messageTable.containsKey(id)) {
						MessageObject messageObject = messageTable.get(id);
						messageObject.setResponseContent(datagramPacket);
						messageObject.setDone(true);
						// if(null!=messageObject.getCallBack()){
						// messageObject.call();
						// }else{
						synchronized (messageObject) {
							messageObject.notify();
							// }
						}
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
