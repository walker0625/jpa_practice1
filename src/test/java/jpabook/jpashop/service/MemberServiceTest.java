package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    void 회원가입() {

        // given
        Member member = new Member();
        member.setName("memberA");

        // when
        Long savedId = memberService.join(member);

        // then
        assertEquals(member, memberRepository.findById(savedId).get());
    }

    @Test
    void 중복_회원_예외() {

        // given
        Member member1 = new Member();
        member1.setName("kim1");

        Member member2 = new Member();
        member2.setName("kim1");

        // when
        memberService.join(member1);

        // then
        Assertions.assertThrows(IllegalStateException.class, () ->
                                memberService.join(member2));
    }

}