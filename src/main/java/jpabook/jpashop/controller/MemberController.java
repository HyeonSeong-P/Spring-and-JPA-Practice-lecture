package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model){
        // 이렇게 빈 Form이라도 가져가는 이유는 validation이나 그런 것들을 해주기 때문
        model.addAttribute("memberForm", new MemberForm());// 컨트롤러에서 뷰로 넘어갈 때 모델을 이용해 데이터를 실어 옮긴다.
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){ // @Vaild 어노테이션을 사용하면 javax.validation의 어노테이션을 사용한 걸 감지할 수 있다. ex. MemberForm의 name 필드의 @NotEmpty

        /**
         * @Valid 어노테이션을 통해 Invalid한 결과가 나오면 그냥 error 페이지를 띄우는데
         * BindingResult를 사용하면 error가 있는지 확인하고 그에 따른 코드를 수행할 수 있다.
         */
        if(result.hasErrors()){ // invalid한 부분이 있어 error 발생하면 회원가입 창으로 이동
            return "members/createMemberForm"; // 에러로 인해 다시 이동했을 때 화면적인 처리는 createMemberForm.html 파일을 참고
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";
    }
    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members",members);
        return "members/memberList";
    }
}
