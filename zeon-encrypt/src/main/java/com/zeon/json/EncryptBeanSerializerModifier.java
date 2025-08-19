package com.zeon.json;

import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.zeon.core.Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 16:41
 */
public class EncryptBeanSerializerModifier extends BeanSerializerModifier {

	@Override
	public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
		return super.changeProperties(config, beanDesc, beanProperties);
	}

}
