
import java.net.DatagramPacket;

public class MessageObject {

	private boolean done = false;
	private DatagramPacket requestContent;
	private DatagramPacket responseContent;
	private int tryNum=0;

	public int getTryNum() {
		return tryNum;
	}

	public void setTryNum(int tryNum) {
		this.tryNum = tryNum;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
	
	public DatagramPacket getRequestContent() {
		return requestContent;
	}
	
	public DatagramPacket getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(DatagramPacket responseContent) {
		this.responseContent = responseContent;
	}

	public void setRequestContent(DatagramPacket requestContent) {
		this.requestContent = requestContent;
	}
}
