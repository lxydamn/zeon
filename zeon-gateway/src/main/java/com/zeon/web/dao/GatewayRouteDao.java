package com.zeon.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zeon.web.entity.GatewayRoute;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 网关路由DAO接口
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/6
 */
@Mapper
public interface GatewayRouteDao extends BaseMapper<GatewayRoute> {
}