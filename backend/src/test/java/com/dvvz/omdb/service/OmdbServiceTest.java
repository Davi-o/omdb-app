package com.dvvz.omdb.service;

import com.dvvz.omdb.config.OmdbParamMap;
import com.dvvz.omdb.exception.RateLimitExceededException;
import com.dvvz.omdb.model.MovieResponse;
import com.dvvz.omdb.model.SearchResponse;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

class OmdbServiceTest {

    private RestTemplate restTemplate;
    private MovieResponse expectedMovie;
    private SearchResponse expectedResponse;
    private OmdbService service;
    private Bucket bucket;

    @BeforeEach
    void setup() {
        restTemplate = Mockito.mock(RestTemplate.class);

        expectedMovie = new MovieResponse();
        expectedMovie.Title = "Matrix";
        expectedMovie.Year = "1993";
        expectedMovie.imdbID = "tt0106062";

        expectedResponse = new SearchResponse();
        expectedResponse.setMovies(List.of(expectedMovie));

        OmdbParamMap paramMap = Mockito.mock(OmdbParamMap.class);
        Mockito.when(paramMap.getParamMapping()).thenReturn(Map.of(
                "title", "t",
                "year", "y",
                "type", "type",
                "page", "page",
                "s", "s"
        ));

        bucket = Mockito.mock(Bucket.class);
        Mockito.when(bucket.tryConsume(1)).thenReturn(true);

        service = new OmdbService(
                restTemplate,
                "https://api.com",
                "123",
                new String[]{"short", "full"},
                paramMap,
                bucket
        );
    }

    @Test
    void givenValidTitleThenReturnMovie() {
        Mockito.when(restTemplate.getForObject(
                Mockito.anyString(),
                Mockito.eq(MovieResponse.class)
        )).thenReturn(expectedMovie);

        MovieResponse result = service.getMovie(null, "matrix", null);

        Assertions.assertEquals(expectedMovie, result);
    }

    @Test
    void givenAllParamsThenReturnMovie() {
        Mockito.when(restTemplate.getForObject(
                Mockito.anyString(),
                Mockito.eq(MovieResponse.class)
        )).thenReturn(expectedMovie);

        MovieResponse result = service.getMovie("101", "matrix", "full");

        Assertions.assertEquals(expectedMovie, result);
    }

    @Test
    void givenValidSearchQueryThenReturnMovieList() {
        Map<String, String> queryParams = Map.of("title", "matrix");

        Mockito.when(restTemplate.getForObject(
                Mockito.argThat(uri -> uri.toString().contains("t=matrix") && uri.toString().contains("page=1")),
                Mockito.eq(SearchResponse.class)
        )).thenReturn(expectedResponse);

        SearchResponse movies = service.searchMovies(queryParams, 1);

        Assertions.assertNotNull(movies);
        Assertions.assertEquals(1, movies.getMovies().size());
        Assertions.assertEquals("Matrix", movies.getMovies().getFirst().Title);

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        Mockito.verify(restTemplate).getForObject(uriCaptor.capture(), Mockito.eq(SearchResponse.class));

        String capturedUri = uriCaptor.getValue().toString();
        Assertions.assertTrue(capturedUri.contains("apikey=123"));
        Assertions.assertTrue(capturedUri.contains("t=matrix"));
        Assertions.assertTrue(capturedUri.contains("page=1"));
    }

    @Test
    void givenEmptyOrNullValuesThenParamsAreIgnored() {
        Map<String, String> queryParams = Map.of(
                "title", "matrix",
                "year", "",
                "type", ""
        );

        Mockito.when(restTemplate.getForObject(
                Mockito.any(URI.class),
                Mockito.eq(SearchResponse.class)
        )).thenReturn(expectedResponse);

        SearchResponse movies = service.searchMovies(queryParams, 1);

        Assertions.assertNotNull(movies);
        Assertions.assertEquals(1, movies.getMovies().size());

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        Mockito.verify(restTemplate).getForObject(uriCaptor.capture(), Mockito.eq(SearchResponse.class));

        String uri = uriCaptor.getValue().toString();

        Assertions.assertTrue(uri.contains("apikey=123"));
        Assertions.assertTrue(uri.contains("&t=matrix"));
        Assertions.assertTrue(uri.contains("&page=1"));

        Assertions.assertFalse(uri.contains("&y="));
        Assertions.assertFalse(uri.contains("&type="));
    }

    @Test
    void givenPageParamZeroThenShouldNotAddPageParam() {
        Map<String, String> queryParams = Map.of("title", "matrix");

        Mockito.when(restTemplate.getForObject(
                Mockito.any(URI.class),
                Mockito.eq(SearchResponse.class)
        )).thenReturn(expectedResponse);

        SearchResponse response = service.searchMovies(queryParams, 0);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getMovies().size());

        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        Mockito.verify(restTemplate).getForObject(uriCaptor.capture(), Mockito.eq(SearchResponse.class));
        String uri = uriCaptor.getValue().toString();

        Assertions.assertTrue(uri.contains("apikey=123"));
        Assertions.assertTrue(uri.contains("&t=matrix"));
        Assertions.assertFalse(uri.contains("&page="));
    }


    @Test
    void givenNullResponseThenReturnEmptySearchResponse() {
        Map<String, String> queryParams = Map.of("title", "non-existing");

        Mockito.when(restTemplate.getForObject(
                Mockito.anyString(),
                Mockito.eq(SearchResponse.class)
        )).thenReturn(null);

        SearchResponse response = service.searchMovies(queryParams, 1);

        Assertions.assertNotNull(response);
        Assertions.assertNull(response.getMovies());
    }

    @Test
    void givenNullMovieListThenReturnEmptySearchResponse() {
        SearchResponse nullMovieListResponse = new SearchResponse();
        nullMovieListResponse.setMovies(null);

        Map<String, String> queryParams = Map.of("title", "matrix");

        Mockito.when(restTemplate.getForObject(
                Mockito.anyString(),
                Mockito.eq(SearchResponse.class)
        )).thenReturn(nullMovieListResponse);

        SearchResponse response = service.searchMovies(queryParams, 1);

        Assertions.assertNotNull(response);
        Assertions.assertNull(response.getMovies());
    }

    @Test
    void givenRestTemplateFailsWhenGetMovieThenThrowException() {
        Mockito.when(restTemplate.getForObject(
                Mockito.anyString(),
                Mockito.eq(MovieResponse.class)
        )).thenThrow(new RuntimeException("Falha na OMDB"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.getMovie("101", null, null);
        });
    }

    @Test
    void givenRestTemplateFailsWhenSearchMoviesThenThrowException() {
        Map<String, String> queryParams = Map.of("title", "matrix");

        Mockito.when(restTemplate.getForObject(
                Mockito.any(URI.class),
                Mockito.eq(SearchResponse.class)
        )).thenThrow(new RuntimeException("Erro OMDB"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.searchMovies(queryParams, 1);
        });
    }

    @Test
    void givenRestClientExceptionWhenGetMovieThenThrowsException() {
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(MovieResponse.class)))
                .thenThrow(new RestClientException("Erro."));

        Assertions.assertThrows(RestClientException.class, () -> {
            service.getMovie("id", "title", null);
        });
    }

    @Test
    void givenRestClientExceptionWhenSearchMoviesThenThrowsException() {
        Map<String, String> queryParams = Map.of("title", "matrix");

        Mockito.when(
                restTemplate.getForObject(
                        Mockito.any(URI.class),
                        Mockito.eq(SearchResponse.class)))
                .thenThrow(new RestClientException("Erro."));

        Assertions.assertThrows(RestClientException.class, () -> {
            service.searchMovies(queryParams, 1);
        });
    }

    @Test
    void givenBucketExceededThenThrowsRateLimitExceededException() {
        Mockito.when(bucket.tryConsume(1)).thenReturn(false);

        Map<String, String> queryParams = Map.of("title", "matrix");

        Assertions.assertThrows(RateLimitExceededException.class, () -> {
            service.searchMovies(queryParams, 1);
        });

        Mockito.verify(restTemplate, Mockito.never()).getForObject(Mockito.any(), Mockito.any());
    }
}
