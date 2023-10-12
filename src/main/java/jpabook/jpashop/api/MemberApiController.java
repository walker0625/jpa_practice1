package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {

        // 바로 이 List를 return하는 것보다는 별도의 응답객체를 사용하는 것이 추가 요구사항에 대응가능(ex : 해당 list의 count를 따로 제공)
        List<MemberDto> members = memberService.findMembers()
                                               .stream().map(m -> new MemberDto(m.getName()))
                                               .collect(Collectors.toList());

        return new Result(members.size(), members);
    }

    // Entity가 API로 노출되는 형태(지양!)
    // 1. Entity에 presentation 계층의 로직이 추가됨(각기 다른 view에 따라 Entity가 변경됨)
    // 2. Entity 구조가 노출되어 보안상 문제
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {

        // Request(DTO)와 Entity를 분리
        // 1. Entity 변경에 API 스펙이 영향을 받지 않음
        // 2. DTO 자체가 API 스펙의 문서 역할이 가능함
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findedMember = memberService.find(id); // query(조회)와 command(update)를 철저하게 분리하기 위해서 한번 더 조회(유지보수에 용이)

        return new UpdateMemberResponse(findedMember.getId(), findedMember.getName());
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

}