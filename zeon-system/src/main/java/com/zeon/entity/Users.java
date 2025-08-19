package com.zeon.entity;

import java.io.Serializable;

import com.zeon.annotations.CrossQueryEntity;
import com.zeon.annotations.CrossQueryField;
import com.zeon.core.Encrypt;
import com.zeon.domain.BaseDomain;

/**
 * (Users)实体类
 *
 * @author xingyang.li@hand-china.com
 * @since 2025-08-16 17:42:48
 */
public class Users extends BaseDomain implements Serializable {
    private static final long serialVersionUID = 438491303269108393L;
    @Encrypt
    private Long id;
    @Encrypt
    private String username;

    private String password;

    @CrossQueryField(tableName = "role", keyField = "role", valueField = "roleName")
    @CrossQueryEntity(keyField = "role", tableName = "role", valueField = "roleObj")
    private String role;

    private String nickname;

    private String roleName;


    private Role roleObj;

    public Role getRoleObj() {
        return roleObj;
    }

    public void setRoleObj(Role roleObj) {
        this.roleObj = roleObj;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

