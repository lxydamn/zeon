package com.zeon.utils;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.zeon.core.Encrypt;
import org.springframework.util.StringUtils;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 17:14
 */
public class EncryptUtils {
	private static final String DEFAULT_KEY = "zeon";
	private static final String ALGORITHM = "AES";
	private static final int KEY_BYTE = 16;

	public static String encrypt(Object value, Encrypt encrypt) {
		String strValue = value.toString();
		String key = StringUtils.hasLength(encrypt.value()) ? encrypt.value() : DEFAULT_KEY;
		return encrypt(strValue, key);
	}

	public static String decrypt(String encryptedValue, Encrypt encrypt) {
		String key = StringUtils.hasLength(encrypt.value()) ? encrypt.value() : DEFAULT_KEY;
		return decrypt(encryptedValue, key);
	}

	private static String encrypt(String value, String key) {
		try {
			byte[] keyBytes = adjustKey(key);
			
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, ALGORITHM));
			byte[] encrypted = cipher.doFinal(value.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			throw new RuntimeException("Failed to encrypt value", e);
		}
	}

	private static String decrypt(String encryptedValue, String key) {
		try {
			byte[] keyBytes = adjustKey(key);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, ALGORITHM));
			byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
			return new String(decrypted);
		} catch (Exception e) {
			throw new RuntimeException("Failed to decrypt value", e);
		}
	}
	
	/**
	 * 调整密钥长度为16字节
	 * @param key 原始密钥
	 * @return 调整后的16字节密钥
	 */
	private static byte[] adjustKey(String key) {
		byte[] keyBytes = key.getBytes();
		if (keyBytes.length != KEY_BYTE) {
			byte[] adjustedKey = new byte[KEY_BYTE];
			System.arraycopy(keyBytes, 0, adjustedKey, 0, Math.min(keyBytes.length, 16));
			keyBytes = adjustedKey;
		}
		return keyBytes;
	}
}
