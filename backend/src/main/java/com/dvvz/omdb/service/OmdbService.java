package com.dvvz.omdb.service;

import com.dvvz.omdb.model.MovieResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class OmdbService {

    private final RestTemplate restTemplate;

    private final String apiUrl;

    private final String apiKey;

    public OmdbService(
            RestTemplate restTemplate,
            @Value("${omdb.api.url}") String apiUrl,
            @Value("${omdb.api.key}") String apiKey
    ) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public MovieResponse getMovie(
            String id,
            String title,
            String year,
            String type,
            String plot
    ) {
        UriComponentsBuilder url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("apikey", apiKey);

        if (id != null) url.queryParam("i", id);
        if (title != null) url.queryParam("t", title);
        if (year != null) url.queryParam("y", year);
        if (type != null) url.queryParam("type", type);
        if (plot != null) url.queryParam("plot", plot);

        String urlWithParams = url.toUriString();

        return restTemplate.getForObject(urlWithParams, MovieResponse.class);
    }
}
