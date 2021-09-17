package com.example.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("hello")
    public String hello(Model model){
        // model에 데이터 넣어 view로 전달
        model.addAttribute("data","hello!");
        return "hello";
    }
}
