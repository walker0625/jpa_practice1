package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    // merge를 지양하고 변경감지만을 사용!!!(null로 업데이트 되는 상황 방지!)
    // 엔티티 조회하여 영속상태로 만들고 set등을 통해(가급적 의미있는 변경 메소드 사용)
    // 1차 캐시에 있는 엔티티를 업데이트(dirty checking)
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findedItem = itemRepository.findOne(itemId);
        // setter보다는 메소드로 만들어서 변경 요망
        findedItem.setName(name);
        findedItem.setPrice(price);
        findedItem.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long id) {
        return itemRepository.findOne(id);
    }

}
