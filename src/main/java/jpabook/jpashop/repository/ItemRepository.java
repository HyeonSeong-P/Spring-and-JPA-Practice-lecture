package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item){
        if(item.getId() == null){ // jpa에 저장하기 전까진 id는 생성되지 않아 신규 생성할 때(save)라고 보면 된다
            em.persist(item);
        } else{ // 아이템 값이 있을 경우임. 살짝 업데이트랑 비슷한 느낌이라 보면된다.
            em.merge(item);
        }
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}
