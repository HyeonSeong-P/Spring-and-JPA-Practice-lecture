package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 먼저 Order에 xToOne 관계에 있는 것들에 대한 작업 먼저 수행
 * xToOne(ManyToOne, OneToOne) 관계 최적화
 * Order
 * Order -> Member
 * Order -> Delivery
 */

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;

    private final OrderSimpleQueryRepository orderSimpleQueryRepository;
    /**
     * V1. 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY = null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     *
     * - api 스펙에 필요하지 않은 정보에 대한 쿼리들이 날라가면서 성능문제도 발생시킨다
     * - 위의 문제 이외에 이전에 적었던 문제점들도 같이 있다 보면 된다.*/
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        /**
         * 발생하는 문제 1. 양방향 관계 무한 루프
         * -> (해결법)양방향 연관관계의 경우 한 쪽은 @JsonIgnore를 붙여줘야 무한 루프가 발생하지 않는다.
         *
         * 발생하는 문제 2. 지연로딩으로 인한 프록시 객체를 json이 어떻게 생성하는지 몰라 예외발생
         * -> Hibernate5Module 사용
         * */
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for(Order order:all){
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }
        return all;
    }

    /**
     * List를 감싸는 Result 클래스를 만드는 게 맞는데 예제라 생략
     *
     * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용 X)
     * - 단점: 지연 로딩으로 쿼리 1 + N + N번 호출
     *  - order->member 지연 로딩 조회 N번
     *  - order->delivery 지연 로딩 조회 N번
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        return orderRepository.findAllByString(new OrderSearch()).stream()
                .map(o->new SimpleOrderDto(o))
                .collect(Collectors.toList());
    }

    /**
     * List를 감싸는 Result 클래스를 만드는 게 맞는데 예제라 생략
     *
     * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용 O)
     * - fetch join 으로 쿼리 1번 호출
     *
     * 엔티티를 호출해서 DTO로 변환하는 작업을 거침
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        return orderRepository.findAllWithMemberDelivery().stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());
    }

    /**
     * V4. JPA에서 DTO로 바로 조회
     * select 절에서 원하는 데이터만 선택해서 조회
     *
     * V3와 V4는 트레이드 오프가 존재한다.
     *
     * V3의 경우 엔티티로 조회해서 리포지토리 재사용성도 좋고 개발도 단순해진다.
     * 하지만 원하는 것만 select해서 들고오는 V4에 비해 성능적으로 조금 떨어진다는 단점이 있다.
     *
     * V4의 경우 select 절에서 원하는 데이터만 조회해서 네트워크 용량을 최적화하여 성능적으로 이점을 볼 수 있다(다만 효과가 미비하다)
     * 하지만 repository 재사용성이 떨어지고,
     * API 스펙에 맞춘 코드가 리포지토리에 들어가는 단점이 있다
     * (하지만 두번째 단점의 경우 repository 패키지 내에 API 스펙에 맞춘 패키지와 repository를 따로 추출해서 사용하면 해결 가능하다, OrderSimpleQueryRepository 참고)
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4(){
        return orderSimpleQueryRepository.findOrderDtos();
    }


    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초과
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초과
        }
    }
}
