package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

/**
 * 员工业务接口
 */
public interface EmployeeService {

    /** 员工登录 */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /** 新增员工 */
    void save(EmployeeDTO employeeDTO);

    /** 员工分页查询 */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /** 启用/禁用员工 */
    void startOrStop(Integer status, Long id);

    /** 根据 id 查询员工 */
    Employee getById(Long id);

    /** 编辑员工信息 */
    void update(EmployeeDTO employeeDTO);
}
