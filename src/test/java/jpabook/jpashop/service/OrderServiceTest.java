package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

// 이 강의는 JPA 관련 동작이 원활히 되는지 확인 하는게 목표라 여기서 하는 테스트는 좋은 테스트라고는 볼 수 없다.
// 좋은 테스트는 JPA, DB등에 의존하는 것 없이 메서드 별로 순수하게 단위 테스트를 진행하는 게 바람직하다.
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
class OrderServiceTest {
    @PersistenceContext
    EntityManager em;
    @Autowired OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        // given
        Member member = createMemberForTest();

        Book book = createBookForTest("시골 JPA", 10000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER,getOrder.getStatus()); // 상품 주문시 상태는 ORDER
        assertEquals(1, getOrder.getOrderItems().size()); // 주문한 상품 종류 수가 정확해야 한다.
        assertEquals(10000*orderCount, getOrder.getTotalPrice()); // 주문 가격은 가격 * 수량이다.
        assertEquals(8,book.getStockQuantity()); // 주문 수량만큼 재고가 줄어야 한다.

    }



    @Test
    public void 상품주문_재고수량초과() throws Exception{ // 사실 이런 테스트보다 removeStock 자체에 대한 단위테스트가 중요하다.
        // given
        Member member = createMemberForTest();
        Item item = createBookForTest("시골 JPA", 10000, 10);

        int orderCount = 11;

        // when,then
        Assertions.assertThrows(NotEnoughStockException.class,
                () -> orderService.order(member.getId(), item.getId(),orderCount));
    }

    @Test
    public void 주문취소() throws Exception{
        // given
        Member member = createMemberForTest();
        Book item = createBookForTest("시골 JPA", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when , 내가 실제 테스트 하고 싶은 코드가 들어가는 부분
        orderService.cancelOrder(orderId);

        // then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getStatus()); // 주문 취소시 상태는 CANCEL 이다.
        assertEquals(10, item.getStockQuantity()); // 주문 취소된 상품은 그만큼 재고가 증가해야 한다.
    }

    private Book createBookForTest(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMemberForTest() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","강가","123-123"));
        em.persist(member);
        return member;
    }
}