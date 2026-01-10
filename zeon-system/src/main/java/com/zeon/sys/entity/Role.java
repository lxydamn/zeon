package com.zeon.sys.entity;

import com.zeon.encrypt.core.Encrypt;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/16 19:00
 */
public class Role {
    @Encrypt
	private Long id;
	private String role;
	private String roleName;
	private String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
