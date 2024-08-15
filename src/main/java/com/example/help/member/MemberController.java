package com.example.help.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/login")
    public String addMember(@RequestBody Member member) {
        member.setUsername(member.getUsername());
        memberRepository.save(member);
        return "redirect:/board";
    }
}
