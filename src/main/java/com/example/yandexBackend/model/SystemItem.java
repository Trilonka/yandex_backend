package com.example.yandexBackend.model;

import com.example.yandexBackend.model.constant.SystemItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "system_item")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class SystemItem {

    @Id
    private String id; // not null // id // "elem_1_1"

    @Column
    private String url; // link for files, for pap is null // can null

    @Column
    private String date; // date-time format // not null // last update time // "2022-05-28T21:12:01.000Z"

    private String parentId; // can null

    @Column
    private SystemItemType type;

    @Column
    private Integer size; // can null // int64

    @OneToMany(mappedBy = "parentId")
    private List<SystemItem> children;

    public List<SystemItem> getChildren() {
        if (type==SystemItemType.FILE)
            return null;
        return children;
    }
}
