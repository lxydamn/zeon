package com.zeon.sys.entity;

import java.io.Serializable;

import com.zeon.db.annotations.CrossQueryField;
import com.zeon.db.domain.BaseDomain;
import com.zeon.encrypt.core.Encrypt;
import com.zeon.export.annotations.ExportColumn;
import com.zeon.export.annotations.ExportEntity;

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
@ExportEntity(name = "用户导出表")
public class Users extends BaseDomain implements Serializable {
    private static final long serialVersionUID = 438491303269108393L;
    @Encrypt
    @ExportColumn(name = "用户ID")
    private Long id;
    @ExportColumn(name = "用户名")
    private String username;
    @Encrypt
    private String password;

    @CrossQueryField(tableName = "role", keyField = "role", valueField = "roleName")
    @ExportColumn(name = "角色")
    private String role;
    @ExportColumn(name = "昵称")
    private String nickname;
    @ExportColumn(name = "角色名称")
    private String roleName;
}

