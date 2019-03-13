package com.example.mvisample.data.remote

import com.example.mvisample.domain.entity.Movie
import com.squareup.moshi.Json

data class ApiResult(
    @field:Json(name = "page")
    val page: Int?,
    @field:Json(name = "results")
    val movies: List<ApiMovie?>?,
    @field:Json(name = "dates")
    val dates: ApiDates?,
    @field:Json(name = "total_pages")
    val totalPages: Int?,
    @field:Json(name = "total_results")
    val totalResults: Int?
)

data class ApiMovie(
    @field:Json(name = "adult")
    val adult: Boolean?,
    @field:Json(name = "backdrop_path")
    val backdropPath: String?,
    @field:Json(name = "genre_ids")
    val genreIds: List<Int?>?,
    @field:Json(name = "id")
    val id: Int?,
    @field:Json(name = "original_language")
    val originalLanguage: String?,
    @field:Json(name = "original_title")
    val originalTitle: String?,
    @field:Json(name = "overview")
    val overview: String?,
    @field:Json(name = "popularity")
    val popularity: Double?,
    @field:Json(name = "poster_path")
    val posterPath: String?,
    @field:Json(name = "release_date")
    val releaseDate: String?,
    @field:Json(name = "title")
    val title: String?,
    @field:Json(name = "video")
    val video: Boolean?,
    @field:Json(name = "vote_average")
    val voteAverage: Double?,
    @field:Json(name = "vote_count")
    val voteCount: Int?
)

data class ApiDates(
    @field:Json(name = "maximum")
    val maximum: String?,
    @field:Json(name = "minimum")
    val minimum: String?
)

fun ApiMovie.toMovie(): Movie? {
    if (
        id != null
        && title != null
        && posterPath != null
    ) {
        return Movie(
            id,
            title,
            "https://image.tmdb.org/t/p/w300/$posterPath"
        )
    }

    return null
}