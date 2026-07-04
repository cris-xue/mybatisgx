# MyBatisGX Advanced Features

This document covers advanced MyBatisGX features: logic delete, optimistic lock, audit fields, composite keys, value generation, and batch operations.

## Logic Delete

Logic delete converts delete operations to update operations, marking data as "deleted" rather than physically removing it.

### Basic Usage

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // Logic delete field
    @LogicDelete
    private Integer status;  // 0: normal, 1: deleted
}
```

**Database Schema:**
```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    status INT DEFAULT 0  -- 0: normal, 1: deleted
);
```

### Behavior

```java
// Call delete method
userDao.deleteById(1L);

// Actual executed SQL
UPDATE user SET status = 1 WHERE id = 1;

// Queries automatically filter deleted data
List<User> users = userDao.findList(query);
// Actual executed SQL
SELECT * FROM user WHERE status = 0 AND ...;
```

### Field Types

**Integer Type:**
```java
@LogicDelete
private Integer status;  // Delete sets to 1
```

**Boolean Type:**
```java
@LogicDelete
private Boolean deleted;  // Delete sets to true
```

### Repeatable Logic Delete

**Problem:** When business allows "delete then re-add same data", normal logic delete causes unique constraint conflicts.

**Solution:** Use `@LogicDeleteId` with `@GeneratedValue`:

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    @LogicDelete
    private Integer status;

    // Logic delete ID
    @LogicDeleteId
    @GeneratedValue(LogicDeleteIdValueProcessor.class)
    private Long logicDeleteId;
}
```

**How It Works:**
```sql
-- Add logic_delete_id column
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    status INT DEFAULT 0,
    logic_delete_id BIGINT,
    UNIQUE (name, logic_delete_id)  -- Composite unique constraint
);

-- Delete updates logic_delete_id
UPDATE user SET status = 1, logic_delete_id = 123 WHERE id = 1;

-- Re-add same name won't conflict
INSERT INTO user (name, status, logic_delete_id) VALUES ('A', 0, NULL);
-- Unique constraint: (A, NULL) ≠ (A, 123)
```

**ValueProcessor Implementation:**
```java
public class LogicDeleteIdValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "logicDeleteId".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.DELETE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        return SnowflakeIdGenerator.nextId();
    }
}
```

## Optimistic Lock

Optimistic lock uses version numbers to handle concurrent updates, checking version match during update to prevent data overwrite.

### Basic Usage

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    private Integer age;

    // Optimistic lock version
    @Version
    private Integer version;
}
```

**Database Schema:**
```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY,
    name VARCHAR(50),
    age INT,
    version INT DEFAULT 0  -- Version, initial value 0
);
```

### How It Works

```
┌─────────────────────────────────────────────────────────────────┐
│                 Optimistic Lock Workflow                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. Query data                                                  │
│     SELECT * FROM user WHERE id = 1                             │
│     Returns: { id: 1, name: 'Zhang', version: 0 }              │
│                                                                 │
│  2. Update data (auto increment version)                        │
│     UPDATE user SET name = 'Li', version = 1                    │
│     WHERE id = 1 AND version = 0                                │
│                                                                 │
│  3. Check update result                                         │
│     - Success: affected rows = 1                                │
│     - Conflict: affected rows = 0 (version modified by another) │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Version Types

**Integer (Recommended):**
```java
@Version
private Integer version;  // Initial 0, +1 each update
```

**Long:**
```java
@Version
private Long version;  // Initial 0, +1 each update
```

### Conflict Handling

**Detect Conflict:**
```java
User user = userDao.findById(1L);
user.setName("New Name");

int rows = userDao.updateById(user);
if (rows == 0) {
    // Version conflict, update failed
    throw new OptimisticLockException("Data modified by another user, please refresh");
}
```

**Retry Mechanism:**
```java
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void updateWithRetry(Long id, String newName) {
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            User user = userDao.findById(id);
            user.setName(newName);

            int rows = userDao.updateById(user);
            if (rows > 0) {
                return;  // Success
            }
            // Conflict, retry
        }
        throw new RuntimeException("Update failed, please retry later");
    }
}
```

## Data Audit

Automatically populate audit fields like creator, creation time, modifier, and modification time.

### Basic Usage

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String name;

    // Creation audit
    @Column(name = "create_by")
    @GeneratedValue(CreateByValueProcessor.class)
    private Long createBy;

    @Column(name = "create_time")
    @GeneratedValue(CreateTimeValueProcessor.class)
    private LocalDateTime createTime;

    // Modification audit
    @Column(name = "update_by")
    @GeneratedValue(UpdateByValueProcessor.class)
    private Long updateBy;

    @Column(name = "update_time")
    @GeneratedValue(UpdateTimeValueProcessor.class)
    private LocalDateTime updateTime;
}
```

### ValueProcessor Implementation

**CreateTimeValueProcessor:**
```java
public class CreateTimeValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "createTime".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT);
    }

    @Override
    public Object process(ValueProcessContext context) {
        return LocalDateTime.now();
    }
}
```

**UpdateTimeValueProcessor:**
```java
public class UpdateTimeValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "updateTime".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.UPDATE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        return LocalDateTime.now();
    }
}
```

**CreateByValueProcessor:**
```java
public class CreateByValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "createBy".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT);
    }

    @Override
    public Object process(ValueProcessContext context) {
        // Get current user from security context
        return SecurityContextHolder.getCurrentUserId();
    }
}
```

## Composite Keys

Support for composite primary keys using `@EmbeddedId` or `@IdClass`.

### Using @EmbeddedId (Recommended)

**Composite Key Class:**
```java
@Embeddable
public class UserRoleId implements Serializable {

    private Long userId;

    private Long roleId;

    // Must implement equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRoleId)) return false;
        UserRoleId that = (UserRoleId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(roleId, that.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}
```

**Entity:**
```java
@Entity
@Table(name = "user_role")
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    private LocalDateTime createTime;

    // getters/setters
}
```

**Usage:**
```java
UserRoleId id = new UserRoleId();
id.setUserId(1L);
id.setRoleId(10L);

UserRole userRole = userDao.findById(id);
```

### Using @IdClass

**Composite Key Class:**
```java
public class UserRoleId implements Serializable {

    private Long userId;
    private Long roleId;

    // Must implement equals() and hashCode()
}
```

**Entity:**
```java
@Entity
@Table(name = "user_role")
@IdClass(UserRoleId.class)
public class UserRole {

    @Id
    private Long userId;

    @Id
    private Long roleId;

    private LocalDateTime createTime;
}
```

## Value Generation

`@GeneratedValue` annotation supports custom value generation strategies.

### Usage

```java
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long id;

    private String name;

    @GeneratedValue(EncryptionValueProcessor.class)
    private String phone;  // Auto-encrypt on insert/update
}
```

### ValueProcessor Interface

```java
public interface ValueProcessor {

    // Whether this processor supports the field
    boolean supports(FieldMeta fieldMeta);

    // Which phases to apply (INSERT, UPDATE, DELETE)
    EnumSet<ValueProcessPhase> phases();

    // Generate value
    Object process(ValueProcessContext context);
}
```

### Example: ID Generator

```java
public class IdValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return fieldMeta.isPrimaryKey();
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT);
    }

    @Override
    public Object process(ValueProcessContext context) {
        return SnowflakeIdGenerator.nextId();
    }
}
```

### Example: Encryption Processor

```java
public class EncryptionValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return "phone".equals(fieldMeta.getJavaColumnName());
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT, ValueProcessPhase.UPDATE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        String rawValue = (String) context.getFieldValue();
        return AesEncryption.encrypt(rawValue);
    }
}
```

## Batch Operations

Support for batch insert, update, and delete operations.

### @BatchOperation Annotation

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // Batch insert
    @BatchOperation
    int insertBatch(@BatchData List<User> users, @BatchSize int batchSize);

    // Batch update
    @BatchOperation
    int updateBatch(@BatchData List<User> users, @BatchSize int batchSize);

    // Batch delete
    @BatchOperation
    int deleteBatchByIds(@BatchData List<Long> ids, @BatchSize int batchSize);
}
```

### Annotation Parameters

| Annotation | Purpose |
|------------|---------|
| `@BatchOperation` | Mark method as batch operation |
| `@BatchData` | Mark the data collection parameter |
| `@BatchSize` | Mark the batch size parameter |

### Usage Example

```java
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void batchInsertUsers(List<User> users) {
        // Batch insert, 500 per batch
        userDao.insertBatch(users, 500);
    }

    public void batchUpdateUsers(List<User> users) {
        // Batch update, 1000 per batch
        userDao.updateBatch(users, 1000);
    }

    public void batchDeleteUsers(List<Long> ids) {
        // Batch delete, 200 per batch
        userDao.deleteBatchByIds(ids, 200);
    }
}
```

### Performance Considerations

- Default batch size: 1000
- Adjust batch size based on database and data size
- Too large: may cause memory issues or exceed database limits
- Too small: more network overhead

## Combining Advanced Features

### Example: Full-Featured Entity

```java
@Entity
@Table(name = "user")
public class User {

    // Primary key with auto-generation
    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long id;

    private String name;

    // Encrypted field
    @GeneratedValue(EncryptionValueProcessor.class)
    private String phone;

    // Logic delete
    @LogicDelete
    private Integer status;

    // Logic delete ID
    @LogicDeleteId
    @GeneratedValue(LogicDeleteIdValueProcessor.class)
    private Long logicDeleteId;

    // Optimistic lock
    @Version
    private Integer version;

    // Audit fields
    @GeneratedValue(CreateByValueProcessor.class)
    private Long createBy;

    @GeneratedValue(CreateTimeValueProcessor.class)
    private LocalDateTime createTime;

    @GeneratedValue(UpdateByValueProcessor.class)
    private Long updateBy;

    @GeneratedValue(UpdateTimeValueProcessor.class)
    private LocalDateTime updateTime;
}
```

## Important Notes

### 1. Logic Delete

- Only one `@LogicDelete` field per entity
- All queries auto-filter deleted data
- Physical delete if custom SQL in mapper.xml

### 2. Optimistic Lock

- Only one `@Version` field per entity
- Always check update return value for conflicts
- Not suitable for batch updates

### 3. Audit Fields

- Register ValueProcessor beans in Spring context
- Implement proper user context handling
- Consider timezone for timestamp fields

### 4. Composite Keys

- Must implement Serializable
- Must implement equals() and hashCode()
- Prefer @EmbeddedId over @IdClass

### 5. Value Generation

- ValueProcessors must be thread-safe
- Consider performance impact for complex generation
- Test encryption/decryption thoroughly

### 6. Batch Operations

- Control batch size appropriately
- Handle exceptions in batch operations carefully
- Consider transaction boundaries

## @QueryColumn Condition Control

### Ignoring Fields in Auto-Condition Generation

By default, all QueryEntity fields participate in auto-generated query conditions. Use `@QueryColumn(ignore=true)` to exclude a field — it will only serve as a parameter carrier for `@Statement` or mapper.xml methods:

```java
@QueryEntity(User.class)
public class UserQuery extends User {
    private String nameLike;        // participates in auto-conditions
    private Integer ageGt;          // participates in auto-conditions

    @QueryColumn(ignore = true)     // excluded from auto-conditions
    private String sortField;       // only used in @Statement or mapper.xml

    @QueryColumn(ignore = true)     // only for custom queries
    private List<Long> excludeIds;
}
```

This is useful when:
- A query field is only meaningful in a custom `@Statement` expression (e.g., `sortField` for dynamic ordering)
- A field is used as a parameter in mapper.xml but should not auto-generate a WHERE condition
- You need to pass extra context to the SQL layer without affecting auto-generated conditions

## Next Steps

- Review best practices for optimal MyBatisGX usage
- Learn troubleshooting techniques for common issues
- Explore performance optimization strategies
