package com.example.inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // http://localhost:8082/ または http://localhost:8082/index にアクセスされた場合
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        // サイドバーの「TOP」や「ホーム」アクティブ判定用（必要に応じて）
        model.addAttribute("activeMenu", "index");
        
        // templates/index.html を呼び出す
        return "index";
    }
}