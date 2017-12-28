package com.jiangwh.asynchronizedMessage;
import java.util.ArrayList;
import java.util.List;

public class PacketIdentifier {

	private List<String> lq = new ArrayList<String>();
	private byte start=0;
	
	
	public synchronized String getReuqestId() {
		
		for (int i = (start&0xff); i <= 0xff; i++) {
			String id = String.valueOf(i);
			if (!lq.contains(id)) {	
				start=(byte) i;
				lq.add(id);
				return id;
			}
		}
		for (int i = 0; i<(start&0xff); i++) {
			String id = String.valueOf(i);
			if (!lq.contains(id)) {		
				start=(byte) i;
				lq.add(id);
				return id;
			}
		}
		try {
			this.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "";
	}

	public synchronized void removeRquestId(String i) {
		lq.remove(String.valueOf(i));
		this.notifyAll();
	}

	public static PacketIdentifier getInstance() {
		return PackagetIdentifierInstance.identifier;
	}

	private PacketIdentifier() {}

	static class PackagetIdentifierInstance {
		static PacketIdentifier identifier = new PacketIdentifier();
	}
	
	public String getReqId(){
		String requestId = "";
		while ("".equals(requestId = this.getReuqestId()));
		return requestId;
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 2768; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					String i = PacketIdentifier.getInstance().getReqId();
					System.out.print(i+" ");
					if(Integer.parseInt(i)%40==0){
						System.out.println();
					}
					try {
						Thread.sleep((long) (Math.random()*100) );
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PacketIdentifier.getInstance().removeRquestId(i);
				}
				
			}).start();
		}
		
	}
}
