---
sidebar_position: 2
---

# DAO 接口

> SimpleDao、CurdDao、SelectDao 接口体系

## 接口层次结构

```
┌─────────────────────────────────────────────────────────────────┐
│                       DAO 接口体系                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│                      ┌─────────────┐                            │
│                      │    Dao      │                            │
│                      └──────┬──────┘                            │
│                             │                                   │
│              ┌──────────────┼──────────────┐                    │
│              │              │              │                    │
│       ┌──────┴──────┐ ┌─────┴─────┐        │                    │
│       │  CurdDao    │ │ SelectDao │        │                    │
│       └──────┬──────┘ └─────┬─────┘        │                    │
│              │              │              │                    │
│              └──────┬───────┘              │                    │
│                     │                      │                    │
│              ┌──────┴──────┐               │                    │
│              │  SimpleDao  │               │                    │
│              └─────────────┘               │                    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## SimpleDao 接口

最常用的接口，组合了 CurdDao 和 SelectDao 的所有方法。

```java
public interface SimpleDao<ENTITY, QUERY_ENTITY, ID extends Serializable>
        extends CurdDao<ENTITY, ID>, SelectDao<ENTITY, QUERY_ENTITY> {
}
```

### 泛型参数

| 参数 | 说明 |
|------|------|
| `ENTITY` | 实体类型 |
| `QUERY_ENTITY` | 查询实体类型 |
| `ID` | 主键类型 |

### 使用示例

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}
```

## CurdDao 接口

提供增删改查基础方法。

```java
public interface CurdDao<ENTITY, ID extends Serializable> extends Dao {

    // 新增
    int insert(ENTITY entity);

    // 批量新增
    int insertBatch(List<ENTITY> entityList);
    int insertBatch(List<ENTITY> entityList, int batchSize);

    // 选择性新增（非空字段）
    int insertSelective(ENTITY entity);

    // 根据ID删除
    int deleteById(ID id);

    // 批量删除
    int deleteBatchById(List<ID> ids);
    int deleteBatchById(List<ID> ids, int batchSize);

    // 根据ID更新
    int updateById(ENTITY entity);

    // 批量更新
    int updateBatchById(List<ENTITY> entityList);
    int updateBatchById(List<ENTITY> entityList, int batchSize);

    // 选择性更新（非空字段）
    int updateByIdSelective(ENTITY entity);

    // 根据ID查询
    ENTITY findById(ID id);
}
```

## SelectDao 接口

提供查询方法。

```java
public interface SelectDao<ENTITY, QUERY_ENTITY> extends Dao {

    // 查询单条
    ENTITY findOne(QUERY_ENTITY entity);

    // 查询列表
    List<ENTITY> findList(QUERY_ENTITY entity);

    // 分页查询
    Page<ENTITY> findPage(QUERY_ENTITY entity, Pageable pageable);
}
```

## 接口选择建议

| 场景 | 推荐接口 | 说明 |
|------|----------|------|
| 简单项目 | `SimpleDao` | 包含所有常用方法 |
| 读写分离（主库） | `CurdDao` | 只包含增删改 |
| 读写分离（从库） | `SelectDao` | 只包含查询 |

### 读写分离示例

```java
// 主库 DAO（写操作）
@Repository
public interface UserWriteDao extends CurdDao<User, Long> {
}

// 从库 DAO（读操作）
@Repository
public interface UserReadDao extends SelectDao<User, UserQuery> {
}
```

## 扩展自定义方法

在 DAO 接口中自定义方法，框架会自动生成 SQL。

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // 根据名称查询
    User findByName(String name);

    // 根据名称和年龄查询
    List<User> findByNameAndAge(String name, Integer age);

    // 根据名称模糊查询
    List<User> findByNameLike(String name);

    // 统计数量
    Long countByAgeGt(Integer age);

    // 更新
    int updateByName(String name, Long id);

    // 删除
    int deleteByName(String name);
}
```

## 注意事项

1. **必须添加 @Repository 或 @Mapper 注解**：框架扫描时识别 DAO 接口

2. **泛型参数顺序固定**：`<ENTITY, QUERY_ENTITY, ID>`

3. **SimpleDao 已包含 CurdDao 和 SelectDao**：无需重复继承

4. **自定义方法支持批量操作**：使用 `@BatchOperation`、`@BatchSize`、`@BatchData` 注解

## 下一步

- 学习 [增删改查](./crud)
- 了解 [方法名派生查询](./method-name-query)
