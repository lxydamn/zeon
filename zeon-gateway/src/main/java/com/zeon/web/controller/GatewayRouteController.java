package com.zeon.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zeon.web.entity.GatewayRoute;
import com.zeon.web.service.GatewayRouteService;
import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * (GatewayRoute)表控制层
 *
 * @author xingyang.li@hand-china.com
 * @since 2025-09-06 11:12:58
 */
@RestController
@RequestMapping("route")
public class GatewayRouteController {
	/**
	 * 服务对象
	 */
	@Resource
	private GatewayRouteService gatewayRouteService;
	@Resource
	private ApplicationContext applicationContext;

	/**
	 * 分页查询所有数据
	 *
	 * @param page         分页对象
	 * @param gatewayRoute 查询实体
	 * @return 所有数据
	 */
	@GetMapping
	public ResponseEntity<Map<String, Object>> selectAll(Page<GatewayRoute> page, GatewayRoute gatewayRoute) {
		RouteDefinitionRepository bean = applicationContext.getBean(RouteDefinitionRepository.class);
		Flux<RouteDefinition> routeDefinitions = bean.getRouteDefinitions();
		routeDefinitions.subscribe(System.out::println);

		Map<String, Object> result = new HashMap<>();
		result.put("data", this.gatewayRouteService.page(page, new QueryWrapper<>(gatewayRoute)));
		result.put("success", true);
		return ResponseEntity.ok(result);
	}

	/**
	 * 通过主键查询单条数据
	 *
	 * @param id 主键
	 * @return 单条数据
	 */
	@GetMapping("{id}")
	public ResponseEntity<Map<String, Object>> selectOne(@PathVariable Serializable id) {
		Map<String, Object> result = new HashMap<>();
		result.put("data", this.gatewayRouteService.getById(id));
		result.put("success", true);
		return ResponseEntity.ok(result);
	}

	/**
	 * 新增数据
	 *
	 * @param gatewayRoute 实体对象
	 * @return 新增结果
	 */
	@PostMapping
	public ResponseEntity<Map<String, Object>> insert(@RequestBody GatewayRoute gatewayRoute) {
		Map<String, Object> result = new HashMap<>();
		boolean saveResult = this.gatewayRouteService.save(gatewayRoute);
		result.put("success", saveResult);
		result.put("message", saveResult ? "新增成功" : "新增失败");
		return ResponseEntity.ok(result);
	}

	/**
	 * 修改数据
	 *
	 * @param gatewayRoute 实体对象
	 * @return 修改结果
	 */
	@PutMapping
	public ResponseEntity<Map<String, Object>> update(@RequestBody GatewayRoute gatewayRoute) {
		Map<String, Object> result = new HashMap<>();
		boolean updateResult = this.gatewayRouteService.updateById(gatewayRoute);
		result.put("success", updateResult);
		result.put("message", updateResult ? "修改成功" : "修改失败");
		return ResponseEntity.ok(result);
	}

	/**
	 * 删除数据
	 *
	 * @param idList 主键结合
	 * @return 删除结果
	 */
	@DeleteMapping
	public ResponseEntity<Map<String, Object>> delete(@RequestParam("idList") List<Long> idList) {
		Map<String, Object> result = new HashMap<>();
		boolean deleteResult = this.gatewayRouteService.removeByIds(idList);
		result.put("success", deleteResult);
		result.put("message", deleteResult ? "删除成功" : "删除失败");
		return ResponseEntity.ok(result);
	}
}

