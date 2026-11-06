package com.sky.service.impl;

import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 地址簿业务实现（骨架，方法逻辑待迭代补充）
 */
@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {

    private final AddressBookMapper addressBookMapper;

    public AddressBookServiceImpl(AddressBookMapper addressBookMapper) {
        this.addressBookMapper = addressBookMapper;
    }

    @Override
    public void save(AddressBook addressBook) {
        // TODO 迭代三：设置当前用户 id 后新增
        log.info("【骨架】新增地址: {}", addressBook);
    }

    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        // TODO 迭代三：按当前用户 id 查询全部地址
        log.info("【骨架】地址列表查询");
        return Collections.emptyList();
    }

    @Override
    public AddressBook getById(Long id) {
        // TODO 迭代三：按 id 查询
        log.info("【骨架】根据 id 查询地址: id={}", id);
        return null;
    }

    @Override
    public void update(AddressBook addressBook) {
        // TODO 迭代三：按 id 更新
        log.info("【骨架】修改地址: {}", addressBook);
    }

    @Override
    public void deleteById(Long id) {
        // TODO 迭代三：按 id 删除
        log.info("【骨架】删除地址: id={}", id);
    }

    @Override
    public void setDefault(AddressBook addressBook) {
        // TODO 迭代三：先将其他地址置为非默认，再设置当前地址为默认
        log.info("【骨架】设置默认地址: id={}", addressBook.getId());
    }

    @Override
    public AddressBook getDefault() {
        // TODO 迭代三：查询当前用户的默认地址
        log.info("【骨架】查询默认地址");
        return null;
    }
}
