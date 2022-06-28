package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
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

    //==비즈니스 로직==// 객체지향적으로 생각해보면 데이터가 있는 곳에서 비즈니스 로직을 작성하는 게 효율적이다.
    // 세터로 값 변경 대신 비즈니스 로직으로 변경하는 게 바람직하다.
    /**
     * stock 증가
     */
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
