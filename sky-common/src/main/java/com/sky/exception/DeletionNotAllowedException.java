package com.sky.exception;

/**
 * 不允许删除异常（分类/菜品/套餐存在关联数据时抛出）
 */
public class DeletionNotAllowedException extends BaseException {

    public DeletionNotAllowedException() {
    }

    public DeletionNotAllowedException(String msg) {
        super(msg);
    }
}
