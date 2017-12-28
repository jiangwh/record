
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;


public class SendWork implements Runnable  {
	private Logger logger = Logger.getLogger(SendWork.class);	
	@Override
	public void run() {
		BlockingQueue<MessageObject> messageQueue = MessageCollection.getMessageQueue();
		while(true){			
			try {
				MessageObject message = messageQueue.take();
				DatagramPacket packet = message.getRequestContent();
				if(null!=packet){
					try {
						RadiusClientSocket.getDatagramSocketInstance().send(packet);
					} catch (IOException ioException) {
						message.setTryNum(message.getTryNum()+1);
						logger.error(ioException);
						RadiusClientSocket.reset();
					}
				}							
			} catch (Throwable e) {				
				e.printStackTrace();
			}
		}
	}
}
