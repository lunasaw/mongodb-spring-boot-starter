package com.luna.mongodb.controller;

import com.luna.common.dto.ResultDTO;
import com.luna.common.dto.constant.ResultCode;
import com.luna.common.page.Page;
import com.luna.mongodb.entity.UserDO;
import com.luna.mongodb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author luna
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/get/{id}")
    public ResultDTO<UserDO> get(@PathVariable(value = "id") Long id) {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, userService.get(id));
    }

    @PostMapping("/save")
    public ResultDTO<UserDO> save(@RequestBody UserDO userDO) {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, userService.save(userDO));
    }

    @PostMapping("/saveBatch")
    public ResultDTO<List<UserDO>> insert(@RequestBody List<UserDO> list) {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, userService.saveBatch(list));
    }

    @GetMapping("/list")
    public ResultDTO<List<UserDO>> list(UserDO userDO) {
        List<UserDO> userDOList = userService.listByEntity(userDO);
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, userDOList);
    }

    @GetMapping("/pageListByEntity/{page}/{size}")
    public ResultDTO<Page<UserDO>> listPageByEntity(@PathVariable(value = "page") int page,
        @PathVariable(value = "size") int size, UserDO userDO) {
        Page<UserDO> pageInfo = userService.listPageByEntity(page, size, userDO);
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, pageInfo);
    }

    @GetMapping("/pageList/{page}/{size}")
    public ResultDTO<Page<UserDO>> listPage(@PathVariable(value = "page") int page,
        @PathVariable(value = "size") int size) {
        Page<UserDO> pageInfo = userService.listPage(page, size);
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, pageInfo);
    }

    @PutMapping("/update")
    public ResultDTO<Boolean> update(@RequestBody UserDO dept) {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, userService.update(dept) == 1);
    }

    @PutMapping("/updateBatch")
    public ResultDTO<Boolean> update(@RequestBody List<UserDO> list) {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS,
            userService.updateBatch(list) == list.size());
    }

    @DeleteMapping("/delete/{id}")
    public ResultDTO<Boolean> deleteOne(@PathVariable(value = "id") Long id) {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, userService.deleteById(id) == 1);
    }

    @DeleteMapping("/deleteByEntity")
    public ResultDTO<Boolean> deleteOne(@RequestBody UserDO dept) {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, userService.deleteByEntity(dept) == 1);
    }

    @DeleteMapping("/delete")
    public ResultDTO<Long> deleteBatch(@RequestBody List<Long> ids) {
        long result = 0;
        if (ids != null && ids.size() > 0) {
            result = userService.deleteByIds(ids);
        }
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, result);
    }

    @GetMapping("/account")
    public ResultDTO<Long> getAccount() {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS, userService.countAll());
    }

    @GetMapping("/accountByEntity")
    public ResultDTO<Long> getAccountByEntity(UserDO dept) {
        return new ResultDTO<>(true, ResultCode.SUCCESS, ResultCode.MSG_SUCCESS,
            userService.countByEntity(dept));
    }
}
