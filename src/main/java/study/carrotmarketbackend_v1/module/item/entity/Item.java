package study.carrotmarketbackend_v1.module.item.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import study.carrotmarketbackend_v1.module.categortItem.entity.CategoryItem;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String name;

    private long price;

    private long stockQuantity;

    @OneToMany(mappedBy = "item")
    private List<CategoryItem> categoryItems = new ArrayList<>();
}