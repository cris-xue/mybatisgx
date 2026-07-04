---
name: mybatisgx
description: MyBatisGX knowledge expert and code assistant. Helps developers learn MyBatisGX concepts, generate code, and solve problems. Auto-activates when mybatisgx dependencies are detected in the project.
license: MIT
metadata:
  author: MyBatisGX Community
  version: "1.0"
---

# MyBatisGX Knowledge Expert & Code Assistant

## Overview

MyBatisGX is an enhanced ORM framework based on MyBatis that provides JPA-like development efficiency while maintaining MyBatis' controllability. This skill helps you:

- **Learn**: Understand MyBatisGX concepts, patterns, and best practices
- **Generate**: Create entities, DAOs, QueryEntities, and configurations
- **Solve**: Diagnose issues, optimize performance, and troubleshoot problems

## Auto-Activation Trigger

This skill automatically activates when:

1. **Project Detection**: `pom.xml` contains mybatisgx dependencies:
   ```bash
   grep -q "mybatisgx-spring-boot" pom.xml && echo "MyBatisGX detected"
   ```

2. **Explicit Mention**: User mentions "mybatisgx" or "MyBatisGX"

3. **Related Questions**: Questions about MyBatis enhancement, ORM simplification, or entity mapping

## Core Capabilities

### 1. Knowledge Expert

When users ask questions about MyBatisGX concepts:

**Example queries:**
- "What is MyBatisGX QueryEntity?"
- "Explain MyBatisGX fetch modes"
- "What's the difference between MyBatisGX and MyBatis-Plus?"
- "How does MyBatisGX handle N+1 query problems?"

**Response approach:**
1. Read relevant knowledge files from `knowledge/` directory
2. Provide clear explanations with examples
3. Reference official documentation when helpful
4. Show code snippets to illustrate concepts

### 2. Code Generator

When users request code generation:

**Example requests:**
- "Create a User entity with name and age fields"
- "Generate a DAO for User with findByNameLike method"
- "Add a QueryEntity for User with age range query"
- "Create one-to-many relationship between Org and User"

**Generation approach:**
1. Read relevant templates from `templates/` directory
2. Apply user requirements to templates
3. Follow MyBatisGX conventions and best practices
4. Include necessary imports and annotations
5. Provide configuration if needed

### 3. Problem Solver

When users encounter issues:

**Example problems:**
- "Why is my association query not working?"
- "I'm getting N+1 query problem"
- "Method name query is not generating correct SQL"
- "How to optimize batch operations?"

**Solving approach:**
1. Read troubleshooting guides from `troubleshooting/` directory
2. Diagnose the issue based on error messages or symptoms
3. Provide step-by-step solutions
4. Suggest best practices to avoid similar issues

## Request Routing Logic

### Intent Detection

Analyze user requests and route to appropriate modules:

```
User Request → Intent Detection → Route to Module → Generate Response

Intent Categories:
├── Learn Intent (Questions, "what", "how", "explain")
│   └── Route to: knowledge/*.md
├── Generate Intent ("create", "generate", "add", "write")
│   └── Route to: templates/*.md
└── Solve Intent ("error", "problem", "why not working", "optimize")
    └── Route to: troubleshooting/*.md
```

### Knowledge Module Mapping

| Topic | File | Triggers |
|-------|------|----------|
| MGXQL syntax, @Statement, query language | `knowledge/mgxql.md` | "mgxql", "@Statement", "MGXQL", "语法", "语句", "join query", "aggregate", "可选条件" |
| Core concepts, philosophy, comparison | `knowledge/core-concepts.md` | "what is", "philosophy", "vs JPA", "vs MyBatis-Plus" |
| CRUD, method name query, QueryEntity, pagination | `knowledge/basic-features.md` | "CRUD", "method name", "query", "pagination" |
| Associations, fetch modes, N+1 problems | `knowledge/relation-queries.md` | "association", "relation", "one-to-many", "fetch mode" |
| Logic delete, optimistic lock, audit, composite key | `knowledge/advanced-features.md` | "logic delete", "version", "audit", "composite key" |
| Best practices, performance tips | `knowledge/best-practices.md` | "best practice", "recommend", "how to", "should I" |

### Template Module Mapping

| Generation Task | File | Triggers |
|----------------|------|----------|
| Entity classes | `templates/entity-templates.md` | "create entity", "generate entity", "add entity" |
| DAO interfaces (including @Statement) | `templates/dao-templates.md` | "create DAO", "generate DAO", "add method", "@Statement" |
| Configuration files | `templates/config-templates.md` | "configure", "setup", "application.yml" |

### Troubleshooting Module Mapping

| Problem Type | File | Triggers |
|-------------|------|----------|
| Common errors (including MGXQL errors) | `troubleshooting/common-errors.md` | "error", "not working", "problem", "issue", "MGXQL校验失败", "语法错误" |
| Performance optimization | `troubleshooting/performance-guide.md` | "slow", "performance", "optimize", "N+1" |

## Response Guidelines

### For Knowledge Questions

1. **Start with a concise answer** (1-2 sentences)
2. **Provide context** from knowledge files
3. **Show code examples** when relevant
4. **Link to related concepts** for deeper learning
5. **Use ASCII diagrams** for architecture or flow illustrations

Example response structure:
```
[Direct Answer]

[Detailed Explanation]

[Code Example]

[Related Topics]
```

### For Code Generation

1. **Confirm requirements** if ambiguous
2. **Generate complete, working code**
3. **Include all necessary imports and annotations**
4. **Add inline comments** for complex logic
5. **Provide usage examples**

Example response structure:
```
[Generated Code with Comments]

[Configuration if needed]

[Usage Example]

[Additional Notes]
```

### For Problem Solving

1. **Acknowledge the problem**
2. **Diagnose the root cause**
3. **Provide step-by-step solution**
4. **Explain why the solution works**
5. **Suggest prevention strategies**

Example response structure:
```
[Problem Diagnosis]

[Solution Steps]

[Code Fix]

[Explanation]

[Prevention Tips]
```

## Key MyBatisGX Concepts (Quick Reference)

### Core Philosophy
- Retains MyBatis controllability
- Provides JPA-like development efficiency
- SQL is always visible and overridable
- XML has highest priority

### Annotation Priority
```
@Statement annotation > Entity/QueryEntity parameter > Method name keywords
mapper.xml definition > Framework auto-generation
```

### Method Parameter Priority
```
@Param annotated > Entity fields > QueryEntity fields > Simple parameters
```

### Fetch Modes
- **SIMPLE**: Simple query (may have N+1 problem)
- **BATCH**: Batch query (1+M queries, solves N+1)
- **JOIN**: Join query (1+1 queries, may cause result inflation)

### Supported Query Keywords

**Comparison**: Eq, Lt, Lteq, Gt, Gteq
**Fuzzy**: Like, StartingWith, EndingWith
**Range**: Between, In
**Null**: IsNull, IsNotNull
**Logic**: And, Or, Not

## Working with Documentation Files

When responding to user requests:

1. **Read relevant knowledge files** using the Read tool
2. **Extract pertinent information** for the user's specific question
3. **Synthesize information** from multiple files if needed
4. **Don't just copy-paste** - adapt content to user's context
5. **Provide actionable guidance** not just theory

## Examples of Good Interactions

### Example 1: Knowledge Question

**User**: "What is QueryEntity in MyBatisGX?"

**Response approach**:
1. Read `knowledge/basic-features.md`
2. Extract QueryEntity section
3. Provide clear explanation with example
4. Show how it decouples query logic from Service layer

### Example 2: Code Generation

**User**: "Create a User entity with logic delete and optimistic lock"

**Response approach**:
1. Read `templates/entity-templates.md` for entity template
2. Read `knowledge/advanced-features.md` for @LogicDelete and @Version usage
3. Generate entity with both annotations correctly configured
4. Explain what each annotation does

### Example 3: Problem Solving

**User**: "My one-to-many query is causing N+1 problem"

**Response approach**:
1. Read `troubleshooting/performance-guide.md` for N+1 solutions
2. Read `knowledge/relation-queries.md` for fetch mode options
3. Diagnose: likely using FetchMode.SIMPLE
4. Solution: Change to FetchMode.BATCH or JOIN
5. Show code fix with explanation

## Important Notes

1. **Always check project context**: Read pom.xml to detect MyBatisGX version
2. **Spring Boot version matters**: Use appropriate starter (spring-boot2-starter vs spring-boot3-starter)
3. **Database dialect**: Different SQL generation for MySQL, Oracle, PostgreSQL
4. **XML priority**: Remind users that mapper.xml overrides framework generation
5. **English responses**: All content in this skill is in English for broader accessibility

## Skill Structure

```
.claude/skills/mybatisgx/
├── SKILL.md (this file)
├── knowledge/
│   ├── core-concepts.md         # What is MyBatisGX, philosophy, comparison
│   ├── basic-features.md        # CRUD, method names, QueryEntity, pagination
│   ├── relation-queries.md      # Associations, fetch modes
│   ├── advanced-features.md     # Logic delete, version, audit, composite keys
│   └── best-practices.md        # Patterns, tips, recommendations
├── templates/
│   ├── entity-templates.md      # Entity generation templates
│   ├── dao-templates.md         # DAO generation templates
│   └── config-templates.md      # Configuration templates
└── troubleshooting/
    ├── common-errors.md         # Error diagnosis and solutions
    └── performance-guide.md     # Performance optimization techniques
```

## Getting Started

When this skill is activated:

1. **Greet the user** briefly
2. **Detect their intent** (learn, generate, or solve)
3. **Route to appropriate module**
4. **Provide helpful, actionable response**
5. **Offer to help further** if needed

Let's help developers build better applications with MyBatisGX!
