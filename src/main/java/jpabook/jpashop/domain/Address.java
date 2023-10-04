package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
// @Setter 값 타입은 불변객체
@Embeddable
public class Address {

    private String city;
    private String street;
    private String zipcode;

    // jpa 스펙 맞춤용이라 protected로 제한
    protected Address() {}

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

}