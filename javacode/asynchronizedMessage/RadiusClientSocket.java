
import java.net.DatagramSocket;
import java.net.InetAddress;


public class RadiusClientSocket {

	private DatagramSocket datagramSocket;
	private int port;

	public RadiusClientSocket(int port) {
		this.port = port;
		try {
			InetAddress ipv4 = InetAddress.getByName(SystemUtil.getBindAddress());// ֱ�Ӱ����õķ�����IP
			this.datagramSocket = new DatagramSocket(port, ipv4);

		} catch (Exception e) {
			RgsplLogger.error(e);

		}
	}

	public RadiusClientSocket() {
		Integer port = 3930;
		int i = 0;
		for (i = 0; i < 10; i++) {// ��ʼ�˿�Ϊ3930,�_ײʮ��,����3930-3939,�������ռ��,���޷�����
			try {
				InetAddress ipv4 = InetAddress.getByName(SystemUtil.getBindAddress());// ֱ�Ӱ����õķ�����IP
				this.datagramSocket = new DatagramSocket(port, ipv4);
				break;
			} catch (Exception e) {
				RgsplLogger.error(e);
				port++;
			}
		}
	}	
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static void reset(){
		RadiusClientSocketInstance.radiusClientSocket = new RadiusClientSocket();
	}
	public static DatagramSocket getDatagramSocketInstance(){
		return RadiusClientSocketInstance.radiusClientSocket.datagramSocket;
	}
	
	static class RadiusClientSocketInstance{
		static RadiusClientSocket radiusClientSocket = new RadiusClientSocket();
	}

}
