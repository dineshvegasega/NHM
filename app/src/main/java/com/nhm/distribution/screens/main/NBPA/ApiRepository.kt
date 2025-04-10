package com.nhm.distribution.screens.main.NBPA


import com.nhm.distribution.networking.ApiInterface
import okhttp3.RequestBody
import retrofit2.http.Body
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiServices: ApiInterface,
) {
//    @Query("page") page: Int, @Query("api_key") api_key: String

    suspend fun getPopularMoviesList( @Body requestBody: RequestBody) = apiServices.getPopularMoviesList(requestBody)
}