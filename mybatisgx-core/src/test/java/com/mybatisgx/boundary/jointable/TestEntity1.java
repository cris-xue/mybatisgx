package com.mybatisgx.boundary.jointable;

import com.mybatisgx.annotation.*;

import java.util.List;

@Entity
@Table(name = "test_entity1")
public class TestEntity1 {

    @Id
    private Long id;

    @Fetch
    @ManyToMany(mappedBy = "testEntity1List")
    private List<TestEntity2> testEntity2List;
}
