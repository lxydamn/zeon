package com.zeon.json;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.zeon.core.Encrypt;
import com.zeon.utils.EncryptUtils;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 17:40
 */
public class EncryptJsonDeserializer extends JsonDeserializer<Object> {
	private static final Logger logger = LoggerFactory.getLogger(EncryptJsonDeserializer.class);
	private final Encrypt encrypt;
    private final JavaType javaType;

    public EncryptJsonDeserializer(Encrypt encrypt, JavaType javaType) {
		this.encrypt = encrypt;
        this.javaType = javaType;
	}

	@Override
	public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
		String value = jsonParser.getValueAsString();
		if (value == null || value.isEmpty()) {
			return value;
		}
        Class<?> rawClass = javaType.getRawClass();
        String decrypt = EncryptUtils.decrypt(value, encrypt);

        return castType(decrypt, rawClass);
	}

    private Object castType(String value, Class<?> clazz) {
        if (clazz == Long.class) {
            return Long.valueOf(value);
		}
        if (clazz == Integer.class) {
            return Integer.valueOf(value);
        }
        if (clazz == Double.class) {
            return Double.valueOf(value);
        }
        if (clazz == Float.class) {
            return Float.valueOf(value);
        }
        return value;
	}
}
