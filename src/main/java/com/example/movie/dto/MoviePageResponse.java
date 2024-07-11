package com.example.movie.dto;

import java.util.List;

public record MoviePageResponse(List<MovieDto> movieDto,
                                Integer pageNumber,
                                Integer pageSize,
                                Long totalElement,
                                int totalPages,
                                boolean isLast) {
}
