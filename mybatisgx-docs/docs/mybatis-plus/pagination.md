---
sidebar_position: 5
---

# 分页

> 共存模式下两套分页机制并存且互不干扰。

## 两条分页链路

共存 starter 同时注册了两个分页拦截器，判别机制完全不同，**天然互斥**：

```
┌─────────────────────────────────────────────────────────────────┐
│   PageHelper PageInterceptor        MybatisPlusInterceptor       │
│   ───────────────────────           ──────────────────────      │
│   判别依据: ThreadLocal LOCAL_PAGE   判别依据: 入参 IPage 实例    │
│   (只有 startPage() 设过)           (只认 selectPage 的入参)     │
│   有 Page → 改写SQL加limit          有 IPage → 改写SQL加limit     │
│   无 Page → 放行                    无 IPage → 放行              │
│           ↑ 互不踩对方的判别场                      ↑            │
│     MyBatisGX 设 ThreadLocal          MP 传 IPage 入参           │
└─────────────────────────────────────────────────────────────────┘
```

| 调用方 | ThreadLocal (PageHelper) | 入参 IPage (MP) | 结果 |
|---|---|---|---|
| MyBatisGX DAO 分页方法（`findPage(Pageable)`） | ✅ RoutingExecutor 调 `startPage()` 设了 | ❌ 参数是 `Pageable`，MP 不认 | 只有 PageHelper 分页 |
| MP `selectPage(IPage, wrapper)` | ❌ 没人调 startPage | ✅ 入参带 IPage | 只有 MP 分页 |
| 普通查询（无分页） | ❌ | ❌ | 双双放行，正常查 |

## 使用方式

```java
// MyBatisGX 分页：findPage（Pageable 参数）
UserQuery query = new UserQuery();
query.setStatus(2);
Page<User> mgxPage = userDao.findPage(query, Pageable.of(1, 2));
long total = mgxPage.getTotal();      // 正确回填总记录数
List<User> list = mgxPage.getList();  // 当前页数据

// MP 分页：selectPage（IPage 入参）
com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> mpPage =
        new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 3);
userMapper.selectPage(mpPage, new QueryWrapper<User>().eq("status", 2));
List<User> mpList = mpPage.getRecords();
```

两套分页在同一 SqlSessionFactory 下各自独立工作，集成测试已验证（`MpCompatCoexistenceTest`：
MyBatisGX `findPage` 分页正确回填 total、MP `selectPage` 独立分页、两页数据不重叠）。

## 依赖说明

- sb3 共存 starter **已内置** `mybatis-plus-jsqlparser`（MP 3.5.9+ 起分页插件移出
  extension，`PaginationInnerInterceptor` 需要 jsqlparser），无需额外引入
- `PageInterceptor`（PageHelper）由共存 starter 自动注册为 `@Bean`

> 注意：MyBatisGX 的 `PageHelper.startPage()` 手动用法在共存模式下同样可用，因为
> `PageInterceptor` 已注册；但更推荐用 `findPage(Pageable)` 内置分页，类型安全。
