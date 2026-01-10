# Zeon Export Module

## 简介

Zeon Export 是一个基于 Spring Boot 的自动配置模块，用于简化数据导出功能。它支持将数据导出为 Excel (XLSX) 和 CSV
格式，通过简单的注解即可实现数据导出功能。

## 功能特性

- 支持同步和~~异步导出~~模式
- 支持 Excel (XLSX) 和 CSV 格式
- 基于注解的声明式导出配置
- 自动设置响应头，浏览器自动下载文件
- 自动调整列宽和样式美化
- 支持自定义导出文件名和列标题

## 安装

在 [pom.xml](file://C:\Users\lxy29\Desktop\zeon\pom.xml) 中添加依赖：

```xml

<dependency>
    <groupId>com.zeon</groupId>
    <artifactId>zeon-export</artifactId>
    <version>0.1.0</version>
</dependency>
```

## 使用方法

### 1. 创建导出实体类

```java

@ExportEntity(name = "用户信息")
public class UserExportDTO {
	@ExportColumn(name = {"用户名"})
	private String username;

	@ExportColumn(name = {"邮箱"})
	private String email;

	@ExportColumn(name = {"创建时间"})
	private LocalDateTime createTime;

// getters and setters
}
```

### 2. 在 Controller 中使用 @Export 注解

```java

@RestController
@RequestMapping("/api/users")
public class UserController {
	@GetMapping("/export")
	@Export(value = UserExportDTO.class, method = ExportMethod.SYNC, type = FileType.XLSX)
	public List<UserExportDTO> exportUsers() {
		// 获取需要导出的数据
		return userService.getAllUsersForExport();
	}
}
```

### 3. 注解说明

#### @ExportEntity

用于类级别，定义导出实体的基本信息：

- [name](file://jakarta\annotation\Resource.java#L8-L8): 导出文件的名称，默认为类名

#### @ExportColumn

用于字段级别，定义导出列的信息：

- [name](file://jakarta\annotation\Resource.java#L8-L8): 列标题，支持多级表头

#### @Export

用于方法级别，定义导出行为：

- [value](file://C:\Users\lxy29\Desktop\zeon\zeon-encrypt\src\main\java\com\zeon\encrypt\core\Encrypt.java#L21-L21):
  导出数据对应的实体类
- [method](file://C:\Users\lxy29\Desktop\zeon\zeon-export\src\main\java\com\zeon\export\annotations\Export.java#L28-L28):
  导出方式 (SYNC 同步, ASYNC 异步)
- [type](file://jakarta\annotation\Resource.java#L10-L10): 导出文件类型 (XLSX, CSV)

## 配置

该模块为自动配置模块，只需添加依赖即可使用，无需额外配置。

## 依赖

- `Spring Boot`
- `fastexcel 1.3.0`
- `Lombok`

## 示例

```java

@Export(value = UserExportDTO.class, type = FileType.XLSX)
public List<UserExportDTO> exportUsersToExcel() {
	return userService.getUsers();
}

@Export(value = UserExportDTO.class, type = FileType.CSV)
public List<UserExportDTO> exportUsersToCsv() {
	return userService.getUsers();
}
```

当访问对应的接口时，系统会自动将返回的数据列表导出为指定格式的文件并提供下载。

## 注意事项

1. 被 @Export 注解的方法必须返回 `List<T>` 或 `ResponseEntity<List<T>>` 类型
2. 导出实体类必须使用 @ExportEntity 和 @ExportColumn 注解标记
3. 目前异步导出功能尚未实现
