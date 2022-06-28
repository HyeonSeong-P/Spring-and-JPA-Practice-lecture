package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 생성 메서드로만 생성하기 위해 거는 제약 조건.
public class Order {

    @Id
    @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id") //FK의 이름이 member_id가 됨
    private Member member;

    /**
     * orderitem이 여러개면
     * persist(orderItemA)
     * persist(orderItemB)
     * persist(orderItemC)
     * persist(order)
     * 이런 식으로 orderItem들을 추가해주고 그다음에 order를 저장하는 방식으로 해야하는데
     * cascade ALL 옵션을 사용하면 persist(order)만 해주면 persist를 전파해서 order에 있는 orderitems도 persist해준다.
     *
     * 주의: cascade의 경우 cascade할 엔티티(여기선 Delivery, OrderItem)가 오직 해당 클래스(여기선 Order)에서만
     * 참조될 경우면서 같은 라이프사이클에 persist될 때 사용하는게 바람직하다.
     * 만약 참조하는 엔티티들이 다른 곳에서도 참조된다면 cascade는 피하고 직접 persist하는 게 낫다.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 [ORDER,CANCEL]

    //==연관 관계 편의 메서드(양방향일 때 필수적으로 설정할 것)==//
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }
    //==연관 관계 편의 메서드(양방향일 때 필수적으로 설정할 것)==//

    //==생성 메서드==//, 주문 생성과 관련된 것은 모두 여기에 있어서 앞으로 여기만 수정하면 된다
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem: orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     */
    public void cancel(){
        if(delivery.getStatus() == DeliveryState.COMP){
            throw new IllegalStateException("이미 배송 완료된 상품은 취소가 불가능합니ㅣ다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem: orderItems){
            orderItem.cancel();
        }
    }

    //==조회 로직==/
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
        return orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum(); // 자바 스트림 이용
    }

}