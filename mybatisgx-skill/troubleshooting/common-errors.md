# MyBatisGX Common Errors and Solutions

This document provides solutions for common MyBatisGX errors and issues.

## Configuration Issues

### 1. No MyBatisGX Scanner Detected

**Error:**
```
No @MybatisgxScan annotation found
```

**Cause:** Missing `@MybatisgxScan` annotation on the main application class.

**Solution:**
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

### 2. Entity Not Found

**Error:**
```
Entity class not found: com.example.entity.User
```

**Cause:** Entity package not scanned or entity missing `@Entity` annotation.

**Solutions:**
1. Check `entityBasePackages` in `@MybatisgxScan`
2. Ensure entity has `@Entity` annotation
3. Verify package structure matches configuration

### 3. DAO Interface Not Injected

**Error:**
```
Could not autowire. No beans of 'UserDao' type found.
```

**Cause:** DAO package not scanned or missing `@Repository` annotation.

**Solutions:**
1. Check `daoBasePackages` in `@MybatisgxScan`
2. Add `@Repository` annotation to DAO interface
3. Ensure DAO extends a base interface (SimpleDao, CrudDao, etc.)

## Entity Mapping Issues

### 4. Table Not Found

**Error:**
```
Table 'database.user' doesn't exist
```

**Cause:** Table name mismatch between entity and database.

**Solution:**
```java
@Entity
@Table(name = "t_user")  // Specify exact table name
public class User {
    // ...
}
```

### 5. Column Not Found

**Error:**
```
Unknown column 'userName' in 'field list'
```

**Cause:** Column name mismatch or camelCase mapping not enabled.

**Solutions:**
1. Enable camelCase mapping:
```yaml
mybatisgx:
  configuration:
    map-underscore-to-camel-case: true
```

2. Or use `@Column` annotation:
```java
@Column(name = "user_name")
private String userName;
```

### 6. Primary Key Not Defined

**Error:**
```
Primary key not found for entity: User
```

**Cause:** Missing `@Id` annotation on primary key field.

**Solution:**
```java
@Entity
public class User {
    @Id  // Required
    private Long id;
}
```

## Query Issues

### 7. Method Name Not Parsed

**Error:**
```
Could not parse method name: findByNameAndAgeGreaterThan
```

**Cause:** Invalid method name keywords.

**Solution:** Use correct keywords:
```java
// ❌ Wrong
List<User> findByNameAndAgeGreaterThan(String name, Integer age);

// ✅ Correct
List<User> findByNameAndAgeGt(String name, Integer age);
```

### 8. Parameter Count Mismatch

**Error:**
```
Parameter count mismatch: expected 2, got 1
```

**Cause:** Method parameters don't match conditions in method name.

**Solution:**
```java
// Method name has 2 conditions (Name AND Age)
// Must have 2 parameters
List<User> findByNameAndAge(String name, Integer age);
```

### 9. QueryEntity Not Recognized

**Error:**
```
QueryEntity annotation not found on class: UserQuery
```

**Cause:** Missing `@QueryEntity` annotation.

**Solution:**
```java
@QueryEntity(User.class)  // Required
public class UserQuery extends User {
    private String nameLike;
}
```

## Association Query Issues

### 10. N+1 Query Problem

**Symptom:** Multiple queries executed for single list retrieval.

**Cause:** Using `FetchMode.SIMPLE` for collections.

**Solution:** Use `FetchMode.BATCH`:
```java
@OneToMany(mappedBy = "org", fetch = FetchType.EAGER)
@Fetch(FetchMode.BATCH)  // Solves N+1
private List<User> userList;
```

### 11. LazyInitializationException

**Error:**
```
org.hibernate.LazyInitializationException: could not initialize proxy - no Session
```

**Cause:** Accessing lazy-loaded association outside transaction.

**Solution:**
```java
@Transactional(readOnly = true)  // Add transaction
public void processOrg(Long orgId) {
    Org org = orgDao.findById(orgId);
    List<User> users = org.getUserList();  // Now works
}
```

### 12. Circular Reference in JSON

**Error:**
```
Infinite recursion (StackOverflowError)
```

**Cause:** Bidirectional association without @JsonIgnore.

**Solution:**
```java
@Entity
public class Org {
    @OneToMany(mappedBy = "org")
    @JsonIgnore  // Prevent circular reference
    private List<User> userList;
}
```

## Logic Delete Issues

### 13. Multiple @LogicDelete Annotations

**Error:**
```
Multiple @LogicDelete annotations found in entity: User
```

**Cause:** More than one field marked with `@LogicDelete`.

**Solution:** Only one field can be logic delete marker:
```java
@Entity
public class User {
    @LogicDelete
    private Integer status;  // Only one allowed

    // ❌ Don't add another @LogicDelete
}
```

### 14. Logic Delete and Unique Constraint Conflict

**Symptom:** Cannot insert same data after logic delete.

**Cause:** Unique constraint still applies to deleted records.

**Solution:** Use `@LogicDeleteId`:
```java
@Entity
public class User {
    @Id
    private Long id;

    private String username;  // Has UNIQUE constraint

    @LogicDelete
    private Integer status;

    @LogicDeleteId
    @GeneratedValue(LogicDeleteIdValueProcessor.class)
    private Long logicDeleteId;  // Allows re-insert
}
```

## Optimistic Lock Issues

### 15. Update Returns 0 Rows

**Symptom:** `updateById` returns 0, no exception thrown.

**Cause:** Version mismatch due to concurrent update.

**Solution:** Check return value and handle:
```java
int rows = userDao.updateById(user);
if (rows == 0) {
    throw new OptimisticLockException("Data modified by another user");
}
```

### 16. Version Not Auto-Incremented

**Symptom:** Version field stays same after update.

**Cause:** Missing `@Version` annotation.

**Solution:**
```java
@Entity
public class User {
    @Version  // Required for auto-increment
    private Integer version;
}
```

## Batch Operation Issues

### 17. Batch Size Too Large

**Error:**
```
Packet for query is too large
```

**Cause:** Batch size exceeds database limit.

**Solution:** Reduce batch size:
```java
// ❌ Too large
userDao.insertBatch(users, 10000);

// ✅ Reasonable size
userDao.insertBatch(users, 500);
```

### 18. @BatchOperation Not Working

**Symptom:** Batch method executes one by one.

**Cause:** Missing `@BatchData` or `@BatchSize` annotations.

**Solution:**
```java
@BatchOperation
int insertBatch(
    @BatchData List<User> users,  // Required
    @BatchSize int batchSize      // Required
);
```

## Configuration Errors

### 19. Wrong Spring Boot Starter Version

**Error:**
```
NoClassDefFoundError or ClassNotFoundException
```

**Cause:** Using wrong starter for Spring Boot version.

**Solution:**
```xml
<!-- For Spring Boot 2.x -->
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot2-starter</artifactId>
    <version>0.0.1</version>
</dependency>

<!-- For Spring Boot 3.x -->
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot3-starter</artifactId>
    <version>0.0.2</version>
</dependency>
```

### 20. mapper.xml Not Found

**Error:**
```
InputStream is null for mapper XML: UserMapper.xml
```

**Cause:** Mapper XML location not configured correctly.

**Solution:**
```yaml
mybatisgx:
  mapper-locations: classpath:mapper/*Mapper.xml  # Correct path
```

Ensure XML files are in `src/main/resources/mapper/`.

## ValueProcessor Issues

### 21. ValueProcessor Not Applied

**Symptom:** Generated values not populated.

**Cause:** ValueProcessor not registered as Spring bean.

**Solution:**
```java
@Configuration
public class MybatisgxConfig {

    @Bean
    public CreateTimeValueProcessor createTimeValueProcessor() {
        return new CreateTimeValueProcessor();
    }
}
```

### 22. ValueProcessor Wrong Phase

**Symptom:** Value generated at wrong time (e.g., on update instead of insert).

**Cause:** Wrong `ValueProcessPhase` specified.

**Solution:**
```java
public class CreateTimeValueProcessor implements ValueProcessor {

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT);  // Not UPDATE
    }
}
```

## Performance Issues

### 23. Slow Query Performance

**Symptom:** Queries taking too long.

**Possible Causes and Solutions:**

1. **Missing indexes:** Add database indexes on query fields
2. **N+1 problem:** Use `FetchMode.BATCH` instead of `SIMPLE`
3. **Loading too much data:** Use pagination
4. **Fetching unnecessary associations:** Use `FetchType.LAZY`

### 24. Memory Issues with Large Result Sets

**Error:**
```
OutOfMemoryError: Java heap space
```

**Cause:** Loading too many records at once.

**Solutions:**
1. Use pagination:
```java
Page<User> page = userDao.findPage(query, new Pageable(1, 100));
```

2. Use streaming (custom mapper):
```xml
<select id="streamAll" resultType="User" fetchSize="100">
    SELECT * FROM user
</select>
```

## Diagnostic Checklist

When encountering issues, check:

- [ ] Entity has `@Entity` and `@Table` annotations
- [ ] Primary key has `@Id` annotation
- [ ] DAO extends appropriate base interface
- [ ] DAO has `@Repository` annotation
- [ ] `@MybatisgxScan` configured with correct packages
- [ ] Database schema matches entity definition
- [ ] Column names match (check camelCase mapping)
- [ ] Spring Boot starter version matches Spring Boot version
- [ ] Mapper XML location configured correctly
- [ ] ValueProcessors registered as Spring beans
- [ ] Transaction configuration for lazy loading
- [ ] MGXQL expression syntax is correct (see [MGXQL Reference](../knowledge/mgxql.md))
- [ ] @Statement parameter names match `:paramName` references
- [ ] Entity aliases provided in multi-entity queries

## MGXQL Syntax Errors

**25. MGXQL syntax error: "line X:X mismatched input"**
- **Cause**: MGXQL expression violates the grammar rules
- **Common mistakes**: Missing `from` keyword, typo in `left join`, using `LEFT JOIN` (uppercase) instead of `left join`
- **Solution**: Check MGXQL syntax against the [MGXQL Reference](../knowledge/mgxql.md). All keywords are lowercase.

**26. Multi-entity query requires aliases**
- **Cause**: When using `left join`, ALL entities must have aliases
- **Error**: `select * from User left join Role on ...` — `User` has no alias
- **Solution**: Add aliases: `select * from User user left join Role role on user = role`

**27. ON alias not found**
- **Cause**: The alias in the ON clause doesn't match any previously defined entity alias
- **Error**: `on u = role` — alias `u` doesn't exist (entity is aliased as `user`)
- **Solution**: Use the exact alias defined in FROM/JOIN

**28. DELETE/UPDATE WHERE field has alias prefix**
- **Cause**: DML statements don't allow alias prefixes in WHERE conditions
- **Error**: `delete User where user.name = :name` — `user.` prefix not allowed
- **Solution**: Use field name without prefix: `delete User where name = :name`

**29. DELETE/UPDATE missing WHERE clause**
- **Cause**: DML statements require WHERE clause as a safety measure
- **Error**: `delete User` — no WHERE clause
- **Solution**: Always include WHERE conditions: `delete User where name = :name`

## MGXQL Semantic Errors

**30. Entity not found: "EntityName"**
- **Cause**: The entity name in FROM/JOIN doesn't match any `@Entity` class registered with `@MybatisgxScan`
- **Solution**: Verify entity class name matches (UpperCamelCase, e.g., `User` not `user` or `USER`)

**31. Field not found in entity**
- **Cause**: A field referenced in SELECT/WHERE/ORDER BY/GROUP BY doesn't exist in the entity
- **Error**: `select nme from User` — typo in field name `name`
- **Solution**: Check field names match the entity's Java field names (lowerCamelCase)

**32. JOIN relation not found**
- **Cause**: The two entities in `on alias1 = alias2` don't have a declared relationship (`@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`)
- **Solution**: Ensure entity classes have the appropriate relationship annotation between them

**33. Operator type mismatch**
- **Cause**: Comparison operator not compatible with field type
- **Examples**: `like` only for String fields, `between` only for Comparable types, `in` not for Boolean
- **Solution**: Use appropriate operator for the field type

## @Statement Errors

**34. @Statement parameter cannot be bound**
- **Cause**: A `:paramName` reference in the MGXQL expression doesn't match any method parameter
- **Error**: `@Statement("select * from User where name = :userName")` but method has `@Param("name")`
- **Solution**: Ensure `:paramName` matches the `@Param("name")` annotation or the field name in Entity/QueryEntity

**35. @Statement MGXQL parse error at startup**
- **Cause**: Invalid MGXQL expression in `@Statement` annotation
- **Symptom**: Application fails to start with `MybatisgxException: MGXQL语义校验失败`
- **Solution**: Check the MGXQL expression for syntax errors, missing aliases, or non-existent entity/field names
- [ ] Appropriate fetch mode chosen for associations

## Getting More Help

If the issue persists:

1. **Enable SQL logging:**
```yaml
mybatisgx:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

2. **Check generated XML:**
Look for generated XML in classpath or enable debug logging

3. **Verify database connection:**
Test with simple query first

4. **Check MyBatis logs:**
Enable MyBatis debug logging to see SQL execution details

5. **Review documentation:**
Refer to knowledge files for detailed explanations

Remember: Most issues stem from configuration or annotation problems. Double-check the basics first!
