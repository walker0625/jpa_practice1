package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@SpringBootTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional
    //@Rollback(value = false) 실제 db 확인용
    void testMember() {
        Member member = new Member();
        member.setUsername("memberA");

        Long id = memberRepository.save(member);
        Member member1 = memberRepository.find(id);

        Assertions.assertThat(member.getId()).isEqualTo(member1.getId());
        Assertions.assertThat(member.getUsername()).isEqualTo(member1.getUsername());
    }

}