package com.zeon.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.zeon.dao.UsersDao;
import com.zeon.encrypt.core.Encrypt;
import com.zeon.entity.Users;
import com.zeon.export.annotations.Export;
import com.zeon.export.constants.ExportMethod;
import com.zeon.service.UsersService;

import jakarta.annotation.Resource;

/**
 * (Users)表控制层
 *
 * @author xingyang.li@hand-china.com
 * @since 2025-08-16 17:42:47
 */
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

    @GetMapping
    public ResponseEntity<List<Users>> queryAll(@Encrypt Users user) throws JsonProcessingException {
        System.out.println(user);
        ObjectMapper objectMapper = applicationContext.getBean(ObjectMapper.class);
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

}

