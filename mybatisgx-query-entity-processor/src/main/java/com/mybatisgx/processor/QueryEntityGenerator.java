package com.mybatisgx.processor;

import com.squareup.javapoet.*;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryEntityGenerator {

    private static final List<String> QUERY_SUFFIXES = Arrays.asList(
            "Eq", "Ne", "Gt", "Lt", "Ge", "Le",
            "Like", "NotLike", "In", "NotIn", "IsNull", "IsNotNull"
    );

    public static void generateQueryEntity(
            TypeElement entityElement,
            ProcessingEnvironment processingEnv) throws IOException {

        // 获取实体类信息
        String entityName = entityElement.getSimpleName().toString();
        String packageName = processingEnv.getElementUtils()
                .getPackageOf(entityElement).getQualifiedName().toString();
        String queryClassName = entityName + "Query";
        String queryPackageName = packageName + ".query";

        // 创建类构建器
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(queryClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(ClassName.get(
                        "com.mybatisgx.core.annotation",
                        "ConditionEntity"
                )).build());

        // 添加Lombok注解
        classBuilder.addAnnotation(ClassName.get("lombok", "Data"));
        classBuilder.addAnnotation(ClassName.get("lombok", "NoArgsConstructor"));
        classBuilder.addAnnotation(ClassName.get("lombok", "AllArgsConstructor"));

        // 添加实体字段的查询条件变体
        List<? extends Element> fields = entityElement.getEnclosedElements()
                .stream()
                .filter(e -> e.getKind().isField())
                .collect(Collectors.toList());

        for (Element field : fields) {
            addQueryFields(classBuilder, field);
        }

        // 构建类
        TypeSpec queryEntity = classBuilder.build();

        // 写入文件
        JavaFile javaFile = JavaFile.builder(queryPackageName, queryEntity)
                .indent("    ")
                .build();

        // 写入生成的源码
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(
                queryPackageName + "." + queryClassName
        );

        try (Writer writer = sourceFile.openWriter()) {
            javaFile.writeTo(writer);
        }
    }

    private static void addQueryFields(TypeSpec.Builder builder, Element field) {
        String fieldName = field.getSimpleName().toString();
        TypeMirror fieldTypeMirror = field.asType();
        TypeName fieldType = TypeName.get(fieldTypeMirror);

        for (String suffix : QUERY_SUFFIXES) {
            String queryFieldName = fieldName + suffix;
            String operator = suffixToOperator(suffix);

            AnnotationSpec.Builder annotationBuilder = AnnotationSpec.builder(
                    ClassName.get("com.mybatisgx.core.annotation", "QueryField")
            ).addMember("operator", "$S", operator);

            // 特殊处理空值检查
            if (suffix.equals("IsNull") || suffix.equals("IsNotNull")) {
                annotationBuilder.addMember("ignoreNull", "false");
            }

            FieldSpec fieldSpec = FieldSpec.builder(fieldType, queryFieldName)
                    .addModifiers(Modifier.PRIVATE)
                    .addAnnotation(annotationBuilder.build())
                    .build();

            builder.addField(fieldSpec);
        }
    }

    private static String suffixToOperator(String suffix) {
        switch (suffix) {
            case "Eq":
                return "=";
            case "Ne":
                return "!=";
            case "Gt":
                return ">";
            case "Lt":
                return "<";
            case "Ge":
                return ">=";
            case "Le":
                return "<=";
            case "Like":
                return "LIKE";
            case "NotLike":
                return "NOT LIKE";
            case "In":
                return "IN";
            case "NotIn":
                return "NOT IN";
            case "IsNull":
                return "IS NULL";
            case "IsNotNull":
                return "IS NOT NULL";
            default:
                return "=";
        }
    }
}
