package com.lc.mybatisx.parse;

import com.lc.mybatisx.annotation.Column;
import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.annotation.Version;
import com.lc.mybatisx.model.FieldNode;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FieldParse {

    private List<FieldNode> parseField(Type type) {
        Field[] fields = FieldUtils.getAllFields((Class<?>) type);

        List<FieldNode> fieldNodeList = new ArrayList<>();
        for (Field field : fields) {
            FieldNode fieldNode = new FieldNode();

            fieldNode.setType(field.getType());
            fieldNode.setName(field.getName());
            fieldNode.setId(field.getAnnotation(Id.class));
            fieldNode.setColumn(field.getAnnotation(Column.class));
            fieldNode.setLogicDelete(field.getAnnotation(LogicDelete.class));
            fieldNode.setVersion(field.getAnnotation(Version.class));

            fieldNodeList.add(fieldNode);
        }

        return fieldNodeList;
    }

}
