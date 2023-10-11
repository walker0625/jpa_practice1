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

    public void save(Item item) {
        if(item.getId() == null) {
            em.persist(item); // 신규 등록
        } else {
            em.merge(item); // like update
            // 1. item의 식별자 값으로 1차 캐시를 조회(1차 캐시에 없으면 db 조회후 1차캐시 적재)
            // 2. 조회한 엔티티의 모든 값에 item의 값을 채워 넣음
            // 3. 채워 넣은 엔티티를 반환하며 1차 캐시에 적재(item은 1차 캐시에 올라가지 않음)
            // ** merge가 위험한 이유는 item의 값을 기준으로 모든 값을 uddate(item에 값이 없으면 null로 처리해버림!)
            // dirty checking으로 하는것이 일부 update에도 용이하여 더 유연
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                 .getResultList();
    }

}
