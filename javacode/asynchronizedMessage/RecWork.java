
import java.net.DatagramPacket;
import java.util.Hashtable;

public class RecWork implements Runnable {

	@Override
	public void run() {
		Hashtable<String, MessageObject> messageTable = MessageCollection.getMessageTable();
		while(true){			
			DatagramPacket datagramPacket = new DatagramPacket(new byte[4096], 4096);
			try {
				RadiusClientSocket.getDatagramSocketInstance().receive(datagramPacket);
				PacketIdProcess packetProcess = MessageProcess.getProcessInstance().getPacketIdProcess();
				if(null==packetProcess){
					throw new NullPointerException("未注册获取报文ID方法");
				}else{
					String id = packetProcess.getPacketId(datagramPacket);
					if(messageTable.containsKey(id)){
						MessageObject messageObject = messageTable.get(id);
						messageObject.setResponseContent(datagramPacket);
						messageObject.setDone(true);
						synchronized (messageObject) {
							messageObject.notify();
						}
					}
				}
			} catch (Throwable e) {				
				e.printStackTrace();
			}
		}
	}	
}
