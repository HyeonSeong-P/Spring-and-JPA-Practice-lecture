package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
// 상속 관계라 전략을 짜줘야함. JOINED: 가장 정규환된 스타일, SINGLE_TABLE: 한 테이블에 다 넣는 전략, TABLE_PER_CLASS: 자식 클래스마다 테이블을 만든다.
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype") // SingleTable이라 자식 클래스들 구분될 때 어떤식으로 할지 나오는 그런 것.
@Getter
@Setter
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();
}
