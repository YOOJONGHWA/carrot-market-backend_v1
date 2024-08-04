package study.carrotmarketbackend_v1.module.categortItem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import study.carrotmarketbackend_v1.module.category.entity.Category;
import study.carrotmarketbackend_v1.module.item.entity.Item;

@Entity
@Table(name = "category_item")
@Getter
@Setter
public class CategoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

}
