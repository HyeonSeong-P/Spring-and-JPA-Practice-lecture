package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        /**
         * 다음과 같이 생성 메서드를 쓰는 걸로 방향을 잡았다면
         * 생성 메서드가 있는 클래스에 protected 생성자를 선언해서 다른 방식으로 생성하는 것을 막는 게 좋다.
         */
        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count); // 생성 메서드 사용

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);// 생성 메서드 사용

        // 주문 저장
        // Order 클래스에서 delivery와 orderItems에 설정한 cascade 옵션 덕분에
        // order하나만 persist해도 두 필드도 알아서 persist된다!
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
        /**
         * !!!JPA의 강점은 이런식으로 데이터를 변경하기만 하면 이를 감지하여 필요한 쿼리들을 자동으로 수행해준다.!!!
         * sql을 직접 다루는 다른 라이브러리들은 일일이 쿼리문을 작성해줘야한다.
         */
    }

    /**
     * 주문 검색
     */
    /*public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }*/
}
