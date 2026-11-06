-- ============================================================
-- 苍穹外卖数据库初始化脚本
-- 数据库：sky_take_out   字符集：utf8mb4
-- 共 11 张表，覆盖员工、分类、菜品、套餐、用户、地址、购物车、订单
-- ============================================================

DROP DATABASE IF EXISTS sky_take_out;
CREATE DATABASE sky_take_out DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE sky_take_out;

-- ---------------------------- 1. 员工表 ----------------------------
CREATE TABLE employee (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(32)  NOT NULL COMMENT '姓名',
    username    VARCHAR(32)  NOT NULL COMMENT '登录账号',
    password    VARCHAR(64)  NOT NULL COMMENT '密码（MD5）',
    phone       VARCHAR(11)  NOT NULL COMMENT '手机号',
    sex         VARCHAR(2)   NOT NULL COMMENT '性别',
    id_number   VARCHAR(18)  NOT NULL COMMENT '身份证号',
    status      INT          NOT NULL DEFAULT 1 COMMENT '状态 0 禁用 1 启用',
    create_time DATETIME     NOT NULL COMMENT '创建时间',
    update_time DATETIME     NOT NULL COMMENT '最后修改时间',
    create_user BIGINT       NOT NULL COMMENT '创建人',
    update_user BIGINT       NOT NULL COMMENT '最后修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) COMMENT '员工表';

-- ---------------------------- 2. 分类表 ----------------------------
CREATE TABLE category (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    type        INT          DEFAULT NULL COMMENT '类型 1 菜品分类 2 套餐分类',
    name        VARCHAR(32)  NOT NULL COMMENT '分类名称',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序字段',
    status      INT          NOT NULL DEFAULT 1 COMMENT '状态 0 禁用 1 启用',
    create_time DATETIME     NOT NULL COMMENT '创建时间',
    update_time DATETIME     NOT NULL COMMENT '最后修改时间',
    create_user BIGINT       NOT NULL COMMENT '创建人',
    update_user BIGINT       NOT NULL COMMENT '最后修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) COMMENT '分类表';

-- ---------------------------- 3. 菜品表 ----------------------------
CREATE TABLE dish (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(32)   NOT NULL COMMENT '菜品名称',
    category_id BIGINT        NOT NULL COMMENT '分类 id',
    price       DECIMAL(10,2) NOT NULL COMMENT '菜品价格',
    image       VARCHAR(200)  DEFAULT NULL COMMENT '图片路径',
    description VARCHAR(200)  DEFAULT NULL COMMENT '描述信息',
    status      INT           NOT NULL DEFAULT 1 COMMENT '状态 0 停售 1 起售',
    create_time DATETIME      NOT NULL COMMENT '创建时间',
    update_time DATETIME      NOT NULL COMMENT '最后修改时间',
    create_user BIGINT        NOT NULL COMMENT '创建人',
    update_user BIGINT        NOT NULL COMMENT '最后修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_dish_name (name)
) COMMENT '菜品表';

-- ---------------------------- 4. 菜品口味表 ----------------------------
CREATE TABLE dish_flavor (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    dish_id     BIGINT      NOT NULL COMMENT '菜品 id',
    name        VARCHAR(32) NOT NULL COMMENT '口味名称',
    value       VARCHAR(200) DEFAULT NULL COMMENT '口味数据（JSON 数组）',
    create_time DATETIME    NOT NULL COMMENT '创建时间',
    update_time DATETIME    NOT NULL COMMENT '最后修改时间',
    create_user BIGINT      NOT NULL COMMENT '创建人',
    update_user BIGINT      NOT NULL COMMENT '最后修改人',
    PRIMARY KEY (id)
) COMMENT '菜品口味表';

-- ---------------------------- 5. 套餐表 ----------------------------
CREATE TABLE setmeal (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    category_id BIGINT        NOT NULL COMMENT '分类 id',
    name        VARCHAR(32)   NOT NULL COMMENT '套餐名称',
    price       DECIMAL(10,2) NOT NULL COMMENT '套餐价格',
    status      INT           NOT NULL DEFAULT 1 COMMENT '状态 0 停售 1 起售',
    description VARCHAR(200)  DEFAULT NULL COMMENT '描述',
    image       VARCHAR(200)  DEFAULT NULL COMMENT '图片路径',
    create_time DATETIME      NOT NULL COMMENT '创建时间',
    update_time DATETIME      NOT NULL COMMENT '最后修改时间',
    create_user BIGINT        NOT NULL COMMENT '创建人',
    update_user BIGINT        NOT NULL COMMENT '最后修改人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_setmeal_name (name)
) COMMENT '套餐表';

-- ---------------------------- 6. 套餐菜品关联表 ----------------------------
CREATE TABLE setmeal_dish (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    setmeal_id   BIGINT       NOT NULL COMMENT '套餐 id',
    dish_id      BIGINT       NOT NULL COMMENT '菜品 id',
    name         VARCHAR(32)  NOT NULL COMMENT '菜品名称（冗余）',
    price        DECIMAL(10,2) NOT NULL COMMENT '菜品单价（冗余）',
    copies       INT          NOT NULL COMMENT '份数',
    sort         INT          NOT NULL DEFAULT 0 COMMENT '排序',
    create_time  DATETIME     NOT NULL COMMENT '创建时间',
    update_time  DATETIME     NOT NULL COMMENT '最后修改时间',
    create_user  BIGINT       NOT NULL COMMENT '创建人',
    update_user  BIGINT       NOT NULL COMMENT '最后修改人',
    PRIMARY KEY (id)
) COMMENT '套餐菜品关联表';

-- ---------------------------- 7. 用户表 ----------------------------
CREATE TABLE user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    openid      VARCHAR(45)  DEFAULT NULL COMMENT '微信 openid',
    name        VARCHAR(32)  DEFAULT NULL COMMENT '姓名',
    phone       VARCHAR(11)  DEFAULT NULL COMMENT '手机号',
    sex         VARCHAR(2)   DEFAULT NULL COMMENT '性别',
    id_number   VARCHAR(18)  DEFAULT NULL COMMENT '身份证号',
    avatar      VARCHAR(500) DEFAULT NULL COMMENT '头像',
    create_time DATETIME     NOT NULL COMMENT '注册时间',
    PRIMARY KEY (id)
) COMMENT '用户表';

-- ---------------------------- 8. 地址簿表 ----------------------------
CREATE TABLE address_book (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT       NOT NULL COMMENT '用户 id',
    consignee   VARCHAR(50)  NOT NULL COMMENT '收货人',
    sex         VARCHAR(2)   DEFAULT NULL COMMENT '性别',
    phone       VARCHAR(11)  NOT NULL COMMENT '联系电话',
    detail      VARCHAR(200) DEFAULT NULL COMMENT '详细地址',
    label       VARCHAR(20)  DEFAULT NULL COMMENT '标签',
    is_default  INT          NOT NULL DEFAULT 0 COMMENT '是否默认 0 否 1 是',
    create_time DATETIME     NOT NULL COMMENT '创建时间',
    update_time DATETIME     NOT NULL COMMENT '修改时间',
    create_user BIGINT       NOT NULL COMMENT '创建人',
    update_user BIGINT       NOT NULL COMMENT '修改人',
    PRIMARY KEY (id)
) COMMENT '地址簿表';

-- ---------------------------- 9. 购物车表 ----------------------------
CREATE TABLE shopping_cart (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(32)   DEFAULT NULL COMMENT '名称',
    user_id     BIGINT        NOT NULL COMMENT '用户 id',
    dish_id     BIGINT        DEFAULT NULL COMMENT '菜品 id',
    setmeal_id  BIGINT        DEFAULT NULL COMMENT '套餐 id',
    dish_flavor VARCHAR(50)   DEFAULT NULL COMMENT '口味',
    number      INT           NOT NULL DEFAULT 1 COMMENT '数量',
    amount      DECIMAL(10,2) NOT NULL COMMENT '金额',
    image       VARCHAR(200)  DEFAULT NULL COMMENT '图片',
    create_time DATETIME      NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
) COMMENT '购物车表';

-- ---------------------------- 10. 订单表 ----------------------------
CREATE TABLE orders (
    id                     BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    number                 VARCHAR(50)   NOT NULL COMMENT '订单号',
    status                 INT           NOT NULL DEFAULT 1 COMMENT '订单状态',
    user_id                BIGINT        NOT NULL COMMENT '用户 id',
    address_book_id        BIGINT        DEFAULT NULL COMMENT '地址 id',
    order_time             DATETIME      NOT NULL COMMENT '下单时间',
    checkout_time          DATETIME      DEFAULT NULL COMMENT '结账时间',
    pay_method             INT           NOT NULL COMMENT '支付方式 1 微信 2 支付宝',
    amount                 DECIMAL(10,2) NOT NULL COMMENT '总金额',
    remark                 VARCHAR(100)  DEFAULT NULL COMMENT '备注',
    user_name              VARCHAR(50)   DEFAULT NULL COMMENT '用户名',
    phone                  VARCHAR(11)   DEFAULT NULL COMMENT '手机号',
    address                VARCHAR(200)  DEFAULT NULL COMMENT '地址',
    consignee              VARCHAR(50)   DEFAULT NULL COMMENT '收货人',
    cancel_reason          VARCHAR(200)  DEFAULT NULL COMMENT '取消原因',
    rejection_reason       VARCHAR(200)  DEFAULT NULL COMMENT '拒单原因',
    cancel_time            DATETIME      DEFAULT NULL COMMENT '取消时间',
    estimated_delivery_time DATETIME    DEFAULT NULL COMMENT '预计送达时间',
    delivery_status        INT           DEFAULT 1 COMMENT '配送状态',
    pack_amount            DECIMAL(10,2) DEFAULT NULL COMMENT '打包费',
    tableware_number       INT           DEFAULT NULL COMMENT '餐具数量',
    tableware_status       INT           DEFAULT NULL COMMENT '餐具状态',
    PRIMARY KEY (id),
    UNIQUE KEY uk_number (number)
) COMMENT '订单表';

-- ---------------------------- 11. 订单明细表 ----------------------------
CREATE TABLE order_detail (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(32)   NOT NULL COMMENT '名称',
    image       VARCHAR(200)  DEFAULT NULL COMMENT '图片',
    order_id    BIGINT        NOT NULL COMMENT '订单 id',
    dish_id     BIGINT        DEFAULT NULL COMMENT '菜品 id',
    setmeal_id  BIGINT        DEFAULT NULL COMMENT '套餐 id',
    dish_flavor VARCHAR(50)   DEFAULT NULL COMMENT '口味',
    number      INT           NOT NULL COMMENT '数量',
    amount      DECIMAL(10,2) NOT NULL COMMENT '金额',
    PRIMARY KEY (id)
) COMMENT '订单明细表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 管理员账号：admin / 123456（密码为 123456 的 MD5 值）
INSERT INTO employee (name, username, password, phone, sex, id_number, status,
                      create_time, update_time, create_user, update_user)
VALUES ('管理员', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '13800000000', '1', '11010119900101001X', 1,
        NOW(), NOW(), 1, 1);

-- 示例分类
INSERT INTO category (type, name, sort, status, create_time, update_time, create_user, update_user)
VALUES (1, '热销菜品', 1, 1, NOW(), NOW(), 1, 1),
       (2, '商务套餐', 2, 1, NOW(), NOW(), 1, 1);
