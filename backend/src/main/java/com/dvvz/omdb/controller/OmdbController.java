package com.dvvz.omdb.controller;

import com.dvvz.omdb.model.MovieResponse;
import com.dvvz.omdb.model.SearchResponse;
import com.dvvz.omdb.service.OmdbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @RequestParam(name="i", required = false) String id,
            @RequestParam(name="t", required = false) String title,
            @RequestParam(name="y", required = false) String year,
            @RequestParam(name="type", required = false) String type,
            @RequestParam(name="plot", required = false) String plot
    ){
        logger.info("Requisição de busca por produção recebida.");
        return omdbService.getMovie(id, title, year, type, plot);
    }

    @GetMapping("/search")
    public SearchResponse searchMovies(
            @RequestParam(name="s", required = true) String query,
            @RequestParam(name="page", required = false, defaultValue = "0") int page
    ) {
        logger.info("Requisição de busca recebida.");
        return omdbService.searchMovies(query, page);
    }
}
