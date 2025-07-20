package com.mybatisgx.processor;

import com.google.auto.service.AutoService;
import com.lc.mybatisx.annotation.Entity;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
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
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            // 1. 扫描所有@Entity注解的类
            for (Element element : roundEnv.getElementsAnnotatedWith(Entity.class)) {
                if (element instanceof TypeElement) {
                    TypeElement typeElement = (TypeElement) element;

                    // 2. 生成查询实体
                    QueryEntityGenerator.generateQueryEntity(
                            typeElement,
                            processingEnv
                    );
                }
            }
            return true;
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Query entity generation failed: " + e.getMessage());
            return false;
        }
    }
}