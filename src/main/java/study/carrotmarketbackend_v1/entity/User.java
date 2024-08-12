package study.carrotmarketbackend_v1.entity;

import jakarta.persistence.*;
import lombok.*;
import study.carrotmarketbackend_v1.status.AccountStatus;
import study.carrotmarketbackend_v1.status.UserType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String role;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(name = "user_name")
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    @Column(nullable = false, unique = true)
    private String phone;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @Column
    private String oauthProvider;

    @Column
    private String oauthId;

    @Column(nullable = false)
    private Date createDate;

    private Date updatedDate;

}
