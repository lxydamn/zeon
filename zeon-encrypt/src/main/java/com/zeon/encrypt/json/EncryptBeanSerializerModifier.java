package com.zeon.encrypt.json;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.zeon.encrypt.core.Encrypt;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 16:41
 */
public class EncryptBeanSerializerModifier extends BeanSerializerModifier {

    private static final Logger logger = LoggerFactory.getLogger(EncryptBeanSerializerModifier.class);

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
                    List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            if (writer.getAnnotation(Encrypt.class) != null) {
                writer.assignSerializer(new EncryptJsonSerializer(writer.getAnnotation(Encrypt.class)));
            }
        }
        return beanProperties;
    }
}
