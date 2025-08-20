package com.zeon.utils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.zeon.core.Encrypt;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 17:14
 */
public class EncryptUtils {
    private static final Logger logger = LoggerFactory.getLogger(EncryptUtils.class);
    private static final String DEFAULT_KEY = "nIOAsDJMmoajsdia";
	private static final String ALGORITHM = "AES";
	private static final int KEY_BYTE = 16;

	public static String encrypt(Object value, Encrypt encrypt) {
		String strValue = value.toString();
		String key = StringUtils.hasLength(encrypt.value()) ? encrypt.value() : DEFAULT_KEY;
		return encrypt(strValue, key);
	}

	public static String decrypt(String encryptedValue, Encrypt encrypt) {
		String key = StringUtils.hasLength(encrypt.value()) ? encrypt.value() : DEFAULT_KEY;
        String decrypt = decrypt(encryptedValue, key);
        logger.info("EncryptUtil use {} decrypt {} get {}", key, encryptedValue, decrypt);
        return decrypt;
	}

    public static Object decrypt(Object object, Encrypt encrypt, Class<?> targetType) {
        if (object instanceof String str) {
            if (str.contains(",")) {
                object = str.split(",");
            } else {
                String decrypted = EncryptUtils.decrypt(str, encrypt);
                return ConvertUtils.convertToTargetType(decrypted, targetType);
            }
        }
        if (object instanceof String[] values) {
            for (int i = 0; i < values.length; i++) {
                values[i] = EncryptUtils.decrypt(values[i], encrypt);
            }
            if (targetType.isArray() && targetType.getComponentType() != String.class) {
                return ConvertUtils.convertArrayToTargetType(values, targetType);
            }
            return values;
        }
        if (object instanceof Iterable<?> iterable) {
            // 创建一个新的列表来存储解密后的值
            List<Object> decryptedList = new ArrayList<>();
            for (Object item : iterable) {
                if (item instanceof String str) {
                    String decrypted = EncryptUtils.decrypt(str, encrypt);
                    decryptedList.add(decrypted);
                } else {
                    decryptedList.add(item);
                }
            }
            // 如果目标类型是数组，则转换为数组
            if (targetType.isArray()) {
                if (targetType.getComponentType() != String.class) {
                    return ConvertUtils.convertListToTargetArrayType(decryptedList, targetType);
                }
                return decryptedList.toArray(new String[0]);
            }
            // 否则返回列表
            return decryptedList;
        }
        return object;
    }

	private static String encrypt(String value, String key) {
		try {
			byte[] keyBytes = adjustKey(key);
			
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, ALGORITHM));
			byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getUrlEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			throw new RuntimeException("Failed to encrypt value", e);
		}
	}

	private static String decrypt(String encryptedValue, String key) {
		try {
			byte[] keyBytes = adjustKey(key);

			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, ALGORITHM));
            byte[] decrypted = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedValue));
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
