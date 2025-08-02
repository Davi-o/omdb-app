package com.dvvz.omdb.service;

import com.dvvz.omdb.model.MovieResponse;
import com.dvvz.omdb.model.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
public class OmdbService {

    private final RestTemplate restTemplate;

    private final String[] plotTypes;

    private final String apiUrl;

    private final String apiKey;

    public OmdbService(
            RestTemplate restTemplate,
            @Value("${omdb.api.url}") String apiUrl,
            @Value("${omdb.api.key}") String apiKey,
            @Value("${omdb.plot.types}") String[] plotTypes
            ) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.plotTypes = plotTypes;
    }

    public MovieResponse getMovie(
            String id,
            String title,
            String year,
            String type,
            String plot
    ) {
        UriComponentsBuilder url = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey);

        if (id != null) url.queryParam("i", id);
        if (title != null) url.queryParam("t", title);
        if (year != null) url.queryParam("y", year);
        if (type != null) url.queryParam("type", type);
        if (plot != null && Arrays.asList(plotTypes).contains(plot)) url.queryParam("plot", plot);

        return restTemplate.getForObject(url.toUriString(), MovieResponse.class);
    }

    @Cacheable(value = "movies", key = "#query + '_' + #page")
    public List<MovieResponse> searchMovies(String query, int page) {
        UriComponentsBuilder url = UriComponentsBuilder
                .fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey)
                .queryParam("s", query)
                .queryParam("page", page);

        SearchResponse response = restTemplate.getForObject(url.toUriString(), SearchResponse.class);

        if(response != null && response.getMovies() != null)
            return response.getMovies();

        return null;
    }
}
