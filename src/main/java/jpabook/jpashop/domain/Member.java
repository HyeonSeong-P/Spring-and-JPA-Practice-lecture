package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {
    @Id @GeneratedValue

    @Column(name = "member_id")
    private Long id;


    private String name;

    @Embedded // 여기 내장되어 있다는 것을 알림.
    private Address address;

    /** 일대다 관계를 표시하며 Order 테이블의 member에 의해 맵핑된다는 것을 표시함.
     * -> 연관 관계의 주인을 Order 테이블의 member로 설정한다는 의미이기도 하다.
     * 즉 Order의 member를 변경하면 여기도 변경된다.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
