package study.carrotmarketbackend_v1.dto;

import lombok.*;
import study.carrotmarketbackend_v1.entity.Address;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private String city;
    private String street;
    private String zipcode;

    public static AddressDTO fromEntity(Address address) {
        return AddressDTO.builder()
                .city(address.getCity())
                .street(address.getStreet())
                .zipcode(address.getZipcode())
                .build();
    }
}