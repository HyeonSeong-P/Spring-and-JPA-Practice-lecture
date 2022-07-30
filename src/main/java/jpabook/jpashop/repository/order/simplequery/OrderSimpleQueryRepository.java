package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;


/**
 * 특정 API 스펙에 맞추어 해당 API이외에 코드의 재사용이 불가할 땐
 * 이런 식으로 특정 쿼리들을 위한 패키지와 Repository를 따로 추출해서 사용하는 것이 좋다.
 */
@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {
    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery("select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id,m.name,o.orderDate,o.status,m.address) from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();

    }
}
