package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 员工 Mapper
 */
@Mapper
public interface EmployeeMapper {

    /** 根据用户名查询员工（登录使用） */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /** 新增员工，公共字段由切面自动填充 */
    @AutoFill(OperationType.INSERT)
    void insert(Employee employee);

    /** 条件分页查询，动态 SQL 见 EmployeeMapper.xml */
    List<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /** 编辑员工，动态 SQL 见 EmployeeMapper.xml */
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);

    /** 根据主键查询 */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}
