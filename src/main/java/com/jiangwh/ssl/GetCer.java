package com.jiangwh.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class GetCer {
	
	public static void main(String[] args) {
		System.out.println(GetCer.class.isInstance(new GetCer()));
		System.out.println(new GetCer() instanceof GetCer);
	}
	public static KeyStore getKeyStore(String host, int dport) throws KeyStoreException, NoSuchAlgorithmException,
			KeyManagementException, CertificateException, IOException {

		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(null, null);

		SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLContext sslContext = SSLContext.getInstance("SSL");
		X509TrustManager[] tm = { new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
				// Trust anything
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
				// Trust anything
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		} };
		SecureRandom secureRandom = new SecureRandom();
		sslContext.init(null, tm, secureRandom);
		sslSocketFactory = sslContext.getSocketFactory();
		// get ip addrs by domain
		InetAddress[] dsts = InetAddress.getAllByName(host);
		for (InetAddress inetAddress : dsts) {
			InetSocketAddress sockertAddress = new InetSocketAddress(inetAddress, dport);
			try {
				Socket socket = SocketFactory.getDefault().createSocket();
				socket.setSoTimeout(3 * 1000);
				socket.connect(sockertAddress, 3 * 1000);
				// if you want to set the connect param to the sslsocket
				SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(socket, sockertAddress.getHostString(),
						sockertAddress.getPort(), false);
				// SSLSocket sslSocket = (SSLSocket)
				// sslSocketFactory.createSocket(sockertAddress.getHostString(),sockertAddress.getPort());
				SSLSession sess = sslSocket.getSession();
				X509Certificate[] certs = (X509Certificate[]) sess.getPeerCertificates();
				sess.invalidate();
				if (1 == certs.length) {
					// need fixed the Certificate chain
					keyStore.setCertificateEntry("tmptrust", certs[0]);
					return keyStore;
				}
			} catch (IOException ioe) {
				continue;
			}
		}
		return keyStore;

	}
}
