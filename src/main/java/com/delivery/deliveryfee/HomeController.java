package com.delivery.deliveryfee;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Simple ThymeLeaf template
     *
     * @return renders index.html
     */
    @GetMapping("/")
    public String deliveryFeeCalculator() {
        return "index";
    }

}
