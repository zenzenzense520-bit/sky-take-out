package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.AddressBook;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 地址簿 Mapper（SQL 待迭代补充）
 */
@Mapper
public interface AddressBookMapper {

    /** 新增地址 */
    @AutoFill(OperationType.INSERT)
    void insert(AddressBook addressBook);

    /** 修改地址 */
    @AutoFill(OperationType.UPDATE)
    void update(AddressBook addressBook);

    /** 根据 id 删除地址 */
    void deleteById(Long id);

    /** 根据 id 查询地址 */
    AddressBook getById(Long id);

    /** 条件查询当前用户地址列表 */
    List<AddressBook> list(AddressBook addressBook);
}
