package com.dvvz.omdb.service;

import com.dvvz.omdb.config.OmdbParamMap;
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
import java.util.Map;

@Service
public class OmdbService {

    private static final Logger logger = LoggerFactory.getLogger(OmdbService.class);

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;
    private final String[] plotTypes;
    private final OmdbParamMap paramMap;

    public OmdbService(
            RestTemplate restTemplate,
            @Value("${omdb.api.url}") String apiUrl,
            @Value("${omdb.api.key}") String apiKey,
            @Value("${omdb.plot.types}") String[] plotTypes,
            OmdbParamMap paramMap
    ) {
        this.restTemplate = restTemplate;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.plotTypes = plotTypes;
        this.paramMap = paramMap;
    }

    @Cacheable(value = "movie", key = "T(String).format('%s_%s_%s_', #id, #title, #plot)")
    public MovieResponse getMovie(String id, String title, String plot) {
        logger.info("movie.get.started id= {}, title= {}, plot= {}", id, title, plot);

        try {
            UriComponentsBuilder url = buildBaseUrl()
                    .queryParamIfPresent("i", optionalNonEmpty(id))
                    .queryParamIfPresent("t", optionalNonEmpty(title));

            if (isValidPlot(plot)) {
                url.queryParam("plot", plot);
            }

            logger.info("movie.get.url={}", url.toUriString());
            MovieResponse movie = restTemplate.getForObject(url.toUriString(), MovieResponse.class);

            logger.info("movie.get.id='{}'", movie != null ? movie.imdbID : "null");
            return movie;

        } catch (RestClientException e) {
            logger.error("movie.get.failed error={}", e.getMessage(), e);
            throw e;
        }
    }

    @Cacheable(value = "movies", key = "#query['title'] + '_' + #query['type'] + '_' + #query['year'] + '_' + #page")
    public SearchResponse searchMovies(Map<String, String> query, int page) {
        logger.info("movie.search.started query={}, page={}", query, page);

        try {
            UriComponentsBuilder url = buildBaseUrl();

            addQueryParams(url, query);
            addPageParam(url, page);

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

    private UriComponentsBuilder buildBaseUrl() {
        return UriComponentsBuilder.fromHttpUrl(apiUrl).queryParam("apikey", apiKey);
    }

    private java.util.Optional<String> optionalNonEmpty(String value) {
        return (value == null || value.isEmpty()) ? java.util.Optional.empty() : java.util.Optional.of(value);
    }

    private boolean isValidPlot(String plot) {
        return plot != null && Arrays.asList(plotTypes).contains(plot);
    }

    private void addQueryParams(UriComponentsBuilder url, Map<String, String> query) {
        paramMap.getParamMapping().forEach((clientKey, omdbKey) -> {
            String value = query.get(clientKey);
            if (value != null && !value.isEmpty()) {
                url.queryParam(omdbKey, value);
            }
        });
    }

    private void addPageParam(UriComponentsBuilder url, int page) {
        if (page > 0) {
            url.queryParam("page", page);
        }
    }
}

