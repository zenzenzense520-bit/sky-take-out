package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * C 端用户 Mapper
 */
@Mapper
public interface UserMapper {

    /** 新增用户 */
    void insert(User user);

    /** 根据 id 查询用户 */
    User getById(Long id);

    /** 根据微信 openid 查询用户 */
    User getByOpenid(String openid);

    /** 根据动态条件统计用户数量（数据报表用） */
    Integer countByMap(Map map);
}
