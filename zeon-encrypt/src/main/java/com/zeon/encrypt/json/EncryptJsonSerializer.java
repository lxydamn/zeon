package com.zeon.encrypt.json;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.zeon.encrypt.core.Encrypt;
import com.zeon.encrypt.utils.EncryptUtils;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 16:52
 */
public class EncryptJsonSerializer extends JsonSerializer<Object> {
	private static final Logger logger = LoggerFactory.getLogger(EncryptJsonSerializer.class);
	private final Encrypt encrypt;

	public EncryptJsonSerializer(Encrypt encrypt) {
		this.encrypt = encrypt;
	}

	@Override
	public void serialize(Object object, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		if (object == null) {
			return;
		}
        jsonGenerator.writeString(EncryptUtils.encrypt(object, encrypt));
	}
}
