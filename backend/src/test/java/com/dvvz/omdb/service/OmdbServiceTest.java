package com.dvvz.omdb.service;

import com.dvvz.omdb.model.MovieResponse;
import com.dvvz.omdb.model.SearchResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootTest
class OmdbServiceTest {

    private RestTemplate restTemplate;
    private MovieResponse expectedMovie;
    private SearchResponse expectedResponse;
    private OmdbService service;

    @BeforeEach
    void setup(){
        restTemplate = Mockito.mock(RestTemplate.class);

        expectedMovie = new MovieResponse();
        expectedMovie.Title = "Matrix";
        expectedMovie.Year = "1993";
        expectedMovie.imdbID = "tt0106062";

        expectedResponse = new SearchResponse();
        expectedResponse.setMovies(List.of(expectedMovie));

        service = new OmdbService(
                restTemplate,
                "https://api.com",
                "123",
                new String[]{"short", "full"}
        );
    }

    @Test
    void givenValidTitleThenReturnMovie() {
        Mockito.when(restTemplate.getForObject(
           Mockito.eq("https://api.com?apikey=123&t=matrix"),
           Mockito.eq(MovieResponse.class)
        )).thenReturn(expectedMovie);

        MovieResponse result = service.getMovie(null, "matrix", null,null,null);

        Assertions.assertEquals(expectedMovie, result);
    }

    @Test
    void givenAllParamsThenReturnMovie() {
        Mockito.when(restTemplate.getForObject(
                Mockito.eq("https://api.com?apikey=123&i=101&t=matrix&y=1999&type=movie&plot=full"),
                Mockito.eq(MovieResponse.class)
        )).thenReturn(expectedMovie);

        MovieResponse result = service.getMovie("101", "matrix", "1999", "movie", "full");

        Assertions.assertEquals(expectedMovie, result);
    }

    @Test
    void givenImdbIdThenReturnMovie() {
        Mockito.when(restTemplate.getForObject(
           Mockito.eq("https://api.com?apikey=123&i=101"),
           Mockito.eq(MovieResponse.class)
        )).thenReturn(expectedMovie);

        MovieResponse result = service.getMovie("101", null, null,null,null);

        Assertions.assertEquals(expectedMovie, result);
    }

    @Test
    void givenPlotTypeThenReturnMovie() {
        Mockito.when(restTemplate.getForObject(
           Mockito.eq("https://api.com?apikey=123&t=matrix&plot=short"),
           Mockito.eq(MovieResponse.class)
        )).thenReturn(expectedMovie);

        MovieResponse result = service.getMovie(null, "matrix", null,null,"short");

        Assertions.assertEquals(expectedMovie, result);
    }

    @Test
    void givenValidSearchQueryThenReturnMovieList() {
        Mockito.when(restTemplate.getForObject(
                Mockito.eq("https://api.com?apikey=123&s=matrix&page=1"),
                Mockito.eq(SearchResponse.class)
        )).thenReturn(expectedResponse);

        SearchResponse movies = service.searchMovies("matrix",1);

        Assertions.assertNotNull(movies);
        Assertions.assertEquals(1, movies.getMovies().size());
        Assertions.assertEquals("Matrix", movies.getMovies().getFirst().Title);
    }

    @Test
    void givenNullResponseThenReturnEmptySearchResponse() {
        Mockito.when(restTemplate.getForObject(
                Mockito.anyString(),
                Mockito.eq(SearchResponse.class)
        )).thenReturn(null);

        SearchResponse response = service.searchMovies("non-existing", 1);

        Assertions.assertNotNull(response);
        Assertions.assertNull(response.getMovies());
    }

    @Test
    void givenNullMovieListThenReturnEmptySearchResponse() {
        SearchResponse nullMovieListResponse = new SearchResponse();
        nullMovieListResponse.setMovies(null);

        Mockito.when(restTemplate.getForObject(
                Mockito.anyString(),
                Mockito.eq(SearchResponse.class)
        )).thenReturn(nullMovieListResponse);

        SearchResponse response = service.searchMovies("matrix", 1);

        Assertions.assertNotNull(response);
        Assertions.assertNull(response.getMovies());
    }

    @Test
    void givenPageParamZeroThenShouldNotAddPageParam() {
        Mockito.when(restTemplate.getForObject(
                Mockito.eq("https://api.com?apikey=123&s=matrix"),
                Mockito.eq(SearchResponse.class)
        )).thenReturn(expectedResponse);

        SearchResponse response = service.searchMovies("matrix", 0);

        Assertions.assertEquals(1, response.getMovies().size());
    }

    @Test
    void givenInvalidPlotTypeThenIgnorePlotParam() {
        Mockito.when(restTemplate.getForObject(
                Mockito.eq("https://api.com?apikey=123&t=matrix"),
                Mockito.eq(MovieResponse.class)
        )).thenReturn(expectedMovie);

        MovieResponse response = service.getMovie(null, "matrix", null, null, "invalid");

        Assertions.assertEquals(expectedMovie, response);
    }

    @Test
    void givenRestTemplateFailsWhenGetMovieThenThrowException() {
        Mockito.when(restTemplate.getForObject(
                Mockito.anyString(),
                Mockito.eq(MovieResponse.class)
        )).thenThrow(new RuntimeException("Falha na OMDB"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.getMovie("101", null, null, null, null);
        });
    }

    @Test
    void givenRestTemplateFailsWhenSearchMoviesThenThrowException() {
        Mockito.when(restTemplate.getForObject(
                Mockito.anyString(),
                Mockito.eq(SearchResponse.class)
        )).thenThrow(new RuntimeException("Erro OMDB"));

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.searchMovies("matrix", 1);
        });
    }

    @Test
    void givenRestClientExceptionWhenGetMovieThenThrowsException() {
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(MovieResponse.class)))
                .thenThrow(new RestClientException("Erro."));

        Assertions.assertThrows(RestClientException.class, () -> {
            service.getMovie("id", "title", "year", "type", "plot");
        });
    }

    @Test
    void givenRestClientExceptionWhenSearchMoviesThenThrowsException() {
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(SearchResponse.class)))
                .thenThrow(new RestClientException("Erro."));

        Assertions.assertThrows(RestClientException.class, () -> {
            service.searchMovies("matrix", 1);
        });
    }

}
