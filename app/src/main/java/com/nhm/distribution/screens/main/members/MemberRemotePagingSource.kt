package com.nhm.distribution.screens.main.members
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nhm.distribution.networking.USER_TYPE
import com.nhm.distribution.networking.USER_TYPE_ADMIN
import com.nhm.distribution.networking.filterByAadhaar
import com.nhm.distribution.networking.filterByEndDate
import com.nhm.distribution.networking.filterByMobile
import com.nhm.distribution.networking.filterByName
import com.nhm.distribution.networking.filterByStartDate
import com.nhm.distribution.networking.getJsonRequestBody
import com.nhm.distribution.networking.page
import com.nhm.distribution.networking.search_input
import com.nhm.distribution.networking.user_id
import com.nhm.distribution.networking.user_type
import com.nhm.distribution.screens.main.NBPA.ApiRepository
import com.nhm.distribution.screens.main.NBPA.MoviesListResponse
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.userIdForGlobal
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.userType
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class MemberRemotePagingSource (
    private val topHeadlineService: ApiRepository,
    filterNameString: String,
    filterMobileString: String,
    filterAadhaarString: String,
    filterStartDateString: String,
    filterEndDateString: String
) : PagingSource<Int, MemberMoviesListResponse.Result>() {
    override fun getRefreshKey(state: PagingState<Int, MemberMoviesListResponse.Result>): Int {
        return 0
    }
    var filterName = filterNameString
    var filterMobile = filterMobileString
    var filterAadhaar = filterAadhaarString
    var filterStartDate = filterStartDateString
    var filterEndDate = filterEndDateString

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MemberMoviesListResponse.Result> {
            Log.e("TAG", "isEmpty")
            val requestPage = params.key ?: 1

            return try {
                var obj: JSONObject = JSONObject()
//                if (userType == USER_TYPE) {
                    obj = JSONObject().apply {
                        put(page, requestPage)
//                        put(user_type, USER_TYPE)
//                        put(user_id, userIdForGlobal)
//                        put(filterByName, filterName)
//                        put(filterByMobile, filterMobile)
//                        put(filterByAadhaar, filterAadhaar)
//                        put(filterByStartDate, filterStartDate)
//                        put(filterByEndDate, filterEndDate)
//                    }
                }
//
//                if (userType == USER_TYPE_ADMIN) {
//                    obj = JSONObject().apply {
//                        put(page, requestPage)
//                        put(user_type, USER_TYPE_ADMIN)
////                        put(user_id, userIdForGlobal)
//                        put(filterByName, filterName)
//                        put(filterByMobile, filterMobile)
//                        put(filterByAadhaar, filterAadhaar)
//                        put(filterByStartDate, filterStartDate)
//                        put(filterByEndDate, filterEndDate)
//                    }
//                }

                val response = topHeadlineService.getPopularMoviesListMember(obj.getJsonRequestBody())
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    val bodydata = response.body()!!.data.size
                    Log.e("TAG", "AAAA "+bodydata)
//                    if (bodydata == 0){
//                        return LoadResult.Page(
//                            data = emptyList(),
//                            prevKey = null,
//                            nextKey = null
//                        )
//                    } else {
                        val previousLoadCount = 0.coerceAtLeast(requestPage - 1) * networkPageSize
                        val remainingCount = body.meta!!.total_items - (previousLoadCount + body.data.size)
                        val nextPage = if (remainingCount == 0) {
                            null
                        } else {
                            requestPage + 1
                        }
                        val prePage = if (requestPage == 1) null else requestPage -1
//                        val data: List<MoviesListResponse.Result> = ArrayList()
                        LoadResult.Page(
                            data = body.data,
                            prevKey = prePage,
                            nextKey = nextPage
                        )
//                    }

                } else {
                    Log.e("TAG", "BBBB")
                    LoadResult.Error(Exception("Response body Invalid"))
                }
            } catch (ex : HttpException) {
                LoadResult.Error(ex)
            } catch ( ex: IOException) {
                LoadResult.Error(ex)
            }
    }

    companion object {
        const val networkPageSize = 10
        const val initialLoad = 20
        const val prefetchDistance = 2
    }
}