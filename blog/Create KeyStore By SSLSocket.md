public static KeyStore getKeyStore(String host, int dport) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, CertificateException, IOException  {
		
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
				if(1==certs.length) {
					//need fixed the Certificate chain
					keyStore.setCertificateEntry("tmptrust", certs[0]);
					return keyStore;
				}				
			} catch (IOException ioe) {
				continue;
			}
		}
		return keyStore;

	}