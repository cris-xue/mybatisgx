package com.mybatisgx.boundary.jointable;

import com.mybatisgx.annotation.*;

import java.util.List;

@Entity
@Table(name = "test_entity2")
public class TestEntity2 {

    @Id
    private Long id;

    @Fetch
    @ManyToMany
    @JoinTable(
            name = "test_entity1_test_entity2",
            joinColumns = @JoinColumn(name = "test_entity2_id"),
            inverseJoinColumns = @JoinColumn(name = "test_entity1_id")
    )
    @JoinColumns({
            @JoinColumn(name = "test_entity2_id"),
            @JoinColumn(name = "test_entity1_id")
    })
    private List<TestEntity1> testEntity1List;
}
