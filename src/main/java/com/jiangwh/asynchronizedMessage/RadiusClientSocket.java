package com.jiangwh.asynchronizedMessage;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;


public class RadiusClientSocket {

	private DatagramSocket datagramSocket;
	private int port;

	public RadiusClientSocket(int port) {
		this.port = port;
		try {
			InetAddress ipv4 = Inet4Address.getLocalHost();
			this.datagramSocket = new DatagramSocket(port, ipv4);

		} catch (Exception e) {
			
		}
	}

	public RadiusClientSocket() {
		Integer port = 3930;
		int i = 0;
		for (i = 0; i < 10; i++) {
			try {
				InetAddress ipv4 = Inet4Address.getLocalHost();
				this.datagramSocket = new DatagramSocket(port, ipv4);
				break;
			} catch (Exception e) {
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
