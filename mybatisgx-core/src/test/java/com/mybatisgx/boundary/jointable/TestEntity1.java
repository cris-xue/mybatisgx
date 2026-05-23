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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<TestEntity2> getTestEntity2List() {
        return testEntity2List;
    }

    public void setTestEntity2List(List<TestEntity2> testEntity2List) {
        this.testEntity2List = testEntity2List;
    }
}
