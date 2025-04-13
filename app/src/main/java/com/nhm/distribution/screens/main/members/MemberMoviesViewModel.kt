package com.nhm.distribution.screens.main.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nhm.distribution.screens.main.NBPA.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class MemberMoviesViewModel @Inject constructor(private val repository: ApiRepository) : ViewModel() {

    var filterNameBoolean = false
    var filterMobileBoolean = false
    var filterAadhaarBoolean = false
    var filterStartDateBoolean = false
    var filterEndDateBoolean = false

    var filterName = ""
    var filterMobile= ""
    var filterAadhaar = ""
    var filterStartDate = ""
    var filterEndDate = ""


//    val loading = MutableLiveData<Boolean>()


//    private val queryFlow = MutableStateFlow("")
//    @OptIn(FlowPreview::class)
//    private val repositoryFlow = queryFlow
//        .debounce(300)
//        .distinctUntilChanged()
//        .flatMapLatest { query ->
//            if (query.isNotEmpty()) {
//                repository.getPopularMoviesList(query).cachedIn(viewModelScope)
//            } else {
//                flowOf(PagingData.empty())
//            }
//        }
//
//    fun searchRepositories(query: String) {
//        queryFlow.value = query
//    }
//
//    fun getRepositories(): Flow<PagingData<RepositoryItem>> {
//        return repositoryFlow
//    }

//
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val moviesList = filterFlow.flatMapLatest { filter ->
//        Pager(PagingConfig(1)) {
//            MoviesPagingSource(repository, filter)
//    }.flow
//    }.cachedIn(viewModelScope)

    @OptIn(FlowPreview::class)
    fun fetch(filterName: String, filterMobile: String, filterAadhaar: String, filterStartDate: String, filterEndDate: String): Flow<PagingData<MemberMoviesListResponse.Result>> {
        val filterFlow = MutableStateFlow<String>(filterName)
//            .debounce(300)
//            .distinctUntilChanged()
        @OptIn(ExperimentalCoroutinesApi::class)
        val moviesList = filterFlow.flatMapLatest { filter ->
            Pager(PagingConfig(1)) {
                MemberRemotePagingSource(repository, filterName, filterMobile, filterAadhaar, filterStartDate, filterEndDate)
            }.flow
        }.cachedIn(viewModelScope)
//        if (filterName.isNotEmpty()) {
//            moviesList
//        } else {
//            flowOf(PagingData.empty())
//        }
        return moviesList
    }

//    fun getFlow(startsWith: String) : Flow<PagingData<MoviesListResponse.Result>> {
//        return Pager(
//            config = PagingConfig(
//                pageSize = NewsRemotePagingSource.networkPageSize,
//                initialLoadSize = NewsRemotePagingSource.initialLoad,
//                prefetchDistance = NewsRemotePagingSource.prefetchDistance,
//                enablePlaceholders = false
//            ),
//            initialKey = 1,
//            pagingSourceFactory = { NewsRemotePagingSource(startsWith,repository) }
//        ).flow
//            .cachedIn(viewModelScope)
//    }

//    var moviesList = Pager(PagingConfig(1)) {
//        MoviesPagingSource(
//            repository,
//            if(filterNameBoolean) filterName else "",
//            if(filterMobileBoolean) filterMobile else "",
//            if(filterAadhaarBoolean) filterAadhaar else "",
//            filterStartDate,
//            filterEndDate)
//    }.flow.cachedIn(viewModelScope)

//    var moviesList = Pager(PagingConfig(1)) {
//        NewsRemotePagingSource(
//            repository,
//            if(filterNameBoolean) filterName else "",
//            if(filterMobileBoolean) filterMobile else "",
//            if(filterAadhaarBoolean) filterAadhaar else "",
//            filterStartDate,
//            filterEndDate)
//    }.flow.cachedIn(viewModelScope)



//    fun invalidateResultList() {
//        moviesList?.sourceLiveData?.value?.invalidate()
//    }

//    //Api
//    val detailsMovie = MutableLiveData<MovieDetailsResponse>()
//    fun loadDetailsMovie(id: Int) = viewModelScope.launch {
//        loading.postValue(true)
//        val response = repository.getMovieDetails(id)
//        if (response.isSuccessful) {
//            detailsMovie.postValue(response.body())
//        }
//        loading.postValue(false)
//    }


}



