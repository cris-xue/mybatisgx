package com.mybatisgx.context;

import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.handler.MethodInfoHandler;
import com.mybatisgx.template.DeleteTemplateHandler;
import com.mybatisgx.template.InsertTemplateHandler;
import com.mybatisgx.template.StatementTemplateHandler;
import com.mybatisgx.template.UpdateTemplateHandler;
import com.mybatisgx.template.select.LimitTemplateHandler;
import com.mybatisgx.template.select.SelectTemplateHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/11/25 18:08
 */
public class MybatisgxObjectFactory {

    private static final Map<Class, Object> OBJECTO_MAP = new HashMap<>();

    public static void register(MybatisgxConfiguration configuration) {
        OBJECTO_MAP.put(MethodInfoHandler.class, new MethodInfoHandler(configuration));

        OBJECTO_MAP.put(StatementTemplateHandler.class, new StatementTemplateHandler(configuration));
        OBJECTO_MAP.put(SelectTemplateHandler.class, new SelectTemplateHandler(configuration));
        OBJECTO_MAP.put(LimitTemplateHandler.class, new LimitTemplateHandler(configuration));

        OBJECTO_MAP.put(InsertTemplateHandler.class, new InsertTemplateHandler());
        OBJECTO_MAP.put(DeleteTemplateHandler.class, new DeleteTemplateHandler());
        OBJECTO_MAP.put(UpdateTemplateHandler.class, new UpdateTemplateHandler());
    }

    public static synchronized <T> T get(Class<T> clazz) {
        return (T) OBJECTO_MAP.get(clazz);
    }
}
