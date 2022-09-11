package com.example.yandexBackend.model;

import com.example.yandexBackend.model.constant.SystemItemType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "system_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemItem {

    @Id
    private String id;

    @Column
    private String url;

    @Column
    @DateTimeFormat(pattern = "yyyyMMdd'T'HH:mm:ssZ")
    private String date;

    private String parentId;

    @Column
    private SystemItemType type;

    @Column
    private Integer size;

    @OneToMany(mappedBy = "parentId")
    private List<SystemItem> children;

    public List<SystemItem> getChildren() {
        if (type==SystemItemType.FILE)
            return null;
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemItem item = (SystemItem) o;
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
