package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "category_item",
    joinColumns = @JoinColumn(name = "category_id"),
    inverseJoinColumns = @JoinColumn(name = "item_id")) // 다대다 관계를 위해 필요한 중간 테이블을 조인, 각 FK이외에 다른 필드를 추가할 수 없어 실무에선 잘 쓰이진 않는다.
    private List<Item> items = new ArrayList<>();

    /** 셀프 관계 설정 **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();
    /** 셀프 관계 설정 **/

    //==연관 관계 편의 메서드(양방향일 때 필수적으로 설정할 것)==//
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }
    //==연관 관계 편의 메서드(양방향일 때 필수적으로 설정할 것)==//
}
