package com.zeon.json;

import java.util.Iterator;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.zeon.core.Encrypt;

/**
 * <p>
 * </p>
 *
 * @author xingyang.li@hand-china.com 2025/8/19 16:44
 */
public class EncryptBeanDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc,
                    BeanDeserializerBuilder builder) {
        // 遍历所有字段
        for (Iterator<SettableBeanProperty> it = builder.getProperties(); it.hasNext();) {
            SettableBeanProperty property = it.next();

            // 检查字段是否有 @Encrypted 注解
            if (property.getMember().hasAnnotation(Encrypt.class)) {
                // 替换反序列化器
                SettableBeanProperty newProperty = property.withValueDeserializer(
                                new EncryptJsonDeserializer(property.getAnnotation(Encrypt.class), property.getType()));
                builder.addOrReplaceProperty(newProperty, true);
            }
        }
        return builder;
    }
}
