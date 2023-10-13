package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.SimpleOrderQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        // 강제 lazy 로딩을 실행
        for (Order order : orders) {
            order.getMember().getName();
            order.getDelivery().getAddress();
        }

        return orders;
    }

    // entity를 dto로 변환하여 response
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {
        return orderRepository.findAllByString(new OrderSearch()).stream()
                              .map(SimpleOrderDto::new) // 생성과정에서 query 계속 나감(튜닝 필요)
                              .collect(toList());
    }

    // fetch join으로 1 + N + N 쿼리를 1번으로 줄임
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        return  orderRepository.findAllWithMemberDelivery().stream()
                               .map(SimpleOrderDto::new)
                               .collect(toList());
    }

    // DTO로 바로 반환(필요한 column만 select 가능)
    // 성능상 이점(네트워크 상 - 미비함)은 있으나 해당 api 용으로만 사용 가능(dto로 조회 했으므로)
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // 쿼리 실행(Lazy)
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // 쿼리 실행(Lazy)
        }
    }

}
