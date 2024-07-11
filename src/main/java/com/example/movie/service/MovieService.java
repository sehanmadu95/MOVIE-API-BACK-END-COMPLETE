package com.example.movie.service;

import com.example.movie.dto.MovieDto;
import com.example.movie.dto.MoviePageResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    ResponseEntity<MovieDto> addMovie(MovieDto movieDto, MultipartFile multipartFile) throws IOException;

    MovieDto getMovie(Integer movieId);

    List<MovieDto> getAllMovies();

    MovieDto updateMovie(Integer id,MovieDto movieDto,MultipartFile file) throws IOException;

    String deleteMovie(Integer id) throws IOException;

    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber,Integer pageSize);

    MoviePageResponse getAllMoviesWithPaginationwithSort(Integer pageNumber, Integer pageSize, String sortBy, String dir);
}
