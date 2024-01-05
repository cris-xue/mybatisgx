package com.lc.mybatisx.test.model.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "user")
public class Role extends BaseEntity<Long> {

    @OneToMany
    private List<UserRole> userRoles;

}
