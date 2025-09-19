package com.zeon.encrypt.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 17:49
 */
public class CryptoModule extends Module {
    private static final String MODULE_NAME = "CryptoModule";
    private static final Version VERSION = new Version(1, 0, 0, null, "com.zeon.encrypt", MODULE_NAME);

	private final EncryptBeanSerializerModifier encryptBeanSerializerModifier;
	private final EncryptBeanDeserializerModifier encryptBeanDeserializerModifier;

	public CryptoModule(EncryptBeanSerializerModifier encryptBeanSerializerModifier, EncryptBeanDeserializerModifier encryptBeanDeserializerModifier) {
		this.encryptBeanSerializerModifier = encryptBeanSerializerModifier;
		this.encryptBeanDeserializerModifier = encryptBeanDeserializerModifier;
	}

	@Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    @Override
    public Version version() {
        return VERSION;
    }

    @Override
    public void setupModule(SetupContext setupContext) {
        // 注册自定义的序列化和反序列化修改器
         setupContext.addBeanSerializerModifier(encryptBeanSerializerModifier);
         setupContext.addBeanDeserializerModifier(encryptBeanDeserializerModifier);
    }
}
