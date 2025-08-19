package com.zeon.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.zeon.core.Encrypt;
import com.zeon.utils.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 17:40
 */
public class EncryptJsonDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer{
	private static final Logger logger = LoggerFactory.getLogger(EncryptJsonDeserializer.class);
	private final Encrypt encrypt;

	public EncryptJsonDeserializer(Encrypt encrypt) {
		this.encrypt = encrypt;
	}

	@Override
	public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
		String value = jsonParser.getValueAsString();
		if (value == null || value.isEmpty()) {
			return value;
		}
		return EncryptUtils.decrypt(value, encrypt);
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
		Encrypt encrypt = null;
		if (beanProperty != null) {
			encrypt = beanProperty.getAnnotation(Encrypt.class);
		}
		return new EncryptJsonDeserializer(encrypt);
	}
}
