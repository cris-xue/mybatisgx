---
sidebar_position: 3
---

# 增删改查

> 内置 CRUD 方法使用指南

## 新增操作

### insert

插入所有字段。

```java
User user = new User();
user.setName("张三");
user.setAge(25);
userDao.insert(user);
```

生成的 SQL：

```sql
INSERT INTO user (id, name, age) VALUES (?, ?, ?)
```

### insertSelective

只插入非空字段。

```java
User user = new User();
user.setName("张三");
// age 为 null，不会被插入
userDao.insertSelective(user);
```

生成的 SQL：

```sql
INSERT INTO user (id, name) VALUES (?, ?)
```

### insertBatch

批量插入，默认批次大小 1000。

```java
List<User> users = Arrays.asList(
    new User("张三", 25),
    new User("李四", 30)
);
userDao.insertBatch(users);
```

自定义批次大小：

```java
userDao.insertBatch(users, 500);
```

## 查询操作

### findById

根据主键查询。

```java
User user = userDao.findById(1L);
```

### findOne

根据条件查询单条记录。

```java
UserQuery query = new UserQuery();
query.setName("张三");
User user = userDao.findOne(query);
```

### findList

根据条件查询列表。

```java
UserQuery query = new UserQuery();
query.setAgeGt(20);
List<User> users = userDao.findList(query);
```

### findPage

分页查询。

```java
UserQuery query = new UserQuery();
query.setAgeGt(20);

Pageable pageable = new Pageable(1, 10);
pageable.addSort("create_time", "DESC");

Page<User> page = userDao.findPage(query, pageable);

long total = page.getTotal();       // 总记录数
List<User> list = page.getList();   // 当前页数据
```

## 更新操作

### updateById

根据主键更新所有字段。

```java
User user = userDao.findById(1L);
user.setName("王五");
userDao.updateById(user);
```

生成的 SQL：

```sql
UPDATE user SET name = ?, age = ? WHERE id = ?
```

### updateByIdSelective

只更新非空字段。

```java
User user = new User();
user.setId(1L);
user.setName("王五");
// age 为 null，不会被更新
userDao.updateByIdSelective(user);
```

生成的 SQL：

```sql
UPDATE user SET name = ? WHERE id = ?
```

### updateBatchById

批量更新。

```java
List<User> users = Arrays.asList(
    new User(1L, "张三", 26),
    new User(2L, "李四", 31)
);
userDao.updateBatchById(users);
```

## 删除操作

### deleteById

根据主键删除。

```java
userDao.deleteById(1L);
```

### deleteBatchById

批量删除。

```java
List<Long> ids = Arrays.asList(1L, 2L, 3L);
userDao.deleteBatchById(ids);
```

## 方法对照表

### 新增方法

| 方法 | 说明 | 空值处理 |
|------|------|----------|
| `insert` | 插入所有字段 | 插入 NULL |
| `insertSelective` | 选择性插入 | 跳过 NULL |
| `insertBatch` | 批量插入 | 插入 NULL |

### 查询方法

| 方法 | 说明 | 返回类型 |
|------|------|----------|
| `findById` | 主键查询 | ENTITY |
| `findOne` | 条件查单条 | ENTITY |
| `findList` | 条件查列表 | List\<ENTITY\> |
| `findPage` | 分页查询 | Page\<ENTITY\> |

### 更新方法

| 方法 | 说明 | 空值处理 |
|------|------|----------|
| `updateById` | 更新所有字段 | 更新为 NULL |
| `updateByIdSelective` | 选择性更新 | 跳过 NULL |
| `updateBatchById` | 批量更新 | 更新为 NULL |

### 删除方法

| 方法 | 说明 |
|------|------|
| `deleteById` | 主键删除 |
| `deleteBatchById` | 批量删除 |

## 注意事项

1. **Select/Update 方法需要 @Dynamic 注解**：`findOne`、`findList`、`findPage` 已内置 `@Dynamic`，条件字段为空时自动跳过

2. **批量操作性能**：批量操作会循环执行，建议控制批次大小

3. **逻辑删除影响**：如果实体配置了 `@LogicDelete`，delete 操作会变为 update

4. **乐观锁影响**：如果实体配置了 `@Version`，update 操作会自动递增版本号

## 下一步

- 学习 [方法名派生查询](./method-name-query)
- 了解 [分页查询](./pagination)
