package com.mybatisgx.method.validator.param.entity.select;

import com.mybatisgx.annotation.Property;
import com.mybatisgx.annotation.QueryColumn;
import com.mybatisgx.annotation.QueryEntity;

@QueryEntity(ValidatorUser.class)
public class IgnoreConditionUserQuery extends ValidatorUser {

    private String nameLike;

    @QueryColumn(ignore = true)
    private String startDate;

    @QueryColumn(ignore = true)
    private String endDate;

    @QueryColumn(ignore = true)
    @Property(name = "name")
    private String fooBar;

    public String getNameLike() {
        return nameLike;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFooBar() {
        return fooBar;
    }

    public void setFooBar(String fooBar) {
        this.fooBar = fooBar;
    }
}
