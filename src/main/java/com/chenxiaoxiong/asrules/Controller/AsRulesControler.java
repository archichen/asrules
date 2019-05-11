package com.chenxiaoxiong.asrules.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AsRulesControler {
    @GetMapping("/")
    public String index() {
        return "index";
    }
}
