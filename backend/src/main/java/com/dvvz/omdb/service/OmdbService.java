package com.dvvz.omdb.service;

import com.dvvz.omdb.model.MovieResponse;
import com.dvvz.omdb.model.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

@Service
public class OmdbService {

    private final RestTemplate restTemplate;

    private final String[] plotTypes;

    private final String apiUrl;

    private final String apiKey;

    private static final Logger logger = LoggerFactory.getLogger(OmdbService.class);

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

    @Cacheable(value = "movie", key = "T(String).format('%s_%s_%s_%s_%s', #id, #title, #year, #type, #plot)")
    public MovieResponse getMovie(
            String id,
            String title,
            String year,
            String type,
            String plot
    ) {
        logger.info("movie.get.started id= {}, title= {}, year= {}, type= {}, plot= {}", id, title, year, type, plot);
        try {
            UriComponentsBuilder url = UriComponentsBuilder
                    .fromHttpUrl(apiUrl)
                    .queryParam("apikey", apiKey);

            if (id != null) url.queryParam("i", id);
            if (title != null) url.queryParam("t", title);
            if (year != null) url.queryParam("y", year);
            if (type != null) url.queryParam("type", type);
            if (plot != null && Arrays.asList(plotTypes).contains(plot)) url.queryParam("plot", plot);

            logger.info("movie.get.url={}", url.toUriString());

            MovieResponse movie = restTemplate.getForObject(url.toUriString(), MovieResponse.class);
            logger.info("movie.get.id='{}'", movie != null ? movie.imdbID : "null");

            return movie;
        }catch (RestClientException e) {
            logger.error("movie.get.failed error={}", e.getMessage(), e);
            throw e;
        }
    }

    @Cacheable(value = "movies", key = "#query + '_' + #page")
    public SearchResponse searchMovies(String query, int page) {
        logger.info("movie.search.started title='{}' page={}", query, page);
        try {
            UriComponentsBuilder url = UriComponentsBuilder
                    .fromHttpUrl(apiUrl)
                    .queryParam("apikey", apiKey)
                    .queryParam("s", query);

            if(page != 0) url.queryParam("page", page);

            logger.info("movie.search.url={}", url.toUriString());
            SearchResponse response = restTemplate.getForObject(url.toUriString(), SearchResponse.class);

            if (response != null && response.getMovies() != null) {
                logger.info("movie.search.found count={} .", response.getMovies().size());
                return response;
            } else {
                logger.warn("movie.search.not_found query='{}'", query);
                return new SearchResponse();
            }
        } catch (RestClientException e) {
            logger.error("movie.search.failed error={}", e.getMessage(), e);
            throw e;
        }
    }
}
