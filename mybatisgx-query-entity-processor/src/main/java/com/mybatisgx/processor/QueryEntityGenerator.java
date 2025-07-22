package com.mybatisgx.processor;

import com.squareup.javapoet.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class QueryEntityGenerator {

    private static final String JAVA_CODE_INDENT = "    ";

    private static final List<String> QUERY_SUFFIXES = Arrays.asList(
            "Eq", "Ne", "Gt", "Lt", "Ge", "Le",
            "Like", "NotLike", "In", "NotIn", "IsNull", "IsNotNull"
    );

    public void generateQueryEntity(
            TypeElement entityElement,
            ProcessingEnvironment processingEnvironment,
            Data lombokDataAnnotation,
            NoArgsConstructor lombokNoArgsConstructorAnnotation,
            AllArgsConstructor lombokAllArgsConstructorAnnotation
    ) throws IOException {
        // 获取实体类信息
        String entityName = entityElement.getSimpleName().toString();
        String packageName = processingEnvironment.getElementUtils().getPackageOf(entityElement).getQualifiedName().toString();
        String queryClassName = entityName + "Query";
        String queryPackageName = packageName;

        // 如果类已存在，就不再创建
        String targetClassName = queryPackageName + "." + queryClassName;
        TypeElement existingClass = processingEnvironment.getElementUtils().getTypeElement(targetClassName);
        if (existingClass != null) {
            System.out.println("QueryEntity " + queryClassName + " already exists.");
            return;
        }

        TypeSpec.Builder classBuilder = build(entityElement, entityName);
        // classBuilder.addMethods(createDefaultConstructor(entityElement, lombokNoArgsConstructorAnnotation, lombokAllArgsConstructorAnnotation));
        useLombok(classBuilder);
        List<? extends Element> fieldElementList = getFieldElementList(entityElement);

        Map<String, List<String>> allFieldElementMap = new LinkedHashMap();
        for (Element fieldElement : fieldElementList) {
            Map<String, List<String>> fieldElementMap = addQueryField(fieldElement);
            allFieldElementMap.putAll(fieldElementMap);
        }
        for (Element fieldElement : fieldElementList) {
            TypeMirror fieldTypeMirror = fieldElement.asType();
            TypeName fieldType = TypeName.get(fieldTypeMirror);
            List<String> fieldList = allFieldElementMap.get(fieldElement.getSimpleName().toString());
            if (fieldList != null && !fieldList.isEmpty()) {
                for (String queryFieldName : fieldList) {
                    FieldSpec fieldSpec = FieldSpec.builder(fieldType, queryFieldName).addModifiers(Modifier.PRIVATE).build();
                    classBuilder.addField(fieldSpec);
                    classBuilder.addMethod(generateGetter(queryFieldName, fieldType));
                    classBuilder.addMethod(generateSetter(queryFieldName, fieldType));
                }
            }
        }

        // 构建类
        TypeSpec queryEntity = classBuilder.build();

        // 写入文件
        JavaFile javaFile = JavaFile.builder(queryPackageName, queryEntity).indent(JAVA_CODE_INDENT).build();

        // 写入生成的源码
        JavaFileObject sourceFile = processingEnvironment.getFiler().createSourceFile(targetClassName);

        try (Writer writer = sourceFile.openWriter()) {
            javaFile.writeTo(writer);
        }
    }

    /**
     * 创建类构建器
     *
     * @return
     */
    private TypeSpec.Builder build(TypeElement entityElement, String entityName) {
        String queryClassName = entityName + "Query";
        AnnotationSpec queryEntityAnnotation = AnnotationSpec.builder(ClassName.get("com.lc.mybatisx.annotation", "QueryEntity")).build();
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(queryClassName)
                // 继承实体类
                // .superclass(entityElement.asType())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(queryEntityAnnotation);
        return classBuilder;
    }

    private List<MethodSpec> createDefaultConstructor(
            TypeElement entityElement,
            NoArgsConstructor lombokNoArgsConstructorAnnotation,
            AllArgsConstructor lombokAllArgsConstructorAnnotation
    ) {
        List<ExecutableElement> constructors = new ArrayList<>();
        for (Element enclosedElement : entityElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                constructors.add((ExecutableElement) enclosedElement);
            }
        }

        // 处理每个构造函数
        List<MethodSpec> methodSpecList = new ArrayList();
        for (ExecutableElement constructor : constructors) {
            // 获取参数列表
            List<? extends VariableElement> parameters = constructor.getParameters();
            // 打印参数信息
            List<String> paramNameTemplateList = new ArrayList();
            List<String> paramValueList = new ArrayList();
            List<ParameterSpec> parameterSpecList = new ArrayList();
            for (VariableElement parameter : parameters) {
                String paramName = parameter.getSimpleName().toString();
                parameterSpecList.add(ParameterSpec.get(parameter));
                paramNameTemplateList.add("$L");
                paramValueList.add(paramName);
            }
            String paramNameTemplate = StringUtils.join(paramNameTemplateList, ",");
            String methodCallParamNameTemplate = String.format("super(%s)", paramNameTemplate);

            CodeBlock codeBlock = CodeBlock.builder().addStatement(methodCallParamNameTemplate, paramValueList.toArray()).build();
            MethodSpec methodSpec = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameters(parameterSpecList)
                    .addCode(codeBlock)
                    .build();
            methodSpecList.add(methodSpec);
        }
        return methodSpecList;
    }

    /**
     * 添加Lombok注解
     *
     * @param classBuilder
     */
    private void useLombok(TypeSpec.Builder classBuilder) {
        // classBuilder.addAnnotation(ClassName.get("lombok", "Data"));
        // classBuilder.addAnnotation(ClassName.get("lombok", "NoArgsConstructor"));
        // classBuilder.addAnnotation(ClassName.get("lombok", "AllArgsConstructor"));
    }

    private List<? extends Element> getFieldElementList(TypeElement entityElement) {
        List<? extends Element> fieldElementList = entityElement.getEnclosedElements()
                .stream()
                .filter(element -> element.getKind().isField())
                .collect(Collectors.toList());
        return fieldElementList;
    }

    private Map<String, List<String>> addQueryField(Element fieldElement) {
        String fieldName = fieldElement.getSimpleName().toString();
        Map<String, List<String>> fieldElementMap = new LinkedHashMap<>();
        List<String> fieldList = new ArrayList();
        fieldList.add(fieldName);
        for (String suffix : QUERY_SUFFIXES) {
            String queryFieldName = fieldName + suffix;
            fieldList.add(queryFieldName);
        }
        fieldElementMap.put(fieldName, fieldList);
        return fieldElementMap;
    }

    private MethodSpec generateGetter(String fieldName, TypeName fieldType) {
        String methodName = "get" + capitalize(fieldName);
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .returns(fieldType)
                .addStatement("return this.$L", fieldName)
                .build();
    }

    private MethodSpec generateSetter(String fieldName, TypeName fieldTypeName) {
        String methodName = "set" + capitalize(fieldName);
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldTypeName, fieldName)
                .addStatement("this.$L = $L", fieldName, fieldName)
                .build();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
