import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageCollection {
	
	public static BlockingQueue<MessageObject> getMessageQueue() {
		return SingleMessageQueue.messageQueue;
	}

	public static Hashtable<String, MessageObject> getMessageTable() {
		return SingleMessageQueue.messageTable;
	}

	static class SingleMessageQueue {
		public static BlockingQueue<MessageObject> messageQueue = new LinkedBlockingQueue<MessageObject>();
		public static Hashtable<String, MessageObject> messageTable = new Hashtable<String, MessageObject>();
	}
}
