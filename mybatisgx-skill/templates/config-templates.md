# MyBatisGX Configuration Templates

This document provides templates for MyBatisGX Spring Boot configuration.

## Application Configuration (application.yml)

### Basic Configuration

```yaml
mybatisgx:
  # Mapper XML locations
  mapper-locations: classpath:mapper/*Mapper.xml

  # Type aliases package
  type-aliases-package: com.example.entity

  # MyBatis configuration
  configuration:
    # Enable camelCase to snake_case mapping
    map-underscore-to-camel-case: true

    # Default fetch size
    default-fetch-size: 100

    # Statement timeout (seconds)
    default-statement-timeout: 30

    # Enable caching
    cache-enabled: true

    # Lazy loading enabled
    lazy-loading-enabled: true

    # Aggressive lazy loading
    aggressive-lazy-loading: false

    # Multiple result sets enabled
    multiple-result-sets-enabled: true

    # Use column label
    use-column-label: true

    # Use generated keys
    use-generated-keys: false

    # Auto mapping behavior
    auto-mapping-behavior: partial

    # Default executor type
    default-executor-type: simple

    # Log implementation
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
```

### Spring Boot 2.x Configuration

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: password

    # HikariCP configuration
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      connection-test-query: SELECT 1

mybatisgx:
  mapper-locations: classpath:mapper/*Mapper.xml
  type-aliases-package: com.example.entity
  configuration:
    map-underscore-to-camel-case: true
```

### Spring Boot 3.x Configuration

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: password

    # HikariCP configuration
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      connection-test-query: SELECT 1

mybatisgx:
  mapper-locations: classpath:mapper/*Mapper.xml
  type-aliases-package: com.example.entity
  configuration:
    map-underscore-to-camel-case: true
```

## Java Configuration

### Application Main Class

```java
package com.example;

import com.mybatisgx.spring.boot.annotation.MybatisgxScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application main class
 */
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

### ValueProcessor Configuration

```java
package com.example.config;

import com.example.processor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisGX ValueProcessor configuration
 */
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

    @Bean
    public CreateByValueProcessor createByValueProcessor() {
        return new CreateByValueProcessor();
    }

    @Bean
    public UpdateByValueProcessor updateByValueProcessor() {
        return new UpdateByValueProcessor();
    }

    @Bean
    public LogicDeleteIdValueProcessor logicDeleteIdValueProcessor() {
        return new LogicDeleteIdValueProcessor();
    }
}
```

## Maven Dependencies

### Spring Boot 2.x

```xml
<dependencies>
    <!-- MyBatisGX Spring Boot 2 Starter -->
    <dependency>
        <groupId>com.mybatisgx</groupId>
        <artifactId>mybatisgx-spring-boot2-starter</artifactId>
        <version>0.0.1</version>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Or PostgreSQL Driver -->
    <!--
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    -->

    <!-- Or Oracle Driver -->
    <!--
    <dependency>
        <groupId>com.oracle.database.jdbc</groupId>
        <artifactId>ojdbc8</artifactId>
        <scope>runtime</scope>
    </dependency>
    -->

    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot Starter Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Spring Boot 3.x

```xml
<dependencies>
    <!-- MyBatisGX Spring Boot 3 Starter -->
    <dependency>
        <groupId>com.mybatisgx</groupId>
        <artifactId>mybatisgx-spring-boot3-starter</artifactId>
        <version>0.0.2</version>
    </dependency>

    <!-- MySQL Driver -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot Starter Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Database-Specific Configuration

### MySQL

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: password
```

### PostgreSQL

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/mydb
    username: postgres
    password: password
```

### Oracle

```yaml
spring:
  datasource:
    driver-class-name: oracle.jdbc.OracleDriver
    url: jdbc:oracle:thin:@localhost:1521:ORCL
    username: system
    password: password
```

## Complete Project Structure

```
project-root/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── Application.java                  # Main class with @MybatisgxScan
│   │   │       ├── config/
│   │   │       │   └── MybatisgxConfig.java         # ValueProcessor configuration
│   │   │       ├── entity/
│   │   │       │   ├── User.java
│   │   │       │   └── Org.java
│   │   │       ├── query/
│   │   │       │   ├── UserQuery.java
│   │   │       │   └── OrgQuery.java
│   │   │       ├── dao/
│   │   │       │   ├── UserDao.java
│   │   │       │   └── OrgDao.java
│   │   │       ├── service/
│   │   │       │   ├── UserService.java
│   │   │       │   └── OrgService.java
│   │   │       ├── controller/
│   │   │       │   ├── UserController.java
│   │   │       │   └── OrgController.java
│   │   │       └── processor/
│   │   │           ├── IdValueProcessor.java
│   │   │           ├── CreateTimeValueProcessor.java
│   │   │           └── UpdateTimeValueProcessor.java
│   │   └── resources/
│   │       ├── application.yml                       # Application configuration
│   │       └── mapper/                               # Optional mapper XML files
│   │           ├── UserMapper.xml
│   │           └── OrgMapper.xml
│   └── test/
│       └── java/
│           └── com/example/
│               ├── dao/
│               │   └── UserDaoTest.java
│               └── service/
│                   └── UserServiceTest.java
└── pom.xml                                           # Maven dependencies
```

## Environment-Specific Configuration

### application-dev.yml (Development)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb_dev
    username: dev_user
    password: dev_password

mybatisgx:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # Console logging
```

### application-prod.yml (Production)

```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-server:3306/mydb
    username: prod_user
    password: ${DB_PASSWORD}  # From environment variable

    hikari:
      maximum-pool-size: 50
      minimum-idle: 10

mybatisgx:
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl  # SLF4J logging
```

## Complete Example: Quick Start Setup

**1. Add dependency (pom.xml):**
```xml
<dependency>
    <groupId>com.mybatisgx</groupId>
    <artifactId>mybatisgx-spring-boot2-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```

**2. Configure application (application.yml):**
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password

mybatisgx:
  mapper-locations: classpath:mapper/*Mapper.xml
  type-aliases-package: com.example.entity
  configuration:
    map-underscore-to-camel-case: true
```

**3. Configure main class (Application.java):**
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

**Done!** MyBatisGX is configured and ready to use.

## MGXSQL Configuration

MGXSQL dynamic SQL syntax can be enabled per-method or globally.

### Method 1: `@Lang` Annotation (Per-Method)

Add `@Lang(MgxsqlLanguageDriver.class)` to individual DAO methods. Other methods still use the default language driver.

```java
import com.mybatisgx.ext.scripting.xmltabs.MgxsqlLanguageDriver;
import org.apache.ibatis.lang.Lang;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

@Repository
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    @Lang(MgxsqlLanguageDriver.class)
    @Select("select * from t_user where\n  #id = :id\n  #and name like %:name%")
    List<User> findByIdAndName(@Param("id") Long id, @Param("name") String name);

    // This method uses default language driver (no MGXSQL)
    @Statement("select * from User where name = :name")
    List<User> findByName(@Param("name") String name);
}
```

**When to use**: Only some DAO methods need MGXSQL syntax; others use MGXQL or method name queries.

### Method 2: Global Configuration (All Methods)

Set `MgxsqlLanguageDriver` as the default scripting language. All `@Select`/`@Update`/`@Delete` annotations will use MGXSQL syntax by default.

```yaml
mybatis:
  configuration:
    default-scripting-language: com.mybatisgx.ext.scripting.xmltags.MgxsqlLanguageDriver
```

**Full configuration example:**

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: password

mybatisgx:
  mapper-locations: classpath:mapper/*Mapper.xml
  type-aliases-package: com.example.entity
  configuration:
    map-underscore-to-camel-case: true
    # Enable MGXSQL globally for all annotation-based SQL
    default-scripting-language: com.mybatisgx.ext.scripting.xmltags.MgxsqlLanguageDriver
```

**When to use**: Most DAO methods use MGXSQL syntax; the project heavily relies on dynamic SQL annotations.

> **Note**: When MGXSQL is set globally, `@Statement` (MGXQL) methods are NOT affected — they use their own parser. Only `@Select`/`@Update`/`@Delete` annotations are affected by the default scripting language setting.

Use these templates to quickly set up MyBatisGX in Spring Boot projects!
