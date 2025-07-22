package com.mybatisgx.processor;

import com.google.auto.service.AutoService;
import com.lc.mybatisx.annotation.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.lc.mybatisx.annotation.Entity")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class QueryEntityProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        QueryEntityGenerator queryEntityGenerator = new QueryEntityGenerator();
        try {
            // 1. 扫描所有@Entity注解的类
            for (Element element : roundEnvironment.getElementsAnnotatedWith(Entity.class)) {
                // 检查元素是否是类/接口
                if (element.getKind() == ElementKind.CLASS || element.getKind() == ElementKind.INTERFACE) {
                    if (element instanceof TypeElement) {
                        TypeElement typeElement = (TypeElement) element;
                        Data lombokDataAnnotation = typeElement.getAnnotation(Data.class);
                        NoArgsConstructor lombokNoArgsConstructorAnnotation = typeElement.getAnnotation(NoArgsConstructor.class);
                        AllArgsConstructor lombokAllArgsConstructorAnnotation = typeElement.getAnnotation(AllArgsConstructor.class);
                        queryEntityGenerator.generateQueryEntity(typeElement, processingEnv, lombokDataAnnotation, lombokNoArgsConstructorAnnotation, lombokAllArgsConstructorAnnotation);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Query entity generation failed: " + e.getMessage());
            return false;
        }
    }
}