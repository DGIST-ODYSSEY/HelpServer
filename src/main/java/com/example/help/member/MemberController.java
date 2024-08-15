package com.example.help.member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
