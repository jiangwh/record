## Radius
* EAP-MD5

```
	public static byte[] encodeEapPassword(
			final byte[] passwd, 
			final byte packetIdentifier,
			final byte[] eapChallenge) {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(packetIdentifier);
		md5.update(passwd);
		md5.update(eapChallenge);
		return md5.digest();
	}
```