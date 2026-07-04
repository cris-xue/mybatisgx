package com.mybatisgx.dsl.test;

import com.mybatisgx.dsl.test.entity.User;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.handler.EntityInfoHandler;
import com.mybatisgx.dsl.method.MethodNamePreprocessor;
import com.mybatisgx.model.handler.test.entity.PreprocessorAmbiguousEntity;
import com.mybatisgx.model.handler.test.entity.PreprocessorLockAndEntity;
import com.mybatisgx.model.handler.test.entity.PreprocessorNameLikeEntity;
import org.junit.Assert;
import org.junit.Test;

/**
 * MethodNamePreprocessor 单元测试
 * 测试预处理层在 ANTLR 解析前自动转义有歧义的字段名
 */
public class MethodNamePreprocessorTest {

    private final EntityInfoHandler entityInfoHandler = new EntityInfoHandler();

    // ===== Case 1: 实体有 nameLike，无 name —— 无歧义自动转义 =====

    @Test
    public void testCase1_nameLikeField_noNameField_autoEscape() {
        // 输入: "findByNameLikeAndAge"
        // 实体: [id, nameLike, age]
        // 期望: "findBy$NameLike$AndAge"
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorNameLikeEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findByNameLikeAndAge");
        Assert.assertEquals("findBy$NameLike$AndAge", result);
    }

    // ===== Case 2: nameLike 字段做 LIKE 查询 =====

    @Test
    public void testCase2_nameLikeField_withLikeOperator() {
        // 输入: "findByNameLikeLike"
        // 实体: [id, nameLike, age]
        // 期望: "findBy$NameLike$Like"
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorNameLikeEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findByNameLikeLike");
        Assert.assertEquals("findBy$NameLike$Like", result);
    }

    // ===== Case 3: 同时存在 name 和 nameLike —— 有歧义保持原样 =====

    @Test
    public void testCase3_bothNameAndNameLike_ambiguous_noChange() {
        // 输入: "findByNameLike"
        // 实体: [id, name, nameLike, age]
        // 期望: "findByNameLike"（保持原样，因为截断后 name 也存在）
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorAmbiguousEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findByNameLike");
        Assert.assertEquals("findByNameLike", result);
    }

    // ===== Case 4: 字段名以 And 结尾 =====

    @Test
    public void testCase4_lockAndField_noLockField_autoEscape() {
        // 输入: "findByLockAndAndAge"
        // 实体: [id, lockAnd, age]
        // 期望: "findBy$LockAnd$AndAge"
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorLockAndEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findByLockAndAndAge");
        Assert.assertEquals("findBy$LockAnd$AndAge", result);
    }

    // ===== 边界场景：null 输入 =====

    @Test
    public void testNullEntityInfo_returnsOriginal() {
        String result = MethodNamePreprocessor.escape(null, "findByNameLike");
        Assert.assertEquals("findByNameLike", result);
    }

    @Test
    public void testNullMethodName_returnsNull() {
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorNameLikeEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, null);
        Assert.assertNull(result);
    }

    // ===== 已手动写 $...$ 的方法名不再处理 =====

    @Test
    public void testAlreadyEscaped_skipProcessing() {
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorNameLikeEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findBy$NameLike$AndAge");
        Assert.assertEquals("findBy$NameLike$AndAge", result);
    }

    // ===== 没有 By 关键字时，不处理 =====

    @Test
    public void testNoByKeyword_noChange() {
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorNameLikeEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findAll");
        Assert.assertEquals("findAll", result);
    }

    // ===== 无冲突字段的实体，不处理 =====

    @Test
    public void testNoAmbiguousFields_noChange() {
        // User 实体有 name 和 nameEq，nameEq 截断后是 name（存在），所以不加入歧义列表
        EntityInfo entityInfo = entityInfoHandler.execute(User.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findByNameEqAndAge");
        Assert.assertEquals("findByNameEqAndAge", result);
    }

    // ===== 字段名在条件末尾（无后续字符）=====

    @Test
    public void testFieldAtEnd_autoEscape() {
        // 输入: "findByNameLike"
        // 实体: [id, nameLike, age] (无 name)
        // 期望: "findBy$NameLike$" (字段在末尾，有效边界)
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorNameLikeEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findByNameLike");
        Assert.assertEquals("findBy$NameLike$", result);
    }

    // ===== nameLike 字段做 Between 查询 =====

    @Test
    public void testNameLikeField_withBetweenOperator() {
        // 输入: "findByNameLikeBetween"
        // 实体: [id, nameLike, age]
        // 期望: "findBy$NameLike$Between"
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorNameLikeEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findByNameLikeBetween");
        Assert.assertEquals("findBy$NameLike$Between", result);
    }

    // ===== lockAnd 字段单独查询（末尾） =====

    @Test
    public void testLockAndField_atEnd() {
        // 输入: "findByLockAnd"
        // 实体: [id, lockAnd, age]
        // 期望: "findBy$LockAnd$"
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorLockAndEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findByLockAnd");
        Assert.assertEquals("findBy$LockAnd$", result);
    }

    // ===== lockAnd 字段做 Eq 查询 =====

    @Test
    public void testLockAndField_withEqOperator() {
        // 输入: "findByLockAndEq"
        // 实体: [id, lockAnd, age]
        // 期望: "findBy$LockAnd$Eq"
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorLockAndEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findByLockAndEq");
        Assert.assertEquals("findBy$LockAnd$Eq", result);
    }

    // ===== 多个歧义字段同时出现 =====

    @Test
    public void testMultipleAmbiguousFields() {
        // 输入: "findByNameLikeAndAgeGt"
        // 实体: [id, nameLike, age] (nameLike 是歧义字段)
        // 期望: "findBy$NameLike$AndAgeGt"
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorNameLikeEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findByNameLikeAndAgeGt");
        Assert.assertEquals("findBy$NameLike$AndAgeGt", result);
    }

    // ===== 带 Custom 前缀的方法名 =====

    @Test
    public void testCustomPrefix_withAmbiguousField() {
        // 输入: "findCustomByNameLikeAndAge"
        // 实体: [id, nameLike, age]
        // 期望: "findCustomBy$NameLike$AndAge"
        EntityInfo entityInfo = entityInfoHandler.execute(PreprocessorNameLikeEntity.class);
        String result = MethodNamePreprocessor.escape(entityInfo, "findCustomByNameLikeAndAge");
        Assert.assertEquals("findCustomBy$NameLike$AndAge", result);
    }
}
