package com.zeon;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import com.zeon.core.Encrypt;
import com.zeon.mvc.WebBeanPostProcessor;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 16:54
 */
@AutoConfiguration
@ConditionalOnClass(Encrypt.class)
public class EncryptConfiguration {

    @Bean
    public WebBeanPostProcessor webBeanPostProcessor() {
        return new WebBeanPostProcessor();
    }
}
