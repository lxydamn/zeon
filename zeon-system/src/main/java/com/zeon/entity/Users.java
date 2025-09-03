package com.zeon.entity;

import java.io.Serializable;

import com.zeon.db.annotations.CrossQueryEntity;
import com.zeon.db.annotations.CrossQueryField;
import com.zeon.encrypt.core.Encrypt;
import com.zeon.db.domain.BaseDomain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (Users)实体类
 *
 * @author xingyang.li@hand-china.com
 * @since 2025-08-16 17:42:48
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Users extends BaseDomain implements Serializable {
    private static final long serialVersionUID = 438491303269108393L;
    @Encrypt
    private Long id;

    private String username;
    @Encrypt
    private String password;

    @CrossQueryField(tableName = "role", keyField = "role", valueField = "roleName")
    @CrossQueryEntity(keyField = "role", tableName = "role", valueField = "roleObj")
    private String role;

    private String nickname;

    private String roleName;

    private Role roleObj;

}

