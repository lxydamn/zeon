package com.zeon.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 16:59
 */
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Encrypt {
	/**
	 * 密钥
	 */
	String value() default "";
}
