package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){ // 파라미터들이 많다면 DTO를 만들어 처리하면 좋다!
        /** 이런식으로 Transtional을 통해 DB에서 찾아온 건 영속성 엔티티다.
         * 즉 save나 merge 그런거 필요없이 데이터를 변경하는 코드만 작성해도
         * Transactional이 Commit되는 순간 JPA는 Flush를 날려 영속성 컨텍스트에서 변경된 엔티티를 다 감지해서 쿼리를 날려 반영한다.
         * !!!!!!!이런 식으로 업데이트를 진행하는게 바람직한 방법이다(변경 감지를 사용하는 방법)!!!!!!!
         * merge 방법의 경우 준영속 엔티티에 맞춰 모든 속성이 변경되어 null 업데이트가 발생할 수도 있으니 되도록이면 쓰지 말자.
         */
        Item findItem = itemRepository.findOne(itemId);
        /**
         * 아래와 같이 세터를 통해 값을 변경하는 방법도 바람직하지 않다.
         * 따로 해당 클래스에 의미있는 change 메서드를 만들어 쓰자(가격이나 이름, 양을 인자로 넘기는 등)
         */
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
    }
    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
