package jpabook.jpashop;

import jpabook.jpashop.controller.MemberForm;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class HelloController {

    private final MemberService memberService;

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello data!");
        return "/hello";
    }

}
