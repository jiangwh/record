
import java.net.DatagramSocket;
import java.net.InetAddress;


public class RadiusClientSocket {

	private DatagramSocket datagramSocket;
	private int port;

	public RadiusClientSocket(int port) {
		this.port = port;
		try {
			InetAddress ipv4 = InetAddress.getByName(SystemUtil.getBindAddress());// 直接绑定配置的服务器IP
			this.datagramSocket = new DatagramSocket(port, ipv4);

		} catch (Exception e) {
			RgsplLogger.error(e);

		}
	}

	public RadiusClientSocket() {
		Integer port = 3930;
		int i = 0;
		for (i = 0; i < 10; i++) {// 初始端口为3930,沖撞十次,即是3930-3939,如果都被占用,则无法启动
			try {
				InetAddress ipv4 = InetAddress.getByName(SystemUtil.getBindAddress());// 直接绑定配置的服务器IP
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
