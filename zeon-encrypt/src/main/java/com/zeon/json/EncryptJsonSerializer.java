package com.zeon.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.zeon.core.Encrypt;
import com.zeon.utils.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 16:52
 */
public class EncryptJsonSerializer extends JsonSerializer<Object> implements ContextualSerializer{
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
		Assert.notNull(object.getClass().getClassLoader(), "Encrypt field is not a basic type");
		jsonGenerator.assignCurrentValue(EncryptUtils.encrypt(object, encrypt));
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
		if (beanProperty != null && beanProperty.getAnnotation(Encrypt.class) != null) {
			return this;
		}
		return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
	}
}
