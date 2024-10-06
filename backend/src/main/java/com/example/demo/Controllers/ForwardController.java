package com.example.demo.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForwardController {

    @GetMapping(value = "/{path:[^\\.]*}")
    public String forward() {
        // Redirecționează toate cererile necunoscute către index.html
        return "forward:/index.html";
    }
}