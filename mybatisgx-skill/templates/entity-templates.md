# MyBatisGX Entity Generation Templates

This document provides templates and guidelines for generating MyBatisGX entity classes.

## Template Selection Guide

| Requirement | Template to Use |
|-------------|----------------|
| Basic entity | Basic Entity Template |
| With logic delete | Logic Delete Template |
| With optimistic lock | Optimistic Lock Template |
| With audit fields | Audit Template |
| With associations | Association Template |
| Full-featured | Full-Featured Template |
| Composite key | Composite Key Template |

## Basic Entity Template

```java
package {{packageName}};

import javax.persistence.*;
import java.io.Serializable;

/**
 * {{entityComment}}
 */
@Entity
@Table(name = "{{tableName}}")
public class {{EntityName}} implements Serializable {

    @Id
    private {{idType}} id;

    {{#each fields}}
    {{#if columnName}}
    @Column(name = "{{columnName}}")
    {{/if}}
    private {{fieldType}} {{fieldName}};

    {{/each}}
    // Getters and Setters

    {{#each fields}}
    public {{fieldType}} get{{FieldName}}() {
        return {{fieldName}};
    }

    public void set{{FieldName}}({{fieldType}} {{fieldName}}) {
        this.{{fieldName}} = {{fieldName}};
    }

    {{/each}}
}
```

### Example Usage

**Input:**
- Entity name: User
- Table name: user
- Fields: id (Long), name (String), age (Integer)

**Generated:**
```java
package com.example.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User Entity
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    private Long id;

    private String name;

    private Integer age;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
```

## Logic Delete Template

```java
package {{packageName}};

import javax.persistence.*;
import com.mybatisgx.core.annotation.LogicDelete;
import java.io.Serializable;

/**
 * {{entityComment}} - with logic delete
 */
@Entity
@Table(name = "{{tableName}}")
public class {{EntityName}} implements Serializable {

    @Id
    private {{idType}} id;

    {{#each fields}}
    private {{fieldType}} {{fieldName}};

    {{/each}}
    /**
     * Logic delete field (0: normal, 1: deleted)
     */
    @LogicDelete
    private Integer status;

    // Getters and Setters
    {{#each fields}}
    public {{fieldType}} get{{FieldName}}() {
        return {{fieldName}};
    }

    public void set{{FieldName}}({{fieldType}} {{fieldName}}) {
        this.{{fieldName}} = {{fieldName}};
    }

    {{/each}}
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
```

## Optimistic Lock Template

```java
package {{packageName}};

import javax.persistence.*;
import java.io.Serializable;

/**
 * {{entityComment}} - with optimistic lock
 */
@Entity
@Table(name = "{{tableName}}")
public class {{EntityName}} implements Serializable {

    @Id
    private {{idType}} id;

    {{#each fields}}
    private {{fieldType}} {{fieldName}};

    {{/each}}
    /**
     * Version for optimistic locking
     */
    @Version
    private Integer version;

    // Getters and Setters
    {{#each fields}}
    public {{fieldType}} get{{FieldName}}() {
        return {{fieldName}};
    }

    public void set{{FieldName}}({{fieldType}} {{fieldName}}) {
        this.{{fieldName}} = {{fieldName}};
    }

    {{/each}}
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
```

## Audit Template

```java
package {{packageName}};

import javax.persistence.*;
import com.mybatisgx.core.annotation.GeneratedValue;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * {{entityComment}} - with audit fields
 */
@Entity
@Table(name = "{{tableName}}")
public class {{EntityName}} implements Serializable {

    @Id
    private {{idType}} id;

    {{#each fields}}
    private {{fieldType}} {{fieldName}};

    {{/each}}
    /**
     * Creator
     */
    @Column(name = "create_by")
    @GeneratedValue(CreateByValueProcessor.class)
    private Long createBy;

    /**
     * Creation time
     */
    @Column(name = "create_time")
    @GeneratedValue(CreateTimeValueProcessor.class)
    private LocalDateTime createTime;

    /**
     * Modifier
     */
    @Column(name = "update_by")
    @GeneratedValue(UpdateByValueProcessor.class)
    private Long updateBy;

    /**
     * Modification time
     */
    @Column(name = "update_time")
    @GeneratedValue(UpdateTimeValueProcessor.class)
    private LocalDateTime updateTime;

    // Getters and Setters (omitted for brevity)
}
```

## Association Template

### One-to-One

```java
@Entity
@Table(name = "{{tableName}}")
public class {{EntityName}} implements Serializable {

    @Id
    private {{idType}} id;

    // One-to-One association
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "{{foreignKeyColumn}}")
    @Fetch(FetchMode.BATCH)
    private {{AssociatedEntity}} {{associatedField}};

    // Other fields and methods
}
```

### One-to-Many

```java
@Entity
@Table(name = "{{tableName}}")
public class {{EntityName}} implements Serializable {

    @Id
    private {{idType}} id;

    // One-to-Many association
    @OneToMany(mappedBy = "{{mappedByField}}", fetch = FetchType.EAGER)
    @Fetch(FetchMode.BATCH)
    private List<{{AssociatedEntity}}> {{associatedList}} = new ArrayList<>();

    // Other fields and methods
}
```

### Many-to-One

```java
@Entity
@Table(name = "{{tableName}}")
public class {{EntityName}} implements Serializable {

    @Id
    private {{idType}} id;

    // Many-to-One association
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "{{foreignKeyColumn}}")
    private {{AssociatedEntity}} {{associatedField}};

    // Other fields and methods
}
```

### Many-to-Many

```java
@Entity
@Table(name = "{{tableName}}")
public class {{EntityName}} implements Serializable {

    @Id
    private {{idType}} id;

    // Many-to-Many association
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "{{joinTableName}}",
        joinColumns = @JoinColumn(name = "{{joinColumn}}"),
        inverseJoinColumns = @JoinColumn(name = "{{inverseJoinColumn}}")
    )
    @Fetch(FetchMode.BATCH)
    private List<{{AssociatedEntity}}> {{associatedList}} = new ArrayList<>();

    // Other fields and methods
}
```

## Full-Featured Template

```java
package {{packageName}};

import javax.persistence.*;
import com.mybatisgx.core.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * {{entityComment}} - full-featured entity
 */
@Entity
@Table(name = "{{tableName}}")
public class {{EntityName}} implements Serializable {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(IdValueProcessor.class)
    private {{idType}} id;

    {{#each fields}}
    /**
     * {{fieldComment}}
     */
    {{#if columnName}}
    @Column(name = "{{columnName}}")
    {{/if}}
    private {{fieldType}} {{fieldName}};

    {{/each}}
    /**
     * Logic delete status (0: normal, 1: deleted)
     */
    @LogicDelete
    private Integer status;

    /**
     * Logic delete ID (for repeatable logic delete)
     */
    @LogicDeleteId
    @GeneratedValue(LogicDeleteIdValueProcessor.class)
    private Long logicDeleteId;

    /**
     * Version for optimistic locking
     */
    @Version
    private Integer version;

    /**
     * Creator
     */
    @Column(name = "create_by")
    @GeneratedValue(CreateByValueProcessor.class)
    private Long createBy;

    /**
     * Creation time
     */
    @Column(name = "create_time")
    @GeneratedValue(CreateTimeValueProcessor.class)
    private LocalDateTime createTime;

    /**
     * Modifier
     */
    @Column(name = "update_by")
    @GeneratedValue(UpdateByValueProcessor.class)
    private Long updateBy;

    /**
     * Modification time
     */
    @Column(name = "update_time")
    @GeneratedValue(UpdateTimeValueProcessor.class)
    private LocalDateTime updateTime;

    // Getters and Setters (omitted for brevity)
}
```

## Composite Key Template

### Using @EmbeddedId

**Composite Key Class:**
```java
package {{packageName}};

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for {{EntityName}}
 */
@Embeddable
public class {{EntityName}}Id implements Serializable {

    {{#each keyFields}}
    private {{fieldType}} {{fieldName}};

    {{/each}}
    // Constructors

    public {{EntityName}}Id() {
    }

    public {{EntityName}}Id({{#each keyFields}}{{fieldType}} {{fieldName}}{{#unless @last}}, {{/unless}}{{/each}}) {
        {{#each keyFields}}
        this.{{fieldName}} = {{fieldName}};
        {{/each}}
    }

    // Getters and Setters

    {{#each keyFields}}
    public {{fieldType}} get{{FieldName}}() {
        return {{fieldName}};
    }

    public void set{{FieldName}}({{fieldType}} {{fieldName}}) {
        this.{{fieldName}} = {{fieldName}};
    }

    {{/each}}
    // equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof {{EntityName}}Id)) return false;
        {{EntityName}}Id that = ({{EntityName}}Id) o;
        return {{#each keyFields}}Objects.equals({{fieldName}}, that.{{fieldName}}){{#unless @last}} &&
               {{/unless}}{{/each}};
    }

    @Override
    public int hashCode() {
        return Objects.hash({{#each keyFields}}{{fieldName}}{{#unless @last}}, {{/unless}}{{/each}});
    }
}
```

**Entity:**
```java
package {{packageName}};

import javax.persistence.*;
import java.io.Serializable;

/**
 * {{entityComment}} - with composite key
 */
@Entity
@Table(name = "{{tableName}}")
public class {{EntityName}} implements Serializable {

    @EmbeddedId
    private {{EntityName}}Id id;

    {{#each fields}}
    private {{fieldType}} {{fieldName}};

    {{/each}}
    // Getters and Setters

    public {{EntityName}}Id getId() {
        return id;
    }

    public void setId({{EntityName}}Id id) {
        this.id = id;
    }

    {{#each fields}}
    public {{fieldType}} get{{FieldName}}() {
        return {{fieldName}};
    }

    public void set{{FieldName}}({{fieldType}} {{fieldName}}) {
        this.{{fieldName}} = {{fieldName}};
    }

    {{/each}}
}
```

## Field Type Mapping

| Java Type | Database Type | Usage |
|-----------|---------------|-------|
| `Long` | BIGINT | IDs, large numbers |
| `Integer` | INT | Counts, small numbers |
| `String` | VARCHAR | Text fields |
| `Boolean` | TINYINT / BOOLEAN | Flags |
| `BigDecimal` | DECIMAL | Money, precise numbers |
| `LocalDateTime` | DATETIME / TIMESTAMP | Date and time |
| `LocalDate` | DATE | Date only |
| `LocalTime` | TIME | Time only |
| `byte[]` | BLOB | Binary data |

## Generation Guidelines

### 1. Always Include

- Package declaration
- Import statements
- @Entity and @Table annotations
- Serializable implementation
- @Id annotation on primary key
- Getters and setters

### 2. Optional Based on Requirements

- @Column annotations (if column name differs from field name)
- @LogicDelete annotation
- @Version annotation
- Audit fields with @GeneratedValue
- Association annotations

### 3. Best Practices

- Use `implements Serializable` for all entities
- Initialize collection fields: `= new ArrayList<>()`
- Add JavaDoc comments for fields
- Use appropriate field types
- Follow naming conventions (camelCase for Java, snake_case for database)

### 4. Imports to Include

**Basic:**
```java
import javax.persistence.*;
import java.io.Serializable;
```

**With Dates:**
```java
import java.time.LocalDateTime;
import java.time.LocalDate;
```

**With Collections:**
```java
import java.util.ArrayList;
import java.util.List;
```

**With MyBatisGX Annotations:**
```java
import com.mybatisgx.core.annotation.LogicDelete;
import com.mybatisgx.core.annotation.LogicDeleteId;
import com.mybatisgx.core.annotation.GeneratedValue;
import com.mybatisgx.core.annotation.Fetch;
import com.mybatisgx.core.enums.FetchMode;
```

## Example: Complete Entity Generation

**User Request:** "Create a User entity with name, age, email fields. Include logic delete, optimistic lock, and audit fields."

**Generated Entity:**
```java
package com.example.entity;

import javax.persistence.*;
import com.mybatisgx.core.annotation.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User Entity
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long id;

    private String name;

    private Integer age;

    private String email;

    @LogicDelete
    private Integer status;

    @Version
    private Integer version;

    @Column(name = "create_by")
    @GeneratedValue(CreateByValueProcessor.class)
    private Long createBy;

    @Column(name = "create_time")
    @GeneratedValue(CreateTimeValueProcessor.class)
    private LocalDateTime createTime;

    @Column(name = "update_by")
    @GeneratedValue(UpdateByValueProcessor.class)
    private Long updateBy;

    @Column(name = "update_time")
    @GeneratedValue(UpdateTimeValueProcessor.class)
    private LocalDateTime updateTime;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
```

Use these templates as a foundation for generating MyBatisGX entities based on user requirements!
