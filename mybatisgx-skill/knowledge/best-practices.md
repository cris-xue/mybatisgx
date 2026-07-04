# MyBatisGX Best Practices

This document provides best practices, design patterns, and recommendations for using MyBatisGX effectively.

## Project Structure

### Recommended Package Organization

```
com.example.project
├── entity/           # Entity classes
│   ├── User.java
│   └── Org.java
├── query/            # QueryEntity classes
│   ├── UserQuery.java
│   └── OrgQuery.java
├── dao/              # DAO interfaces
│   ├── UserDao.java
│   └── OrgDao.java
├── service/          # Business logic
│   ├── UserService.java
│   └── OrgService.java
├── processor/        # ValueProcessors
│   ├── IdValueProcessor.java
│   └── CreateTimeValueProcessor.java
└── config/           # Configuration
    └── MybatisgxConfig.java
```

## Entity Design

### 1. Keep Entities Clean

Entities should only contain data and relationships:

```java
// ✅ Good: Clean entity
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;
    private String name;
    private Integer age;

    // Only getters/setters, no business logic
}

// ❌ Bad: Entity with business logic
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;

    // Don't put business logic in entities
    public boolean isAdult() {
        return age >= 18;
    }
}
```

### 2. Initialize Collections

Always initialize collection fields to avoid NullPointerException:

```java
@Entity
@Table(name = "org")
public class Org {
    @Id
    private Long id;

    @OneToMany(mappedBy = "org")
    private List<User> userList = new ArrayList<>();  // ✅ Initialize
}
```

### 3. Use Appropriate Field Types

```java
// ✅ Good: Appropriate types
private LocalDateTime createTime;  // For dates
private BigDecimal amount;         // For money
private Boolean active;            // For flags

// ❌ Avoid: Inappropriate types
private Date createTime;           // Use LocalDateTime instead
private Float amount;              // Use BigDecimal for precision
private Integer active;            // Use Boolean for flags
```

### 4. Naming Conventions

| Element | Convention | Example |
|---------|------------|---------|
| Entity class | PascalCase | `User`, `UserDetail` |
| Table name | snake_case | `user`, `user_detail` |
| Java field | camelCase | `userName`, `createTime` |
| Database column | snake_case | `user_name`, `create_time` |

## DAO Design

### 1. Extend Appropriate Interface

Choose the right base interface:

```java
// Read-only operations
public interface UserDao extends SelectDao<User, UserQuery, Long> {
}

// Full CRUD
public interface UserDao extends CrudDao<User, UserQuery, Long> {
}

// Most common: Simple CRUD
public interface UserDao extends SimpleDao<User, UserQuery, Long> {
}
```

### 2. Method Name Best Practices

**Keep method names readable:**

```java
// ✅ Good: Clear and concise
List<User> findByNameLike(String name);
Page<User> findByAgeGt(Integer age, Pageable pageable);

// ❌ Bad: Too long
List<User> findByNameLikeAndAgeGtAndAgeLtAndDeptAndStatusAndCreateTimeBetween(
    String name, Integer minAge, Integer maxAge, String dept, Integer status,
    LocalDateTime startTime, LocalDateTime endTime);
```

**Use QueryEntity for complex queries:**

```java
// ✅ Better: Use QueryEntity
List<User> findList(UserQuery query);

// QueryEntity holds all conditions
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;
    private Integer ageGt;
    private Integer ageLt;
    private List<LocalDateTime> createTimeBetween;
}
```

**Use @Statement for queries beyond method name/QueryEntity capability:**

```java
// ✅ Good: @Statement for JOINs, aggregation, custom projection
@Statement("select user.name, role.name from User user left join Role role on user = role where user.dept = :dept")
List<UserRoleProjection> findUserRoles(@Param("dept") String dept);

// ✅ Good: @Statement for aggregation
@Statement("select count(*) from User group by dept having count(*) > :minCount")
List<Map<String, Object>> findLargeDepts(@Param("minCount") Integer minCount);
```

### Condition Source Selection

| Query Type | Recommended | Why |
|-----------|-------------|-----|
| 1-3 conditions | Method name | Concise, readable |
| 4+ conditions, dynamic null-skipping | QueryEntity | Clean, supports `@Dynamic` |
| Multi-entity JOIN | @Statement | Method name can't express JOINs |
| Aggregation + GROUP BY/HAVING | @Statement | Method name only supports `countBy` |
| Custom column projection | @Statement | Method name returns full entity |

### 3. Pagination Standards

Always use consistent pagination:

```java
// ✅ Good: Consistent pagination pattern
public Page<User> searchUsers(String keyword, int pageNo, int pageSize) {
    UserQuery query = new UserQuery();
    query.setNameLike(keyword);

    Pageable pageable = new Pageable(pageNo, pageSize);
    pageable.addSort("create_time", "DESC");

    return userDao.findPage(query, pageable);
}
```

## Service Layer Best Practices

### 1. Keep Service Layer Clean

Service layer should focus on business logic, not query assembly:

```java
// ❌ Bad: Query assembly in Service
@Service
public class UserService {
    public List<User> search(String name, Integer age) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(User::getName, name);
        }
        if (age != null) {
            wrapper.gt(User::getAge, age);
        }
        return userMapper.selectList(wrapper);
    }
}

// ✅ Good: Clean Service using QueryEntity
@Service
public class UserService {
    public List<User> search(UserQuery query) {
        return userDao.findList(query);
    }
}
```

### 2. Transaction Management

Use `@Transactional` appropriately:

```java
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    // Read-only transaction for queries
    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userDao.findById(id);
    }

    // Regular transaction for updates
    @Transactional
    public void updateUser(User user) {
        userDao.updateById(user);
    }

    // Propagation for nested transactions
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createAuditLog(AuditLog log) {
        auditLogDao.insert(log);
    }
}
```

### 3. Exception Handling

Handle optimistic lock and other exceptions properly:

```java
@Service
public class UserService {

    public void updateWithOptimisticLock(User user) {
        int rows = userDao.updateById(user);
        if (rows == 0) {
            throw new OptimisticLockException("Data has been modified");
        }
    }

    public void updateWithRetry(User user) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                User fresh = userDao.findById(user.getId());
                // Copy fields
                BeanUtils.copyProperties(user, fresh, "id", "version");

                int rows = userDao.updateById(fresh);
                if (rows > 0) {
                    return;
                }
            } catch (Exception e) {
                if (i == maxRetries - 1) {
                    throw e;
                }
            }
        }
        throw new RuntimeException("Update failed after retries");
    }
}
```

## QueryEntity Best Practices

### 1. Extend Entity Class

```java
// ✅ Good: Extend entity to reuse fields
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;
    private Integer ageGt;
}

// ❌ Bad: Duplicate fields
@QueryEntity(User.class)
public class UserQuery {
    private Long id;        // Duplicated
    private String name;    // Duplicated
    private String nameLike;
}
```

### 2. Clear Naming

Use clear suffixes for query fields:

```java
@QueryEntity(User.class)
public class UserQuery extends User {
    // Clear purpose from name
    private String nameLike;           // LIKE '%value%'
    private String nameStartingWith;   // LIKE 'value%'
    private Integer ageGt;             // > value
    private Integer ageLteq;           // <= value
    private List<Long> idIn;           // IN (values)
    private List<LocalDateTime> createTimeBetween;  // BETWEEN
}
```

### 3. Reasonable Defaults

Consider default values and validation:

```java
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;
    private Integer ageGteq = 0;  // Default: >= 0

    // Validation in setter
    public void setAgeGteq(Integer ageGteq) {
        if (ageGteq != null && ageGteq < 0) {
            throw new IllegalArgumentException("Age must be positive");
        }
        this.ageGteq = ageGteq;
    }
}
```

## Performance Optimization

### 1. Choose Right Fetch Mode

```java
// Small result set: JOIN
@OneToOne(fetch = FetchType.EAGER)
@Fetch(FetchMode.JOIN)
private UserDetail userDetail;

// Large result set: BATCH (default recommendation)
@OneToMany(fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)
private List<User> userList;

// Rarely accessed: LAZY
@ManyToOne(fetch = FetchType.LAZY)
private Org org;
```

### 2. Use Selective Methods

```java
// ✅ Good: Only update non-null fields
user.setName("New Name");
userDao.updateByIdSelective(user);

// ❌ Bad: Updates all fields (including nulls)
user.setName("New Name");
userDao.updateById(user);  // Sets other fields to null
```

### 3. Batch Operations

```java
// ✅ Good: Batch insert
List<User> users = prepareUserList();
userDao.insertBatch(users, 500);  // 500 per batch

// ❌ Bad: Loop insert
for (User user : users) {
    userDao.insert(user);  // N database calls
}
```

### 4. Pagination for Large Results

```java
// ✅ Good: Paginated query
Pageable pageable = new Pageable(pageNo, pageSize);
Page<User> page = userDao.findPage(query, pageable);

// ❌ Bad: Load all data
List<User> allUsers = userDao.findList(query);  // May OOM
```

## Relation Query Best Practices

### 1. Avoid Circular References

```java
// ✅ Good: Use @JsonIgnore to prevent circular serialization
@Entity
@Table(name = "org")
public class Org {
    @OneToMany(mappedBy = "org")
    @JsonIgnore  // Prevent circular reference in JSON
    private List<User> userList;
}

@Entity
@Table(name = "user")
public class User {
    @ManyToOne
    @JoinColumn(name = "org_id")
    private Org org;
}
```

### 2. Use mappedBy for Bidirectional

```java
// ✅ Good: Use mappedBy
@Entity
public class Org {
    @OneToMany(mappedBy = "org")  // Let User manage relationship
    private List<User> userList;
}

@Entity
public class User {
    @ManyToOne
    @JoinColumn(name = "org_id")  // Owner side
    private Org org;
}
```

### 3. Transaction for Lazy Loading

```java
// ✅ Good: Access lazy fields within transaction
@Transactional(readOnly = true)
public void processOrg(Long orgId) {
    Org org = orgDao.findById(orgId);
    // Lazy loading works within transaction
    List<User> users = org.getUserList();
    users.forEach(user -> processUser(user));
}

// ❌ Bad: Access outside transaction
public void processOrg(Long orgId) {
    Org org = orgDao.findById(orgId);
    // LazyInitializationException!
    List<User> users = org.getUserList();
}
```

## Configuration Best Practices

### 1. Application Configuration

```yaml
mybatisgx:
  mapper-locations: classpath:mapper/*Mapper.xml
  type-aliases-package: com.example.entity
  configuration:
    map-underscore-to-camel-case: true
    default-fetch-size: 100
    default-statement-timeout: 30

spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

### 2. Scan Configuration

```java
@MybatisgxScan(
    entityBasePackages = "com.example.entity",
    daoBasePackages = "com.example.dao"
)
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. ValueProcessor Registration

```java
@Configuration
public class MybatisgxConfig {

    @Bean
    public IdValueProcessor idValueProcessor() {
        return new IdValueProcessor();
    }

    @Bean
    public CreateTimeValueProcessor createTimeValueProcessor() {
        return new CreateTimeValueProcessor();
    }

    @Bean
    public UpdateTimeValueProcessor updateTimeValueProcessor() {
        return new UpdateTimeValueProcessor();
    }
}
```

## Common Patterns

### Pattern 1: Standard CRUD Service

```java
@Service
@Transactional
public class UserService {

    @Autowired
    private UserDao userDao;

    public User create(User user) {
        userDao.insert(user);
        return user;
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userDao.findById(id);
    }

    public User update(User user) {
        userDao.updateById(user);
        return user;
    }

    public void delete(Long id) {
        userDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<User> search(UserQuery query, int pageNo, int pageSize) {
        Pageable pageable = new Pageable(pageNo, pageSize);
        return userDao.findPage(query, pageable);
    }
}
```

### Pattern 2: Complex Business Logic

```java
@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private InventoryService inventoryService;

    public Order createOrder(OrderRequest request) {
        // 1. Create order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTotalAmount(request.getTotalAmount());
        orderDao.insert(order);

        // 2. Create order items
        List<OrderItem> items = request.getItems().stream()
            .map(item -> {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order.getId());
                orderItem.setProductId(item.getProductId());
                orderItem.setQuantity(item.getQuantity());
                return orderItem;
            })
            .collect(Collectors.toList());
        orderItemDao.insertBatch(items, 100);

        // 3. Update inventory
        inventoryService.deductInventory(items);

        return order;
    }
}
```

### Pattern 3: Read-Write Separation

```java
@Service
public class UserService {

    @Autowired
    @Qualifier("writeUserDao")
    private UserDao writeUserDao;

    @Autowired
    @Qualifier("readUserDao")
    private UserDao readUserDao;

    @Transactional
    public void create(User user) {
        writeUserDao.insert(user);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return readUserDao.findById(id);
    }
}
```

## Testing Best Practices

### 1. Unit Testing with Mock

```java
@SpringBootTest
public class UserServiceTest {

    @MockBean
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Test
    public void testGetById() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Test");

        when(userDao.findById(1L)).thenReturn(mockUser);

        User result = userService.getById(1L);

        assertNotNull(result);
        assertEquals("Test", result.getName());
    }
}
```

### 2. Integration Testing

```java
@SpringBootTest
@Transactional
public class UserDaoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void testInsertAndFind() {
        User user = new User();
        user.setName("Test User");
        user.setAge(25);

        userDao.insert(user);
        assertNotNull(user.getId());

        User found = userDao.findById(user.getId());
        assertEquals("Test User", found.getName());
    }
}
```

## Migration Best Practices

### From MyBatis to MyBatisGX

1. **Keep existing mapper.xml** - They will override framework generation
2. **Gradually add annotations** - Start with simple entities
3. **Use SimpleDao** - Extends existing DAOs one by one
4. **Test thoroughly** - Verify behavior matches original

### From MyBatis-Plus to MyBatisGX

1. **Replace Wrapper with QueryEntity** - More explicit and maintainable
2. **Move query logic to DAO** - Clean up Service layer
3. **Use method name queries** - Simpler than Wrapper for basic queries
4. **Adopt fetch modes** - Better association query performance

## Troubleshooting Checklist

Before asking for help, verify:

- [ ] Entity has `@Entity` and `@Table` annotations
- [ ] DAO extends appropriate base interface
- [ ] `@MybatisgxScan` configured correctly
- [ ] Database schema matches entity definition
- [ ] Version compatibility (Spring Boot 2.x vs 3.x)
- [ ] No conflicting mapper.xml definitions
- [ ] Transaction configuration correct for lazy loading
- [ ] Fetch mode chosen appropriately

## Key Takeaways

1. **Keep entities clean** - Data only, no business logic
2. **Use QueryEntity for complex queries** - Don't overload method names
3. **Choose right fetch mode** - BATCH for most cases
4. **Service layer focuses on business** - Not query assembly
5. **Test with real database** - Integration tests are valuable
6. **Read the generated XML** - Understand what framework does
7. **mapper.xml overrides everything** - Use for complex cases
8. **Performance matters** - Use selective updates and batch operations

Following these best practices will help you build maintainable, performant applications with MyBatisGX!
