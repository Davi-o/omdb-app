package com.dvvz.omdb.controller;

import com.dvvz.omdb.service.OmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class Controller {

    @Autowired
    private OmdbService omdbService;

    @GetMapping("/")
    public Mono<String> getMovieByTitle(@RequestParam(name="t") String title){
        return omdbService.searchByTitle(title);
    }
}
