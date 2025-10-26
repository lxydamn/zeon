package com.zeon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p> </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/10/26 15:17
 */
@Data
@Builder
public class DataImportItf {
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 批次号
	 */
	private String batchNo;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;
	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime;

	/**
	 * 数据
	 */
	private String data;

	/**
	 * sheet编号
	 */
	private Long sheetNo;
}
