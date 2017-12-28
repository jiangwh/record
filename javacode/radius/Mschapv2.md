## Radius
* mschapv2

mschapv2加密过程是不可逆的，只能通过相关因子计算ntresponse，然后比较ntresponse的值是否相同?
HashNtPasswordHash的值是在下发mppk时需要使用

```

	public static byte[] GenerateNTResponse(byte[] AuthenticatorChallenge, byte[] PeerChallenge, byte[] userid,
			byte[] Password) {
		byte Challenge[] = ChallengeHash(PeerChallenge, AuthenticatorChallenge, userid);
		byte PasswordHash[] = NtPasswordHash(Password);
		return ChallengeResponse(Challenge, PasswordHash);
	}

	public static byte[] ChallengeHash(final byte[] PeerChallenge, final byte[] AuthenticatorChallenge,
			final byte[] UserName) {
		byte Challenge[] = new byte[8];
		IMessageDigest md = HashFactory.getInstance("SHA-1");
		md.update(PeerChallenge, 0, 16);
		md.update(AuthenticatorChallenge, 0, 16);
		md.update(UserName, 0, UserName.length);
		System.arraycopy(md.digest(), 0, Challenge, 0, 8);
		return Challenge;
	}

	private static byte[] NtPasswordHash(byte[] Password) {
		byte PasswordHash[] = new byte[16];
		byte uniPassword[] = unicode(Password);
		IMessageDigest md = HashFactory.getInstance("MD4");
		md.update(uniPassword, 0, uniPassword.length);
		System.arraycopy(md.digest(), 0, PasswordHash, 0, 16);
		return PasswordHash;
	}

	private static byte[] HashNtPasswordHash(byte[] PasswordHash) {
		byte PasswordHashHash[] = new byte[16];
		IMessageDigest md = HashFactory.getInstance("MD4");
		md.update(PasswordHash, 0, 16);
		System.arraycopy(md.digest(), 0, PasswordHashHash, 0, 16);
		return PasswordHashHash;
	}
	
	
	//NtResponseHashHash
	public static byte[] GenerateAuthenticatorResponseForMd4(
		RadiusPacket request, String username, String password, 	byte[] ntResponse, byte[] peerChallenge, byte[] 		authChallenge) {
		byte[] passwordHash = new byte[16];
		byte[] passwordHashHash = new byte[16];
		byte[] challenge = new byte[8];
		byte[] digest = new byte[20];
		String encode = "UTF-8";
		if (request instanceof AccessRequest) {
			encode = ((AccessRequest) request).getPeapEncode();
		}
		/*
		 * "Magic" constants used in response generation
		 */
		byte[] Magic1 = new byte[]
		{ 0x4D, 0x61, 0x67, 0x69, 0x63, 0x20, 0x73, 0x65, 0x72, 0x76,
				0x65, 0x72, 0x20, 0x74, 0x6F, 0x20, 0x63, 0x6C, 0x69, 0x65,
				0x6E, 0x74, 0x20, 0x73, 0x69, 0x67, 0x6E, 0x69, 0x6E, 0x67,
				0x20, 0x63, 0x6F, 0x6E, 0x73, 0x74, 0x61, 0x6E, 0x74 };

		byte[] Magic2 = new byte[]
		{ 0x50, 0x61, 0x64, 0x20, 0x74, 0x6F, 0x20, 0x6D, 0x61, 0x6B,
				0x65, 0x20, 0x69, 0x74, 0x20, 0x64, 0x6F, 0x20, 0x6D, 0x6F,
				0x72, 0x65, 0x20, 0x74, 0x68, 0x61, 0x6E, 0x20, 0x6F, 0x6E,
				0x65, 0x20, 0x69, 0x74, 0x65, 0x72, 0x61, 0x74, 0x69, 0x6F,
				0x6E };

		passwordHash = StringUtil.hexStringToBytes(password);
		passwordHashHash = HashNtPasswordHash(passwordHash);

		IMessageDigest sha1Hash = HashFactory.getInstance("SHA-1");
		sha1Hash.reset();
		sha1Hash.update(passwordHashHash, 0, passwordHashHash.length);
		sha1Hash.update(ntResponse, 0, ntResponse.length);
		sha1Hash.update(Magic1, 0, Magic1.length);
		digest = sha1Hash.digest();

		try {
			challenge = ChallengeHash(peerChallenge, authChallenge, username.getBytes(encode));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		sha1Hash.reset();
		sha1Hash.update(digest, 0, digest.length);
		sha1Hash.update(challenge, 0, challenge.length);
		sha1Hash.update(Magic2, 0, Magic2.length);
		digest = sha1Hash.digest();

		return digest;
	}
	
```
    
