package study.carrotmarketbackend_v1.module.delivery.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import study.carrotmarketbackend_v1.module.address.entity.Address;
import study.carrotmarketbackend_v1.module.delivery.status.DeliveryStatus;
import study.carrotmarketbackend_v1.module.order.entity.Order;

@Entity
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; //ENUM [READY(준비), COMP(배송)]
}
