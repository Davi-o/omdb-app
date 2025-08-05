package com.dvvz.omdb.controller;

import com.dvvz.omdb.model.MovieResponse;
import com.dvvz.omdb.model.SearchResponse;
import com.dvvz.omdb.service.OmdbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:3000")
public class OmdbController {

    @Autowired
    private final OmdbService omdbService;

    public OmdbController(OmdbService omdbService) {
        this.omdbService = omdbService;
    }

    private static final Logger logger = LoggerFactory.getLogger(OmdbService.class);

    @GetMapping("/")
    public MovieResponse getMovie(
            @RequestParam(name="id", required = false) String id,
            @RequestParam(name="title", required = false) String title,
            @RequestParam(name="plot", required = false) String plot
    ){
        logger.info("Requisição de busca por produção recebida.");
        return omdbService.getMovie(id, title, plot);
    }

    @GetMapping("/search")
    public SearchResponse search(@RequestParam Map<String, String> query) {
        int page = Integer.parseInt(query.getOrDefault("page", "1"));
        query.remove("page");
        return omdbService.searchMovies(query, page);
    }

}
