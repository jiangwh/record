package com.jiangwh.asynchronizedMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;



public class MessageSend {

	
	public DatagramPacket send(DatagramPacket datagramPacket,long timeOut,int tryNum)
			throws InterruptedException, TimeOutException {
		MessageObject messageObject = new MessageObject();		
		// send
		DatagramPacket res = null;
		while (messageObject.getTryNum() < tryNum) {
			messageObject.setTryNum(messageObject.getTryNum() + 1);
			try {
				res = doSend(messageObject, datagramPacket,timeOut);
			} catch (Exception e) {
				
			}
		}
		return res;
	}
	
	public DatagramPacket doSend(MessageObject messageObject,
			DatagramPacket datagramPacket,long timeOut)
			throws TimeOutException, InterruptedException {
		Hashtable<String, MessageObject> messageTable = MessageCollection.getMessageTable();
		BlockingQueue<MessageObject> messageQueue = MessageCollection.getMessageQueue();
		messageObject.setRequestContent(datagramPacket);
		String requestId = MessageProcess.getProcessInstance().getPacketIdProcess().getPacketId(datagramPacket);
		if (null != requestId  && !"".equals(requestId))
			messageTable.put(requestId, messageObject);
		messageQueue.add(messageObject);
		try {
			synchronized (messageObject) {
				messageObject.wait(timeOut);
			}
			if (messageObject.isDone()) {
				return messageObject.getResponseContent();
			} else {
				throw new TimeOutException("Time is out : "+ MessageProcessConstan.TIME_OUT + " ms");
			}
		} finally {
			if (null != requestId && !"".equals(requestId)) {
				messageTable.remove(requestId);
				PacketIdentifier.getInstance().removeRquestId(requestId);
			}
		}
	}
	
	public DatagramPacket send(DatagramPacket datagramPacket)
			throws InterruptedException, TimeOutException {
		MessageObject messageObject = new MessageObject();
		// send
		DatagramPacket res = null;
		while (messageObject.getTryNum() < MessageProcessConstan.MAX_TRY) {
			messageObject.setTryNum(messageObject.getTryNum() + 1);
			try {
				res = doSend(messageObject, datagramPacket);
				//check response
			} catch (Exception e) {
				
			}
		}
		return res;
	}

	public DatagramPacket doSend(MessageObject messageObject,
			DatagramPacket datagramPacket)
			throws TimeOutException, InterruptedException {
		Hashtable<String, MessageObject> messageTable = MessageCollection.getMessageTable();
		BlockingQueue<MessageObject> messageQueue = MessageCollection.getMessageQueue();
		messageObject.setRequestContent(datagramPacket);
		String requestId = MessageProcess.getProcessInstance().getPacketIdProcess().getPacketId(datagramPacket);
		if (null != requestId  && !"".equals(requestId))
			messageTable.put(requestId, messageObject);
		messageQueue.add(messageObject);
		try {
			synchronized (messageObject) {
				messageObject.wait(MessageProcessConstan.TIME_OUT);
			}
			if (messageObject.isDone()) {				
				return messageObject.getResponseContent();
			} else {
				throw new TimeOutException("Time is out : "+ MessageProcessConstan.TIME_OUT + " ms");
			}
		} finally {
			if (null != requestId && !"".equals(requestId)) {
				messageTable.remove(requestId);
				PacketIdentifier.getInstance().removeRquestId(requestId);
			}
		}
	}

	private MessageSend() {
		MessageProcess.getProcessInstance().RegisterPacketProcess(
			new PacketIdProcess() {
				@Override
				public String getPacketId(DatagramPacket datagramPacket) {
					byte[] data = null == datagramPacket ? null: datagramPacket.getData();
					if (null != data) {
						ByteBuffer buffer = ByteBuffer.allocate(data.length);
						buffer.put(data);
						buffer.flip();
						buffer.get();// code
						int id = buffer.get() & 0xff;
						return String.valueOf(id);
					}
					return null;
				}
			});
		
		MessageProcess.getProcessInstance().registerVarlidateResponse(new PacketVarlidate() {
			class Packet{
				byte code;
				byte identifier;
				int length;
				byte[] authenticator;
				byte[] attributes;
				public byte getCode() {return code;}
				public void setCode(byte code) {this.code = code;}
				public byte getIdentifier() {return identifier;}
				public void setIdentifier(byte identifier) {this.identifier = identifier;}
				public int getLength() {return length;}
				public void setLength(int length) {this.length = length;}
				public byte[] getAuthenticator() {return authenticator;}
				public void setAuthenticator(byte[] authenticator) {this.authenticator = authenticator;}
				public byte[] getAttributes() {	return attributes;}
				public void setAttributes(byte[] attributes) {this.attributes = attributes;}				
			}
			private boolean authenticatorCheck(Packet packet,byte[] reqAuthenticator,String key) throws NoSuchAlgorithmException, UnsupportedEncodingException{
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.update(packet.getCode());
				md5.update(packet.getIdentifier());
				md5.update((byte)(packet.getLength() >> 8));
				md5.update((byte)(packet.getLength() & 0x0ff));
				md5.update(reqAuthenticator);
				md5.update(packet.getAttributes());
				md5.update(key.getBytes("UTF-8"));				
				return Arrays.equals(packet.getAuthenticator(), md5.digest());
			}
			private Packet packetParse(DatagramPacket datagramPacket) throws IOException{
				byte[] packetData = datagramPacket.getData();
				ByteArrayInputStream bais = new ByteArrayInputStream(packetData);
				int code = bais.read();
				int identifier = bais.read();
				int length = bais.read() << 8 | bais.read();
				byte[] authenticator = new byte[16];
				byte[] attributes = new byte[length - 32];
				bais.read(authenticator);
				bais.read(attributes);
				Packet packet = new Packet();
				packet.setCode((byte)code);
				packet.setIdentifier((byte)identifier);
				packet.setLength(length);
				packet.setAuthenticator(authenticator);
				packet.setAttributes(attributes);
				return packet;
			}
			@Override
			public boolean varlidatePacket(DatagramPacket datagramPacket,
					String key, byte[] reqAuthenticator) {
				try {
					Packet packet = packetParse(datagramPacket);
					return authenticatorCheck(packet, reqAuthenticator, key);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		});
		ThreadGroup group = new ThreadGroup("dm-coa");
		Thread send = new Thread(group,new SendWork());
		send.setName("dm-coa-send");
		send.setDaemon(true);		
		send.start();
		Thread rec = new Thread(group,new RecWork());
		rec.setName("dm-coa-rec");
		rec.setDaemon(true);		
		rec.start();
	}

	public static MessageSend getInstance() {
		return MessageSendInstance.messageSend;
	}

	static class MessageSendInstance {
		static MessageSend messageSend = new MessageSend();
	}

}
