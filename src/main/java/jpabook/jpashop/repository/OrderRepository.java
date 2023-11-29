package jpabook.jpashop.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;

import static jpabook.jpashop.domain.QMember.member;
import static jpabook.jpashop.domain.QOrder.order;

@Repository
public class OrderRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public OrderRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {

        // static import
        return query.select(order)
                    .from(order)
                    .join(order.member, member)
                    .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
                    .limit(1000)
                    .fetch();
    }

    private BooleanExpression statusEq(OrderStatus orderStatus) {
        // 동적 쿼리
        if (orderStatus == null) {
            return null;
        }

        return order.status.eq(orderStatus);
    }

    private BooleanExpression nameLike(String memberName) {
        // 동적 쿼리
        if (!StringUtils.hasText(memberName)) {
            return null;
        }

        return member.name.like(memberName);
    }

    // Query DSL 미적용 상태
    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o " +
                                     "join fetch o.member m " +
                                     "join fetch o.delivery d", Order.class).getResultList();
    }

    public List<Order> findAllWithItem() {
        // 일대다(1:N)를 join하는 경우
        // 1. paging이 불가해짐(query에 limit/offset이 적용되지 않음 > order 기준이 아니라 불어난 row 기준으로 application memory에 다 올리고 시도(OOM 위험))
        // 2. 중복 데이터가 생김(distinct로 해결)
        return em.createQuery("select distinct o from Order o" +
                                      " join fetch o.member m" +
                                      " join fetch o.delivery d" +
                                      " join fetch o.orderItems oi" +
                                      " join fetch oi.item i", Order.class).getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Order o " +
                "join fetch o.member m " +
                "join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

}