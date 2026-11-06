# 苍穹外卖数据库设计

数据库 `sky_take_out`，字符集 utf8mb4，共 11 张表，按业务域分为四组。

## 一、表清单

| 分组 | 表名 | 说明 |
| --- | --- | --- |
| 基础数据 | employee | 员工 |
| 基础数据 | category | 分类（菜品/套餐） |
| 商品 | dish | 菜品 |
| 商品 | dish_flavor | 菜品口味 |
| 商品 | setmeal | 套餐 |
| 商品 | setmeal_dish | 套餐-菜品关联 |
| 用户交易 | user | C 端用户 |
| 用户交易 | address_book | 收货地址簿 |
| 用户交易 | shopping_cart | 购物车 |
| 订单 | orders | 订单主表 |
| 订单 | order_detail | 订单明细 |

## 二、表关系说明

```
category 1 ── n dish
category 1 ── n setmeal
dish     1 ── n dish_flavor
setmeal  n ── n dish            （通过 setmeal_dish 关联）
user     1 ── n address_book
user     1 ── n shopping_cart
user     1 ── n orders
orders   1 ── n order_detail
```

- **setmeal_dish**：解决套餐与菜品的多对多关系，冗余 name/price 并记录份数 copies。
- **order_detail**：订单快照，下单时固化商品名称、价格、口味，即使后续商品改价或删除也不影响历史订单。

## 三、关键表设计要点

### employee 员工表

- username 唯一约束，登录时按账号查询。
- 密码存 MD5 摘要，不存明文；status 控制账号启用/禁用。
- 初始管理员：admin / 123456（MD5：e10adc3949ba59abbe56e057f20f883e）。

### category 分类表

- type 区分：1 菜品分类、2 套餐分类。
- sort 控制前端展示顺序，name 唯一。

### dish / dish_flavor

- price 使用 DECIMAL(10,2)，避免浮点精度问题。
- flavor 的 value 存 JSON 数组字符串，如 `["原味","微辣"]`。

### orders 订单表

- number 订单号唯一，status 对应订单状态枚举（1 待付款 ~ 7 退款）。
- 冗余收货人、地址、电话、用户名等信息，地址变更不影响历史订单。
- 记录取消/拒单原因与时间，支持完整状态追溯。

### shopping_cart 购物车

- 通过 dish_id 或 setmeal_id 区分菜品/套餐条目，dish_flavor 记录具体口味。
- 同一用户同一商品同一口味只保留一条，重复添加累加 number。

## 四、公共字段约定

除 user、shopping_cart 等表外，业务表统一包含四个审计字段，由 AOP 自动填充：

| 字段 | 含义 | 填充时机 |
| --- | --- | --- |
| create_time | 创建时间 | INSERT |
| update_time | 最后修改时间 | INSERT / UPDATE |
| create_user | 创建人 id | INSERT |
| update_user | 最后修改人 id | INSERT / UPDATE |

## 五、约束与索引

- 主键统一 BIGINT 自增。
- 唯一约束：employee.username、category.name、dish.name、setmeal.name、orders.number。
- 关联查询字段（category_id、dish_id、user_id、order_id 等）在数据量增长后可按实际查询补充普通索引。

## 六、初始化方式

```bash
# 方式一：Docker 启动时自动执行 sql/sky_take_out.sql
docker compose up -d

# 方式二：手动导入
mysql -u root -p < sql/sky_take_out.sql
```
