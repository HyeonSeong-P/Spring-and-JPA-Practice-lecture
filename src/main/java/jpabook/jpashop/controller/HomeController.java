package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j //lombok의 로그용 어노테이션
public class HomeController {
    @RequestMapping("/") // "/",즉 홈 주소가 들어오면 home.html로 맵핑해준다.
    public String home(){
        log.info("home controller");
        return "home";
    }
}
