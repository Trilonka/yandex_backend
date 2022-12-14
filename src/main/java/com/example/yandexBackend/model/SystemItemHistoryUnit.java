package com.example.yandexBackend.model;

import com.example.yandexBackend.model.constant.SystemItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "system_item_history")
public class SystemItemHistoryUnit {

    @Id
    @Column(name = "history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int historyId;

    private String id;
    private String url;
    private String parentId;
    private SystemItemType type;
    private Integer size;
    private String date;
}
