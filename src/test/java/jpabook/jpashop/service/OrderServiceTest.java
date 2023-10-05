package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void 상품주문() {

        // given
        Member member = createMember("memberA");
        Item book = createBook("jpa 완전정복", 10000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // then
        Order findedOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, findedOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다", 1, findedOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다", 10000 * orderCount, findedOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다", 8, book.getStockQuantity());
    }

    @Test
     void 상품주문_재고수량초과() {

        // given
        Member member = createMember("memberA");
        Item book = createBook("jpa 완전정복", 10000, 10);

        int orderCount = 11;

        // when/then
        assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), book.getId(), orderCount));
    }

    @Test
    void 주문취소() {

        // given
        Member member = createMember("memberA");
        Item book = createBook("jpa 완전정복", 10000, 10);

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order findedOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL이다", OrderStatus.CANCEL, findedOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다", 10, book.getStockQuantity());
    }

    private Item createBook(String bookName, int price, int stockQuantity) {

        Item book = new Book();
        book.setName(bookName);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);

        return book;
    }

    private Member createMember(String username) {

        Member member = new Member();
        member.setName(username);
        member.setAddress(new Address("서울","강남", "111-22"));
        em.persist(member);

        return member;
    }

}