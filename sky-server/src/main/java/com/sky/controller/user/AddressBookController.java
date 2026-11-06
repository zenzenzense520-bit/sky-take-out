package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端地址簿接口
 */
@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C 端地址簿接口")
@Slf4j
public class AddressBookController {

    private final AddressBookService addressBookService;

    public AddressBookController(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    @PostMapping
    @ApiOperation("新增地址")
    public Result<String> save(@RequestBody AddressBook addressBook) {
        addressBookService.save(addressBook);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询当前用户全部地址")
    public Result<List<AddressBook>> list() {
        return Result.success(addressBookService.list(new AddressBook()));
    }

    @GetMapping("/{id}")
    @ApiOperation("根据 id 查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        return Result.success(addressBookService.getById(id));
    }

    @PutMapping
    @ApiOperation("修改地址")
    public Result<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.update(addressBook);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("根据 id 删除地址")
    public Result<String> deleteById(Long id) {
        addressBookService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result<String> setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefault() {
        return Result.success(addressBookService.getDefault());
    }
}
