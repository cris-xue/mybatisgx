# MyBatisGX DAO Generation Templates

This document provides templates and guidelines for generating MyBatisGX DAO interfaces.

## Template Selection Guide

| Requirement | Template to Use |
|-------------|----------------|
| Basic CRUD | SimpleDao Template |
| Read-only | SelectDao Template |
| Full CRUD | CrudDao Template |
| Custom methods | Extended DAO Template |
| Batch operations | Batch Operations Template |

## SimpleDao Template

```java
package {{packageName}};

import com.mybatisgx.core.dao.SimpleDao;
import org.springframework.stereotype.Repository;

/**
 * {{EntityName}} DAO interface
 */
@Repository
public interface {{EntityName}}Dao extends SimpleDao<{{EntityName}}, {{EntityName}}Query, {{IdType}}> {
}
```

**Built-in Methods:**
- `insert(entity)`
- `insertSelective(entity)`
- `insertBatch(list)`
- `findById(id)`
- `findOne(query)`
- `findList(query)`
- `findPage(query, pageable)`
- `updateById(entity)`
- `updateByIdSelective(entity)`
- `deleteById(id)`

## SelectDao Template

```java
package {{packageName}};

import com.mybatisgx.core.dao.SelectDao;
import org.springframework.stereotype.Repository;

/**
 * {{EntityName}} DAO interface - read-only
 */
@Repository
public interface {{EntityName}}Dao extends SelectDao<{{EntityName}}, {{EntityName}}Query, {{IdType}}> {
}
```

**Built-in Methods (Read-only):**
- `findById(id)`
- `findOne(query)`
- `findList(query)`
- `findPage(query, pageable)`

## CrudDao Template

```java
package {{packageName}};

import com.mybatisgx.core.dao.CrudDao;
import org.springframework.stereotype.Repository;

/**
 * {{EntityName}} DAO interface - full CRUD
 */
@Repository
public interface {{EntityName}}Dao extends CrudDao<{{EntityName}}, {{EntityName}}Query, {{IdType}}> {
}
```

**Built-in Methods (Full CRUD):**
- All SimpleDao methods
- Additional insert/update/delete operations

## Extended DAO with Custom Methods

### Method Name Query Template

```java
package {{packageName}};

import com.mybatisgx.core.dao.SimpleDao;
import com.mybatisgx.core.model.Page;
import com.mybatisgx.core.model.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * {{EntityName}} DAO interface with custom methods
 */
@Repository
public interface {{EntityName}}Dao extends SimpleDao<{{EntityName}}, {{EntityName}}Query, {{IdType}}> {

    /**
     * Find by {{fieldName}}
     */
    List<{{EntityName}}> findBy{{FieldName}}({{fieldType}} {{fieldName}});

    /**
     * Find by {{fieldName}} like
     */
    List<{{EntityName}}> findBy{{FieldName}}Like(String {{fieldName}});

    /**
     * Find by {{fieldName}} greater than
     */
    List<{{EntityName}}> findBy{{FieldName}}Gt({{fieldType}} {{fieldName}});

    /**
     * Count by {{fieldName}}
     */
    Long countBy{{FieldName}}({{fieldType}} {{fieldName}});

    /**
     * Delete by {{fieldName}}
     */
    int deleteBy{{FieldName}}({{fieldType}} {{fieldName}});

    /**
     * Update by {{fieldName}}
     */
    int update{{UpdateField}}By{{ConditionField}}({{updateType}} {{updateField}}, {{conditionType}} {{conditionField}});
}
```

### Complex Query Examples

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // Simple query
    List<User> findByName(String name);

    // Multiple conditions with AND
    List<User> findByNameAndAge(String name, Integer age);

    // Multiple conditions with OR
    List<User> findByNameOrEmail(String name, String email);

    // Like query
    List<User> findByNameLike(String name);

    // Greater than
    List<User> findByAgeGt(Integer age);

    // Between
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    // In query
    List<User> findByIdIn(List<Long> ids);

    // With sorting
    List<User> findByAgeGtOrderByCreateTimeDesc(Integer age);

    // Top N results
    List<User> findTop10ByAgeGtOrderByCreateTimeDesc(Integer age);

    // Pagination
    Page<User> findByNameLike(String name, Pageable pageable);

    // Count
    Long countByAgeGt(Integer age);

    // Update
    int updateStatusByName(Integer status, String name);

    // Delete
    int deleteByStatus(Integer status);
}
```

## Batch Operations Template

```java
package {{packageName}};

import com.mybatisgx.core.dao.SimpleDao;
import com.mybatisgx.core.annotation.BatchOperation;
import com.mybatisgx.core.annotation.BatchData;
import com.mybatisgx.core.annotation.BatchSize;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * {{EntityName}} DAO interface with batch operations
 */
@Repository
public interface {{EntityName}}Dao extends SimpleDao<{{EntityName}}, {{EntityName}}Query, {{IdType}}> {

    /**
     * Batch insert
     */
    @BatchOperation
    int insertBatch(@BatchData List<{{EntityName}}> entities, @BatchSize int batchSize);

    /**
     * Batch update by ID
     */
    @BatchOperation
    int updateBatchById(@BatchData List<{{EntityName}}> entities, @BatchSize int batchSize);

    /**
     * Batch delete by IDs
     */
    @BatchOperation
    int deleteBatchByIds(@BatchData List<{{IdType}}> ids, @BatchSize int batchSize);
}
```

## QueryEntity Template

Generate QueryEntity alongside DAO:

```java
package {{packageName}}.query;

import {{packageName}}.entity.{{EntityName}};
import com.mybatisgx.core.annotation.QueryEntity;
import java.util.List;

/**
 * Query entity for {{EntityName}}
 */
@QueryEntity({{EntityName}}.class)
public class {{EntityName}}Query extends {{EntityName}} {

    {{#each queryFields}}
    /**
     * {{comment}}
     */
    private {{fieldType}} {{fieldName}};

    {{/each}}
    // Getters and Setters

    {{#each queryFields}}
    public {{fieldType}} get{{FieldName}}() {
        return {{fieldName}};
    }

    public void set{{FieldName}}({{fieldType}} {{fieldName}}) {
        this.{{fieldName}} = {{fieldName}};
    }

    {{/each}}
}
```

### QueryEntity Field Examples

```java
@QueryEntity(User.class)
public class UserQuery extends User {

    // Like queries
    private String nameLike;
    private String emailLike;

    // Comparison queries
    private Integer ageGt;
    private Integer ageLt;
    private Integer ageGteq;
    private Integer ageLteq;

    // Range queries
    private List<Integer> ageBetween;
    private List<LocalDateTime> createTimeBetween;

    // In queries
    private List<Long> idIn;
    private List<String> statusIn;

    // Null checks
    private Boolean nameIsNull;
    private Boolean emailNotNull;

    // Prefix/Suffix
    private String nameStartingWith;
    private String nameEndingWith;

    // Getters and Setters...
}
```

## Complete Example

**Request:** "Create a UserDao with findByNameLike and findByAgeGt methods, plus a UserQuery"

**Generated UserDao:**
```java
package com.example.dao;

import com.example.entity.User;
import com.example.query.UserQuery;
import com.mybatisgx.core.dao.SimpleDao;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * User DAO interface
 */
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    /**
     * Find users by name like
     *
     * @param name name keyword
     * @return user list
     */
    List<User> findByNameLike(String name);

    /**
     * Find users by age greater than
     *
     * @param age minimum age
     * @return user list
     */
    List<User> findByAgeGt(Integer age);
}
```

**Generated UserQuery:**
```java
package com.example.query;

import com.example.entity.User;
import com.mybatisgx.core.annotation.QueryEntity;

/**
 * Query entity for User
 */
@QueryEntity(User.class)
public class UserQuery extends User {

    /**
     * Name like query (LIKE '%?%')
     */
    private String nameLike;

    /**
     * Age greater than query (> ?)
     */
    private Integer ageGt;

    // Getters and Setters

    public String getNameLike() {
        return nameLike;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

    public Integer getAgeGt() {
        return ageGt;
    }

    public void setAgeGt(Integer ageGt) {
        this.ageGt = ageGt;
    }
}
```

## Generation Guidelines

### 1. Package Structure

```
com.example
├── entity/
│   └── User.java
├── query/
│   └── UserQuery.java
└── dao/
    └── UserDao.java
```

### 2. Naming Conventions

- DAO interface: `{EntityName}Dao`
- QueryEntity: `{EntityName}Query`
- Method names follow MyBatisGX conventions

### 3. Import Statements

```java
// DAO imports
import com.mybatisgx.core.dao.SimpleDao;
import com.mybatisgx.core.model.Page;
import com.mybatisgx.core.model.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;

// QueryEntity imports
import com.mybatisgx.core.annotation.QueryEntity;
import java.util.List;  // For In/Between queries

// Batch operations imports
import com.mybatisgx.core.annotation.BatchOperation;
import com.mybatisgx.core.annotation.BatchData;
import com.mybatisgx.core.annotation.BatchSize;
```

### 4. Documentation

- Add JavaDoc for each custom method
- Describe parameters and return values
- Include SQL behavior when helpful

### 5. Type Parameters

```java
SimpleDao<EntityType, QueryEntityType, IdType>
```

- EntityType: The entity class (e.g., User)
- QueryEntityType: The query entity class (e.g., UserQuery)
- IdType: Primary key type (e.g., Long, Integer, String)

## Method Naming Best Practices

### Query Methods

| Pattern | Example | Generated SQL |
|---------|---------|---------------|
| `findBy{Field}` | `findByName` | `WHERE name = ?` |
| `findBy{Field}Like` | `findByNameLike` | `WHERE name LIKE '%?%'` |
| `findBy{Field}Gt` | `findByAgeGt` | `WHERE age > ?` |
| `findBy{Field1}And{Field2}` | `findByNameAndAge` | `WHERE name = ? AND age = ?` |
| `findBy{Field}OrderBy{Field}Desc` | `findByAgeGtOrderByNameDesc` | `WHERE age > ? ORDER BY name DESC` |

### Update Methods

| Pattern | Example | Generated SQL |
|---------|---------|---------------|
| `update{Field1}By{Field2}` | `updateStatusByName` | `UPDATE ... SET status = ? WHERE name = ?` |

### Delete Methods

| Pattern | Example | Generated SQL |
|---------|---------|---------------|
| `deleteBy{Field}` | `deleteByStatus` | `DELETE FROM ... WHERE status = ?` |

### Count Methods

| Pattern | Example | Generated SQL |
|---------|---------|---------------|
| `countBy{Field}` | `countByStatus` | `SELECT COUNT(*) FROM ... WHERE status = ?` |

Use these templates to generate MyBatisGX DAO interfaces based on user requirements!

## @Statement Template

When method names cannot express complex queries, use `@Statement` with MGXQL:

### SELECT Queries

```java
@Mapper
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // Simple condition
    @Statement("select * from User where name = :name and age > :age")
    List<User> findActiveUsers(@Param("name") String name, @Param("age") Integer age);

    // Optional conditions (? prefix = MyBatis <if> tag)
    @Statement("select * from User where ?name = :name and ?age > :age")
    List<User> search(@Param("name") String name, @Param("age") Integer age);

    // Multi-entity JOIN
    @Statement("select user.name, role.name from User user left join Role role on user = role where user.dept = :dept")
    List<UserRoleProjection> findUserRoles(@Param("dept") String dept);

    // Aggregation
    @Statement("select count(*) from User where dept = :dept")
    long countByDept(@Param("dept") String dept);

    @Statement("select avg(age) from User group by dept having avg(age) > :minAvg")
    List<Map<String, Object>> findDeptAvgAge(@Param("minAvg") Integer minAvg);
}
```

### DELETE/UPDATE

```java
@Statement("delete User where name = :name")
void deleteByName(@Param("name") String name);

@Statement("update User where name = :name")
int updateByName(@Param("name") String name, User entity);
```

### Condition Source Selection Guide

| Scenario | Recommended | Example |
|----------|-------------|---------|
| 1-3 simple conditions | Method name | `findByNameAndAge` |
| Dynamic null-skipping conditions | QueryEntity | `findList(UserQuery query)` |
| Dynamic WHERE with multiple optional conditions | MGXSQL (@Lang + @Select) | `@Lang(MgxsqlLanguageDriver.class) @Select("select * from t_user where\n  #id = :id\n  #and name like %:name%")` |
| Dynamic SET with optional fields | MGXSQL (@Lang + @Update) | `@Lang(MgxsqlLanguageDriver.class) @Update("update t_user set[#[name = :name], #[age = :age]] where id = :id")` |
| IN list with auto-foreach | MGXSQL | `@Lang(MgxsqlLanguageDriver.class) @Select("select * from t_user where id in :idList")` |
| LIKE with auto-bind | MGXSQL | `@Lang(MgxsqlLanguageDriver.class) @Select("select * from t_user where #[name like %:name%]")` |
| Multi-entity JOIN | @Statement | `@Statement("select ... from User user left join Role role on user = role ...")` |
| Aggregation + GROUP BY | @Statement | `@Statement("select count(*) from User group by dept having ...")` |
| Custom column projection | @Statement | `@Statement("select user.name, role.name from ...")` |
| Very complex SQL | mapper.xml | Direct XML definition (highest priority) |

## MGXSQL DAO Method Templates

When you need dynamic conditions (optional WHERE/SET), IN with auto-foreach, or LIKE with auto-bind, use MGXSQL with `@Lang` annotation.

### Template 1: Dynamic Query

```java
// Using #[body] — auto-generates isNotEmpty() guard
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where #[name = :name] #[and age > :age]")
List<User> findByNameAndAge(@Param("name") String name, @Param("age") Integer age);

// Using #condition form (must be on its own line)
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where\n  #id = :id\n  #and name = :name")
List<User> findByIdAndName(@Param("id") Long id, @Param("name") String name);

// Custom guard expression
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where #(:type = 1)[status = :status]")
List<User> findByTypeAndStatus(@Param("type") Integer type, @Param("status") String status);
```

### Template 2: Dynamic Update

```java
// Dynamic SET with optional fields
@Lang(MgxsqlLanguageDriver.class)
@Update("update t_user set[#[name = :name], #[age = :age]] where id = :id")
int updateSelective(@Param("name") String name, @Param("age") Integer age, @Param("id") Long id);

// Dynamic SET with custom guard
@Lang(MgxsqlLanguageDriver.class)
@Update("update t_user set[#(name != null)[name = :name], #(age != null)[age = :age]] where id = :id")
int updateSelectiveByGuard(@Param("name") String name, @Param("age") Integer age, @Param("id") Long id);
```

### Template 3: Dynamic Delete

```java
// Dynamic WHERE in DELETE
@Lang(MgxsqlLanguageDriver.class)
@Delete("delete from t_user where #[name = :name] or id in :idList")
int deleteByNameOrIds(@Param("name") String name, @Param("idList") List<Long> idList);
```

### Template 4: IN Query

```java
// Simple IN with auto-foreach
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where id in :idList")
List<User> findByIdList(@Param("idList") List<Long> idList);

// IN with condition guard
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where #[id in :idList]")
List<User> findByIdListOptional(@Param("idList") List<Long> idList);

// Complex type IN with property access
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where id in (item:idList)=>$item.id")
List<User> findByUserList(@Param("idList") List<User> userList);
```

### Template 5: LIKE Query

```java
// Both-side fuzzy LIKE with auto-bind
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where #[name like %:name%]")
List<User> findByNameLike(@Param("name") String name);

// Right-side fuzzy LIKE
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where #[name like :name%]")
List<User> findByNameStartingWith(@Param("name") String name);

// LIKE with other conditions
@Lang(MgxsqlLanguageDriver.class)
@Select("select * from t_user where\n  #name like %:name%\n  #and status = :status")
List<User> findByNameLikeAndStatus(@Param("name") String name, @Param("status") String status);
```

### Template 6: Complete Complex Example

```java
@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    // Multi-optional-condition search with IN and LIKE
    @Lang(MgxsqlLanguageDriver.class)
    @Select("select * from t_user where\n"
          + "  #name like %:name%\n"
          + "  #and dept = :dept\n"
          + "  #and age > :minAge\n"
          + "  #and status in :statusList\n"
          + "  order by create_time desc")
    List<User> search(
        @Param("name") String name,
        @Param("dept") String dept,
        @Param("minAge") Integer minAge,
        @Param("statusList") List<String> statusList
    );

    // Nested conditions
    @Lang(MgxsqlLanguageDriver.class)
    @Select("select * from t_user where id = :id #[and name = :name #[and age > :age]]")
    List<User> findByIdWithOptionalFilters(
        @Param("id") Long id,
        @Param("name") String name,
        @Param("age") Integer age
    );

    // Custom guard with nested conditions
    @Lang(MgxsqlLanguageDriver.class)
    @Select("select * from t_user where #(:type = 1)[#[and category = :category] #(:subType = 2)[#[and tag = :tag]]]")
    List<User> findByTypeWithSubConditions(
        @Param("type") Integer type,
        @Param("category") String category,
        @Param("subType") Integer subType,
        @Param("tag") String tag
    );
}
```

### MGXSQL Import Statements

```java
// MGXSQL imports (add to existing DAO imports)
import com.mybatisgx.ext.scripting.xmltags.MgxsqlLanguageDriver;
import org.apache.ibatis.lang.Lang;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
```
