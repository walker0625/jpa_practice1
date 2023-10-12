package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional//(readOnly = false)
    public Long join(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findedMembers = memberRepository.findByName(member.getName());
        if(!findedMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member find(Long id) {
        return memberRepository.find(id);
    }

    // query와 command(update)를 분리하기 위해서 void로 마무리
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.find(id);
        member.setName(name); // 변경감지를 통한 update
    }
}