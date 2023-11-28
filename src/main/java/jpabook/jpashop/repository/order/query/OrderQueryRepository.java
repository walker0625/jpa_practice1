package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> orders = findOrders();

        orders.forEach(o -> {
             List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
             o.setOrderItems(orderItems);
        });

        return orders;
    }

    public List<OrderQueryDto> findAllOptimizationDto() {
        List<OrderQueryDto> orders = findOrders();

        List<Long> orderIds = orders.stream()
                                    .map(o -> o.getOrderId())
                                    .collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems = em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                                               " from OrderItem oi" +
                                                               " join oi.item i" +
                                                               " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                                               .setParameter("orderIds", orderIds)
                                               .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemsMap = orderItems.stream().collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));

        orders.forEach(o -> o.setOrderItems(orderItemsMap.get(o.getOrderId())));

        return orders;
    }

    public List<OrderFlatDto> findAllFlatDto() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                 " from OrderItem oi" +
                                 " join oi.item i" +
                                 " where oi.order.id = :orderId", OrderItemQueryDto.class)
                              .setParameter("orderId", orderId)
                              .getResultList();
    }

    private List<OrderQueryDto> findOrders() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) from Order o" +
                                " join o.member m" +
                                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

}