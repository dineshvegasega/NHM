package com.ezatpanah.hilt_retrofit_paging_youtube.repository

import com.nhm.distribution.networking.ApiInterface
import com.nhm.distribution.networking.getJsonRequestBody
import org.json.JSONObject
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val apiServices: ApiInterface,
) {
    suspend fun getPopularMoviesList(jsonObject: JSONObject) = apiServices.liveScheme(requestBody = jsonObject.getJsonRequestBody())
//    suspend fun getMovieDetails(id: Int) = apiServices.getMovieDetails(id)
}