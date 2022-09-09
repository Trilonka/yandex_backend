package com.example.yandexBackend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @ManyToMany
    @JoinTable(
            name = "parent_child",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private List<Folder> parent;

    @ManyToMany(mappedBy = "parent")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private List<Folder> children;
}
