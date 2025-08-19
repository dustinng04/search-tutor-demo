package com.example.dev.Controller;

import com.example.dev.dto.SearchRequestParams;
import com.example.dev.dto.SearchResponse;
import com.example.dev.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MyController {
    final SearchService searchService;

    @GetMapping("/search")
    @ResponseBody
    public SearchResponse search(SearchRequestParams searchRequestParams) {
        return searchService.search(searchRequestParams);
    }
}

@Controller
class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
}
