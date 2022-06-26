package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B") // 전략이 SingleTable이라 구분할 수 있는 값을 줘야함.
@Getter
@Setter
public class Book extends Item{

    private String author;
    private String isbn;

}
