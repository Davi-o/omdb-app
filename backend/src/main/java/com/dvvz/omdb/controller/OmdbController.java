package com.dvvz.omdb.controller;

import com.dvvz.omdb.model.MovieResponse;
import com.dvvz.omdb.service.OmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class OmdbController {

    @Autowired
    private final OmdbService omdbService;

    public OmdbController(OmdbService omdbService) {
        this.omdbService = omdbService;
    }

    @GetMapping("/")
    public MovieResponse getMovie(
            @RequestParam(name="i", required = false) String id,
            @RequestParam(name="t", required = false) String title,
            @RequestParam(name="y", required = false) String year,
            @RequestParam(name="type", required = false) String type,
            @RequestParam(name="plot", required = false) String plot
    ){
        return omdbService.getMovie(id, title, year, type, plot);
    }

    @GetMapping("/search")
    public List<MovieResponse> searchMovies(
            @RequestParam(name="s", required = true) String query,
            @RequestParam(name="page", required = false, defaultValue = "1") int page
    ) {
        return omdbService.searchMovies(query, page);
    }
}
