package com.zeon.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * (GatewayRoute)表实体类
 *
 * @author lxy
 * @since 2025-09-06 11:13:05
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayRoute extends Model<GatewayRoute> {
	// 主键
	@TableId(type = IdType.AUTO)
	private Long id;
	// 路由地址
	private String uri;
	// 断言
	private String predicates;
	// 过滤器
	private String filters;
	// 优先级
	private Integer orderBy;
	// 描述
	private String description;
	// 开关
	private Boolean enabled;
	// 服务名称
	private String serviceName;
	// 创建时间
	private LocalDateTime createTime;
	// 更新时间
	private LocalDateTime updateTime;
	// 版本号
	private Integer version;

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		GatewayRoute that = (GatewayRoute) o;
		return uri.equals(that.uri) && serviceName.equals(that.serviceName);
	}

	@Override
	public int hashCode() {
		int result = uri.hashCode();
		result = 31 * result + serviceName.hashCode();
		return result;
	}
}

