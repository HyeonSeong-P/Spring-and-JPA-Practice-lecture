package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // jpa의 내장 타입이라는 것, 어딘가에 내장이 될 수 있다
@Getter
public class Address { // 값 타입은 변경 불가능하게 설계해야 한다.
    private String city;
    private String street;
    private String zipcode;

    /**
     * JPA 스펙상 엔티티나 임베디드 타입(@Embeddable)은 자바 기본 생성자를 public 또는 protected로 설정해야 한다.
     * protected로 설정하는 것이 그나마 더 안전하다.
     * JPA가 이런 제약을 두는 이유는 JPA 구현 라이브러리가 객체를 생성할 때 리플랙션, 프록시 같은 기술을 사용할 수 있도록 지원해야하기 때문.
     */
    protected Address(){
    }


    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
