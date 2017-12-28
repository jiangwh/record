```
	public static byte[] encodePapPassword(final byte[] userPass, final byte[] requestAuthenticator,
			final String sharedSecret) {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] userPassBytes = null;
		// password的长度必须是16的倍数,并且小于128位字节.
		// 如果不是16的倍数,补0填充.
		// 如果大于128位,截取前128位.

		if (userPass.length > 128) {
			userPassBytes = new byte[128];
			System.arraycopy(userPass, 0, userPassBytes, 0, 128);
		} else {
			userPassBytes = userPass;
		}

		// declare the byte array to hold the final product
		byte[] encryptedPass = null;

		if (userPassBytes.length < 128) {
			if (userPassBytes.length % 16 == 0) {
				// It is already a multiple of 16 bytes
				encryptedPass = new byte[userPassBytes.length];
			} else {
				// Make it a multiple of 16 bytes
				encryptedPass = new byte[((userPassBytes.length / 16) * 16) + 16];
			}
		} else {
			// the encrypted password must be between 16 and 128 bytes
			encryptedPass = new byte[128];
		}

		// copy the userPass into the encrypted pass and then fill it out with zeroes
		System.arraycopy(userPassBytes, 0, encryptedPass, 0, userPassBytes.length);
		for (int i = userPassBytes.length; i < encryptedPass.length; i++) {
			encryptedPass[i] = 0; // fill it out with zeroes
		}
		// add the shared secret
		md5.update(sharedSecret.getBytes());
		// add the Request Authenticator.
		md5.update(requestAuthenticator);
		// get the md5 hash( b1 = MD5(S + RA) ).
		byte bn[] = md5.digest();

		for (int i = 0; i < 16; i++) {
			// perform the XOR as specified by RFC 2865.
			encryptedPass[i] = (byte) (bn[i] ^ encryptedPass[i]);
		}

		if (encryptedPass.length > 16) {
			for (int i = 16; i < encryptedPass.length; i += 16) {
				md5.reset();
				md5.update(sharedSecret.getBytes());
				md5.update(encryptedPass, i - 16, 16);
				bn = md5.digest();
				for (int j = 0; j < 16; j++) {
					// perform the XOR as specified by RFC 2865.
					encryptedPass[i + j] = (byte) (bn[j] ^ encryptedPass[i + j]);
				}
			}
		}
		return encryptedPass;
	}