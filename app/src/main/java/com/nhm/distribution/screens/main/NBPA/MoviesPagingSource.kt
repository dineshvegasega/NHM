package com.nhm.distribution.screens.main.NBPA

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nhm.distribution.networking.USER_TYPE
import com.nhm.distribution.networking.getJsonRequestBody
import com.nhm.distribution.networking.page
import com.nhm.distribution.networking.userId
import com.nhm.distribution.networking.user_id
import com.nhm.distribution.networking.user_type
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.userIdForGlobal
import org.json.JSONObject
import retrofit2.HttpException

class MoviesPagingSource(
    private val repository: ApiRepository ,
) : PagingSource<Int, MoviesListResponse.Result>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MoviesListResponse.Result> {
        return try {
            val currentPage = params.key ?: 1

            val obj: JSONObject = JSONObject().apply {
                put(page, currentPage)
                put(user_type, USER_TYPE)
                put(userId, userIdForGlobal)
            }

            val response = repository.getPopularMoviesList(obj.getJsonRequestBody())
            val data = response.body()!!.data
            val responseData = mutableListOf<MoviesListResponse.Result>()
            responseData.addAll(data)

            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = if(data.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }

    }


    override fun getRefreshKey(state: PagingState<Int, MoviesListResponse.Result>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
//        return null
    }


}