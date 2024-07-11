package com.example.movie.controller;

import com.example.movie.dto.MovieDto;
import com.example.movie.dto.MoviePageResponse;
import com.example.movie.exception.EmptyFileException;
import com.example.movie.service.MovieService;
import com.example.movie.utils.AppConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/movie")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<MovieDto>addMovie(String movieDto, MultipartFile file) throws IOException {
        if(file.isEmpty()){
            throw new EmptyFileException("Empty file! Select file and try again!");
        }
        MovieDto movieDto1=convertToMovieDto(movieDto);

        return movieService.addMovie(movieDto1,file);
    }
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/get/{id}")
    public MovieDto getMovie(@PathVariable int id){
        return movieService.getMovie(id);
    }

    @GetMapping("/getAll")
    public List<MovieDto> getAllMovie(){
        return movieService.getAllMovies();
    }

    @PutMapping("/update/{id}")
    public MovieDto updateMovie(@PathVariable int id,String movieDto,MultipartFile file) throws IOException {
        if(file.isEmpty()) file=null;

        MovieDto movieDto1=convertToMovieDto(movieDto);

        return movieService.updateMovie(id,movieDto1,file);

    }

    @DeleteMapping("/delete/{id}")
    public String deleteMovie(@PathVariable int id) throws IOException {
        return movieService.deleteMovie(id);
    }


    @GetMapping("/getMoviePages")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER,required = false)Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize)
    {
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber,pageSize));
    }


    @GetMapping("/allMoviePagesSort")
    public ResponseEntity<MoviePageResponse> getMoviesWithPaginationWithSort(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER,required = false)Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false)Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY,required = false)String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR,required = false)String dir)
    {
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationwithSort(pageNumber,pageSize,sortBy,dir));
    }

    private MovieDto convertToMovieDto(String movieDto) throws JsonProcessingException {
        return new ObjectMapper().readValue(movieDto, MovieDto.class);
    }

}
