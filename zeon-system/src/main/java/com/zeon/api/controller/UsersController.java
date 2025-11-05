package com.zeon.api.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zeon.common.utils.RedisLockUtils;
import com.zeon.dao.DataImportItfMapper;
import com.zeon.dao.UsersDao;
import com.zeon.encrypt.core.Encrypt;
import com.zeon.entity.DataImportItf;
import com.zeon.entity.Users;
import com.zeon.service.UsersService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * (Users)表控制层
 *
 * @author xingyang.li@hand-china.com
 * @since 2025-08-16 17:42:47
 */
@Slf4j
@RestController
@RequestMapping("users")
public class UsersController {
    /**
     * 服务对象
     */
    @Resource
    private UsersService usersService;

    @Resource
    private UsersDao usersDao;

	@Resource
	private DataImportItfMapper dataImportItfMapper;

	@Autowired
	private RedisLockUtils redisLockUtils;

    @GetMapping
    public ResponseEntity<List<Users>> queryAll(@Encrypt Users user) {
        System.out.println(user);
        return ResponseEntity.ok(usersDao.queryAll(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Users> queryById(@PathVariable("id") @Encrypt Long id) {
        return ResponseEntity.ok(this.usersDao.queryById(id));
    }

    @PostMapping
    public ResponseEntity<Users> add(@RequestBody @Encrypt Users users) {
        return ResponseEntity.ok(this.usersService.insert(users));
    }

    /**
     * 编辑数据
     *
     * @param users 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<Users> edit(Users users) {
        return ResponseEntity.ok(this.usersService.update(users));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Long id) {
        return ResponseEntity.ok(this.usersService.deleteById(id));
    }

	@GetMapping("/template")
	public void template(HttpServletResponse response) {

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''user-import-template.xlsx" );

		try (ServletOutputStream outputStream = response.getOutputStream();
			 ExcelWriter writer = EasyExcel.write(outputStream).build()){
			for (int i = 0; i < 3; i ++) {
				WriteSheet sheet = EasyExcel.writerSheet().build();
				sheet.setSheetName("sheet" + i);
				sheet.setSheetNo( i);
				sheet.setHead(List.of(List.of("姓名"), List.of("手机号"), List.of("邮箱")));
				writer.write(List.of(List.of("张三-" + i, "12345678901-" + i , "123@qq.com-" + i)), sheet);
			}
		} catch (Exception  e) {
			log.info("template error: {}", e.getMessage());
		}

	}

	@PostMapping("/import")
	public String importExcel(@RequestParam("file") MultipartFile file) {
		try (InputStream is = file.getInputStream()) {
			String batchNo = "USER-" + UUID.randomUUID().toString().replace("-", "");
			log.info("start import excel, batchNo: {}", batchNo);
			List<DataImportItf> columns = new ArrayList<>();
			EasyExcel.read(is, new AnalysisEventListener<Map<String, String>>() {
				@Override
				public void invoke(Map<String, String> list, AnalysisContext context) {
					Integer sheetNo = context.readSheetHolder().getSheetNo();
					List<String> results = list.values().stream().toList();
					columns.add(DataImportItf.builder()
							.batchNo(batchNo)
							.status("PENDING")
							.sheetNo(Long.valueOf(sheetNo))
							.data(results.toString())
							.build());
					if (columns.size() > 100) {
						dataImportItfMapper.insert(columns);
						columns.clear();
					}
				}
				@Override
				public void doAfterAllAnalysed(AnalysisContext analysisContext) {
					log.info("read all of data sheet: {}", analysisContext.readSheetHolder().getSheetName());
				}
			}).doReadAll();

			return batchNo;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	@GetMapping("/read")
	public List<DataImportItf> getRead(@RequestParam("batchNo") String batchNo) {
		return dataImportItfMapper.selectList(new LambdaQueryWrapper<>(DataImportItf.class));
	}

	@PostMapping("/incr")
	@Transactional
	public boolean incr(@RequestParam String batchNo) throws InterruptedException {
		String key = null;
		try {
			key = redisLockUtils.tryLock("store", Duration.ofSeconds(5));
			if (key == null) throw new RuntimeException("get lock failed!");
			int store = usersDao.selectStore();
			Thread.sleep(300);
			store = store + 1;
			return usersDao.updateStore(store) == 1;
		} finally {
			if (key != null )
				redisLockUtils.unlock("store", key);
		}
	}
}

