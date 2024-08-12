package study.carrotmarketbackend_v1.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private AddressDTO address;
    private String role;
    private Date createDate;
    private Date updatedDate;
}