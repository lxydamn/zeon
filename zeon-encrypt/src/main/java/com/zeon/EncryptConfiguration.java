package com.zeon;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import com.zeon.encrypt.core.Encrypt;
import com.zeon.encrypt.mvc.WebBeanPostProcessor;
import org.springframework.context.annotation.ComponentScan;

/**
 * <p>
 * </p>
 *
 * @author xingyang.li@hand-china.com 2025/8/19 16:54
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.zeon")
@ConditionalOnClass(Encrypt.class)
public class EncryptConfiguration {

    @Bean
    public WebBeanPostProcessor webBeanPostProcessor() {
        return new WebBeanPostProcessor();
    }
}
