package com.sky.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询统一返回结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult implements Serializable {

    /** 总记录数 */
    private long total;

    /** 当前页数据 */
    private List records;
}
