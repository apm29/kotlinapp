/**
 * Copyright (C) 2014 Luki(liulongke@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.luki.x.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES Util
 * 
 * @author Luki
 */
public class DESUtil {

	private final static String DEFAULT_KEY = "lukixdes";

	public static void main(String[] args) throws Exception {

		String data = "";
		String key = "1";
		System.err.println(encrypt(data, key));
		System.err.println(decrypt(encrypt(data, key), key));

	}

	/**
	 * Description
	 * 
	 * @param data data
	 * @param key key
	 * @return encrypt data
	 * @throws Exception
	 */
	public static String encrypt(String data, String key) throws Exception {
		if (data == null)
			return null;
		if (key == null || key.length() < 8) {
			key += DEFAULT_KEY;
		}
		return new String(Base64.encode(encrypt(data.getBytes(), key.getBytes()), 0));
	}

	/**
	 * Description
	 * 
	 * @param data data
	 * @param key key
	 * @return decrypt data
	 * @throws Exception
	 */
	public static String decrypt(String data, String key) throws Exception {
		if (data == null)
			return null;
		if (key == null || key.length() < 8) {
			key += DEFAULT_KEY;
		}
		return new String(decrypt(Base64.decode(data, 0), key.getBytes()));
	}

	/**
	 * Description encrypt
	 * 
	 * @param data data
	 * @param key key
	 * @return encrypt data
	 * @throws Exception
	 */
	private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();

		DESKeySpec dks = new DESKeySpec(key);

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretkey = keyFactory.generateSecret(dks);

		Cipher cipher = Cipher.getInstance("DES");

		cipher.init(Cipher.ENCRYPT_MODE, secretkey, sr);

		return cipher.doFinal(data);
	}

	/**
	 * Description decrypt
	 * 
	 * @param data data
	 * @param key key
	 * @return decrypt data
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		SecureRandom sr = new SecureRandom();

		DESKeySpec dks = new DESKeySpec(key);

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretkey = keyFactory.generateSecret(dks);

		Cipher cipher = Cipher.getInstance("DES");

		cipher.init(Cipher.DECRYPT_MODE, secretkey, sr);

		return cipher.doFinal(data);
	}
}
