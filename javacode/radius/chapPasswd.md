## Radius

* Chap 

```
	public static byte[] encodeChapPassword(final byte[] passwd, final byte chapIdentifier,
			final byte[] chapChallenge) {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(chapIdentifier);
		md5.update(passwd);
		md5.update(chapChallenge);
		return md5.digest();
	}
```