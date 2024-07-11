package com.example.movie.service.Impl;

import com.example.movie.dto.MovieDto;
import com.example.movie.dto.MoviePageResponse;
import com.example.movie.entity.Movie;
import com.example.movie.exception.MovieNotFoundException;
import com.example.movie.repositories.MovieRepository;
import com.example.movie.service.FileService;
import com.example.movie.service.MovieService;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


@Service
public class MovieServiceImpl implements MovieService {

    @Value("${project.poster}")
    private String path;

    @Value("${project.baseUrl}")
    private String baseUrl;
    private final MovieRepository movieRepository;
    private final FileService fileService;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }


    @Override
    public ResponseEntity<MovieDto> addMovie(MovieDto movieDto, MultipartFile multipartFile) throws IOException {

        if(Files.exists(Paths.get(path+File.separator+multipartFile.getOriginalFilename()))){
            throw new FileAlreadyExistsException("File Already exists! Please enter another file name!");
        }
        // save upload file and return the file name
        String uploadFileName=fileService.uploadFile(path,multipartFile);

        //set poster name to MovieDto Object
        movieDto.setPoster(uploadFileName);

        //Save Movie object
        Movie movie=toMovie(movieDto);
        movie.setMovieId(0);
        movieRepository.save(movie);

        //generate Postre URL
        String posterUrl=baseUrl+"/file/"+uploadFileName;

        //mao to MovieDto
        MovieDto movieDto1=toMovieDto(movie,posterUrl);


        return new ResponseEntity<>(movieDto1, HttpStatus.CREATED);
    }

    @Override
    public MovieDto getMovie(Integer movieId) {

        Movie movie=movieRepository.findById(movieId).orElseThrow(()->new MovieNotFoundException("Movie not found..."));



        //System.out.println(movie1);

        //generate Postre URL
        String posterUrl=baseUrl+"/file/"+movie.getPoster();


        MovieDto movieDto= toMovieDto(movie,posterUrl);
       // System.out.println(movieDto);

        return movieDto;

    }

    @Override
    public List<MovieDto> getAllMovies() {
        List<Movie> moviList=movieRepository.findAll();
        List<MovieDto> movieDtoList=new ArrayList<>();

        for (Movie m:moviList
             ) {
            String posterUrl=baseUrl+"/file/"+m.getPoster();
            MovieDto movieDto= toMovieDto(m,posterUrl);
            movieDtoList.add(movieDto);
        }

       return movieDtoList;
    }

    @Override
    public MovieDto updateMovie(Integer id, MovieDto movieDto, MultipartFile file) throws IOException {

        Movie movie=movieRepository.findById(id).orElseThrow(()->new MovieNotFoundException("Movie not found..."));

        String fileName=movie.getPoster();

        if(file!=null){
            Files.deleteIfExists(Paths.get(path+ File.separator+fileName));

            fileName=fileService.uploadFile(path,file);
        }

        movieDto.setPoster(fileName);
        Movie m=toMovie(movieDto);

        String posterUrl=baseUrl+"/file/"+m.getPoster();
        movieRepository.save(m);

        return toMovieDto(m,posterUrl);

    }

    @Override
    public String deleteMovie(Integer id) throws IOException {
        Movie movie=movieRepository.findById(id).orElseThrow(()->new MovieNotFoundException("Movie not found..."));

        Integer mId=movie.getMovieId();

        Files.deleteIfExists(Paths.get(path+File.separator+movie.getPoster()));

        movieRepository.deleteById(mId);

        return "Movie Deleted Succesfully....";
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);

      Page<Movie> moviePage= movieRepository.findAll(pageable);

      List<Movie> movies=moviePage.getContent();

        List<MovieDto> movieDtoList=new ArrayList<>();

        for (Movie m:movies
        ) {
            String posterUrl=baseUrl+"/file/"+m.getPoster();
            MovieDto movieDto= toMovieDto(m,posterUrl);
            movieDtoList.add(movieDto);
        }

        return new MoviePageResponse(movieDtoList,pageNumber,pageSize,moviePage.getTotalElements(),moviePage.getTotalPages(),moviePage.isLast());


    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationwithSort(Integer pageNumber, Integer pageSize, String sortBy, String dir) {


        Sort sort=dir.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);

        Page<Movie> moviePage= movieRepository.findAll(pageable);

        List<Movie> movies=moviePage.getContent();

        List<MovieDto> movieDtoList=new ArrayList<>();

        for (Movie m:movies
        ) {
            String posterUrl=baseUrl+"/file/"+m.getPoster();
            MovieDto movieDto= toMovieDto(m,posterUrl);
            movieDtoList.add(movieDto);
        }

        return new MoviePageResponse(movieDtoList,pageNumber,pageSize,moviePage.getTotalElements(),moviePage.getTotalPages(),moviePage.isLast());

    }


    private Movie toMovie(MovieDto movieDto){
        return Movie.builder()
                .movieId(movieDto.getMovieId())
                .title(movieDto.getTitle())
                .director(movieDto.getDirector())
                .studio(movieDto.getStudio())
                .movieCast(movieDto.getMovieCast())
                .releaseYear(movieDto.getReleaseYear())
                .poster(movieDto.getPoster())
                .build();
    }

    private MovieDto toMovieDto(Movie movie,String url){
        return MovieDto.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .director(movie.getDirector())
                .studio(movie.getStudio())
                .movieCast(movie.getMovieCast())
                .releaseYear(movie.getReleaseYear())
                .poster(movie.getPoster())
                .posterUrl(url)
                .build();
    }
}
