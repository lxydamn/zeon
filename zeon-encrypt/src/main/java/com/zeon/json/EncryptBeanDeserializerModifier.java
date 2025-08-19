package com.zeon.json;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.zeon.core.Encrypt;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 16:44
 */
public class EncryptBeanDeserializerModifier extends BeanDeserializerModifier {
	@Override
	public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
		return super.updateBuilder(config, beanDesc, builder);
	}
}
