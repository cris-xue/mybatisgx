---
title: MyBatis、MyBatis-Plus、JPA、MyBatisGX 写法比较：同一个需求，四种解法
authors: [XueChengCheng]
tags: [mybatis, mybatis-plus, jpa, 架构, orm]
description: 从 MyBatis Plus、JPA 到 MyBatis 的选择困境，以及 MyBatisGX 的解决之道
---

> 用两个真实场景，看看四种框架在日常开发中的真实体感。

---

## 场景 1：一个"普通"的用户管理需求

产品经理说，用户模块需要支持：
- **新增**用户
- **查询**用户详情（带出 1:1 的用户详情关联）
- **列表查询**（姓名模糊、年龄区间、状态、注册时间范围、分页）
- **更新**用户状态（按部门 ID 批量更新）
- **删除**用户

这几乎是每个后端都写过的需求，足够把各框架的体感完全暴露出来。

---

### MyBatis 写法

#### 1. 查询

`UserMapper.xml` 核心片段：

```xml
<select id="findUserList" resultMap="userResultMap">
    select u.*, d.phone, d.address from user u
    left join user_detail d on u.id = d.user_id
    <where>
        <if test="name != null">and u.name like concat('%', #{name}, '%')</if>
        <if test="minAge != null">and u.age >= #{minAge}</if>
        <if test="maxAge != null">and u.age <= #{maxAge}</if>
        <if test="status != null">and u.status = #{status}</if>
        <if test="startTime != null">and u.create_time >= #{startTime}</if>
        <if test="endTime != null">and u.create_time <= #{endTime}</if>
    </where>
</select>
```

再加上 `<resultMap>` 做关联映射、Param 对象、Service 分页拼接……查询 alone 就要写约 50 行 XML。

#### 2. 增删改

```xml
<insert id="insert">insert into user(...) values(...)</insert>
<update id="updateStatusByDeptId">update user set status=#{status} where dept_id=#{deptId}</update>
<delete id="deleteById">delete from user where id=#{id}</delete>
```

增删改本身不复杂，但每个方法都要在 XML 里写一遍，重复劳动不少。

---

### MyBatis-Plus 写法

#### 1. 列表查询

Service 层常见写法：

```java
LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery();
wrapper.like(StringUtils.isNotBlank(name), User::getName, name)
       .ge(minAge != null, User::getAge, minAge)
       .le(maxAge != null, User::getAge, maxAge)
       .eq(status != null, User::getStatus, status)
       .ge(startTime != null, User::getCreateTime, startTime)
       .le(endTime != null, User::getCreateTime, endTime);

Page<User> page = userMapper.selectPage(new Page<>(current, size), wrapper);
```

关联查询？MP 基本要回退到手写 SQL 或自己组装数据。

#### 2. 增删改

```java
// 增
userMapper.insert(user);

// 查单条
userMapper.selectById(id);

// 改
LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.set(User::getStatus, newStatus).eq(User::getDeptId, deptId);
userMapper.update(null, updateWrapper);

// 删
userMapper.deleteById(id);
```

单表增删查很顺手，但**更新照样要在 Service 层拼 Wrapper**。

---

### JPA 写法

#### 1. 列表查询

方法名查询：

```java
Page<User> findByNameContainingAndAgeBetweenAndStatusAndCreateTimeBetween(
    String name, Integer minAge, Integer maxAge, 
    Integer status, Date startTime, Date endTime, Pageable pageable);
```

或者用 Specification：

```java
Specification<User> spec = (root, query, cb) -> {
    List<Predicate> predicates = new ArrayList<>();
    if (name != null) predicates.add(cb.like(root.get("name"), "%" + name + "%"));
    if (minAge != null && maxAge != null) 
        predicates.add(cb.between(root.get("age"), minAge, maxAge));
    // ... 省略类似条件
    return cb.and(predicates.toArray(new Predicate[0]));
};
Page<User> page = userRepository.findAll(spec, pageable);
```

#### 2. 增删改

```java
// 增/改
userRepository.save(user);

// 查单条
userRepository.findById(id);

// 按条件更新（需要写 JPQL）
@Modifying
@Query("UPDATE User u SET u.status = :status WHERE u.deptId = :deptId")
int updateStatusByDeptId(@Param("deptId") Long deptId, @Param("status") Integer status);

// 删
userRepository.deleteById(id);
```

关联查询可以在实体上配 `@OneToOne`，但 LAZY 容易触发 N+1，EAGER 又可能拖出不需要的数据。

---

### MyBatisGX 写法

#### 1. 只需要定义这些

实体（带关联）：

```java
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;
    private String name;
    private Integer age;
    private Integer status;
    private Long deptId;
    
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private UserDetail userDetail;
}
```

查询实体：

```java
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;
    private List<Integer> ageBetween;
    private List<Date> createTimeBetween;
}
```

DAO 接口——**注意：这里不需要写任何自定义方法**：

```java
@Mapper
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}
```

#### 2. Service 层写法

```java
// 增
userDao.insert(user);

// 查单条（自动带出 userDetail，BATCH 模式解决 N+1）
userDao.findById(id);

// 列表查询 —— 这是内置方法，直接传 QueryEntity 就行
Pageable pageable = new Pageable(pageNum, pageSize);
userDao.findPage(userQuery, pageable);

// 按部门 ID 更新状态
User updateData = new User();
updateData.setStatus(newStatus);
userDao.updateByDeptId(deptId, updateData);

// 删
userDao.deleteById(id);
```

#### 3. 为什么列表查询不需要写方法？

因为 `SimpleDao` 已经内置了：

```java
@Dynamic
Page<ENTITY> findPage(ENTITY entity, Pageable pageable);
```

`UserQuery` 中的字段命名会自动映射成查询条件：
- `nameLike` → `name LIKE '%value%'`
- `ageBetween` → `age BETWEEN min AND max`
- `createTimeBetween` → `create_time BETWEEN start AND end`
- `status` → `status = value`

`@Dynamic` 注解会让 null 字段自动不参与 WHERE 拼接。

---

## 场景 2：产品经理改需求了

本来以为这个需求已经够"标准"了。

但上线两周后，产品经理改需求了：
- 状态从等值变**多值**
- 用户名称从等值变**模糊**
- 用户需要带**角色**（多对多）
- 角色页要**反查用户**

关系从 1:1 变成了 **多对多**。看看四个框架分别要改多少代码。

---

### MyBatis 改写

#### 1. 状态等值改多值（IN 查询）

XML 里找 `findUserList`：

```xml
<!-- 之前 -->
<if test="status != null">and u.status = #{status}</if>

<!-- 之后 -->
<if test="statusList != null and statusList.size() > 0">
    and u.status in
    <foreach collection="statusList" item="s" open="(" separator="," close=")">
        #{s}
    </foreach>
</if>
```

Param 对象里的 `Integer status` 也要改成 `List<Integer> statusList`。

#### 2. 名称从等值改模糊

如果之前用的是 `=`，现在改成 `like concat('%', #{name}, '%')`。

#### 3. 加多对多关联

需要在 `UserMapper.xml` 里写新的 `resultMap`，加入 `roleList` 的 `collection` 映射，再写一条带 `JOIN user_role`、`JOIN role` 的 SQL。_role 查用户_ 反向也要再写一套 ResultMap 和 SQL。

约新增 40~60 行 XML。

**痛点**：改一个查询条件 + 加一层关联，Param → XML → ResultMap 全要动。

---

### MyBatis-Plus 改写

#### 1. 状态等值改多值

找到 Service 里的 Wrapper：

```java
// 之前
.eq(status != null, User::getStatus, status)

// 之后
.in(statusList != null && !statusList.isEmpty(), User::getStatus, statusList)
```

#### 2. 名称改模糊

```java
// 之前可能用了 .eq()
// 之后
.like(StringUtils.isNotBlank(name), User::getName, name)
```

#### 3. 加多对多关联

MP 不原生支持多对多自动关联。方案：
- 手写 SQL 带多表 JOIN
- 或者查完用户后，再循环查角色列表手动塞进去（又会触发 N+1）

**痛点**：查询条件一改，所有拼过这个 Wrapper 的 Service 方法都要检查；关联查询仍是短板。

---

### JPA 改写

#### 1. 状态等值改多值 + 名称改模糊

如果你用的是方法名查询：

```java
// 之前
Page<User> findByNameContainingAndAgeBetweenAndStatusAndCreateTimeBetween(...)

// 之后
Page<User> findByNameContainingAndAgeBetweenAndStatusInAndCreateTimeBetween(...)
```

方法名又变长一截。如果用 Specification，改 `cb.equal` → `cb.in` 和 `cb.like`，但要找到所有复用了这个条件的地方。

#### 2. 加多对多关联

实体改造：

```java
@Entity
public class User {
    // ...
    @ManyToMany(mappedBy = "userList", fetch = FetchType.LAZY)
    private List<Role> roleList;
}

@Entity
public class Role {
    // ...
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> userList;
}
```

关联声明确实优雅，但一不留神就会触发 N+1：查 10 个用户，每个用户再去查角色，变成 11 条甚至更多 SQL。要加 `@EntityGraph` 或 `JOIN FETCH` 才能缓解，分页时又有新坑。

**痛点**：条件一变，方法名爆炸；关联加了，但 SQL 黑盒和 N+1 的焦虑也来了。

---

### MyBatisGX 改写

#### 1. 状态等值改多值 + 名称改模糊

打开 `UserQuery`，改两个字段后缀：

```java
// 之前
private Integer status;

// 之后
private List<Integer> statusIn;
```

名称本来就是 `nameLike`，不需要改。

**DAO 不需要改，Service 不需要改。** 因为 `findPage(userQuery, pageable)` 是内置方法，框架会自动把 `statusIn` 解析成 `status IN (...)`。

#### 2. 加多对多关联

实体上直接加注解：

```java
@Entity
@Table(name = "user")
public class User {
    // ...
    @ManyToMany(mappedBy = "userList", fetch = FetchType.LAZY)
    @Fetch(FetchMode.BATCH)
    private List<Role> roleList;
}

@Entity
@Table(name = "role")
public class Role {
    @Id
    private Long id;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Fetch(FetchMode.BATCH)
    private List<User> userList;
}
```

然后：

```java
// 查用户详情（自动带出 userDetail + roleList）
userDao.findById(userId);

// 查角色详情（自动带出 userList）
roleDao.findById(roleId);
```

**不需要写 ResultMap，不需要写 JOIN SQL。** `FetchMode.BATCH` 默认会自动解决 N+1：先查主表，再批量用 `IN` 查关联表，最后组装数据。

**一句话总结**：需求变了，MyBatisGX 的改动最小，而且关联查询的稳定性是可预期的。

---

## 框架优痛点分析

### MyBatis
- ✅ SQL 完全可控，复杂查询最稳
- ❌ 改一个字段，Param → XML → ResultMap 全链路改
- ❌ 动态 SQL 一多，XML 迅速膨胀

### MyBatis-Plus
- ✅ 单表 CRUD 效率很高，不用写 XML
- ❌ 业务层被查询/更新逻辑严重侵入
- ❌ Wrapper 到处传，DAO 抽象边界逐渐失效
- ❌ 关联查询是明显短板

### JPA
- ✅ CRUD 极其舒服，面向对象体验好
- ❌ SQL 黑盒，优化全靠猜
- ❌ N+1 问题防不胜防
- ❌ 复杂查询方法名长到离谱，最终往往还是写原生 SQL

### MyBatisGX
- ✅ Service 层零查询逻辑，所有条件收敛到 QueryEntity
- ✅ 关联查询不用写 SQL / ResultMap，声明式关联 + BATCH 抓取策略 N+1 开箱解决
- ✅ 分页、批量都是内置能力
- ⚠️ 超复杂的多表聚合统计，仍然可以用 mapper.xml 兜底（优先级最高）

---

## 升维对比：四种框架，本质在解决什么？

| 框架 | 核心解决的问题 | 典型代价 |
|---|---|---|
| MyBatis | SQL 完全可控 | 重复代码、维护成本高 |
| MyBatis-Plus | 单表开发效率 | 业务层侵入、复杂查询乏力 |
| JPA | 对象建模与 CRUD 体验 | SQL 黑盒、复杂场景失控 |
| MyBatisGX | **查询定义与业务解耦** | 需要适应新的设计规范 |

更进一步看 CRUD + 关联的支持：

| 能力 | MyBatis | MP | JPA | MyBatisGX |
|---|---|---|---|---|
| 单表增删改 | 需手写 XML | 内置，快捷 | 内置，优雅 | 内置，简洁 |
| 复杂条件查询 | XML 冗长 | Service 拼 Wrapper | Specification/长方法名 | **QueryEntity 直接传** |
| 1:1 关联查询 | 手写 ResultMap | 需手写 SQL | 声明式，但 N+1 隐患 | **声明式 + BATCH 自动解决** |
| 按条件更新 | 手写 XML | Service 拼 UpdateWrapper | 需写 JPQL | **方法名自动生成** |
| 分页 | 手动处理 | 内置 | 内置 | **内置，对关联友好** |
| 需求变更成本 | 高 | 中 | 中 | **低** |

三种框架都在做"取舍"，而 MyBatisGX 试图回答的问题是：

> 能不能既保留 MyBatis 的 SQL 可控性，又拥有 JPA 的声明式关联体验，同时避免 Wrapper/Specification 对业务层的侵蚀？

---

## 总结

如果你也遇到过这些困境：
- 写 MyBatis 写到手麻，每个查询都要复制粘贴 XML
- 用 MyBatis-Plus 用到业务层一团乱，Wrapper 满天飞
- 用 JPA 被黑盒 SQL 和 N+1 折磨到半夜调优

那或许可以了解一下 MyBatisGX 的思路：**把查询还给 DAO，把业务还给 Service。**

有的框架在“改实现”
有的框架在“改代码结构”

而 MyBatisGX，只是在“改定义”。

- 项目文档地址：[http://www.mybatisgx.com/](http://www.mybatisgx.com/) 
- 项目已经开源github：[https://github.com/cris-xue/mybatisgx](https://github.com/cris-xue/mybatisgx)
- demo：[https://github.com/cris-xue/mybatisgx-example](https://github.com/cris-xue/mybatisgx-example)