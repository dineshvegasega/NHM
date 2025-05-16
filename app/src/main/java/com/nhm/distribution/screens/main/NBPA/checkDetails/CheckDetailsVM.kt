package com.nhm.distribution.screens.main.NBPA.checkDetails

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhm.distribution.models.ItemFormListDetail
import com.nhm.distribution.models.ItemNBPAFormRoot
import com.nhm.distribution.networking.ApiInterface
import com.nhm.distribution.networking.CallHandler
import com.nhm.distribution.networking.Repository
import com.nhm.distribution.networking.getJsonRequestBody
import com.nhm.distribution.utils.mainThread
import com.nhm.distribution.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CheckDetailsVM @Inject constructor(private val repository: Repository): ViewModel() {


//    fun checkAadhaarNo(
//        view: View,
//        _id: String,  callBack: String.() -> Unit
//    ) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<ItemFormListDetail>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.checkAadhaarNo(_id)
//
//                override fun success(response: Response<ItemFormListDetail>) {
//                    if (response.isSuccessful) {
////                        isProductLoad = true
////                        isProductLoadMember = true
////                        showSnackBar(view.resources.getString(R.string.forms_added_successfully))
//                        callBack("response.body()!!")
//
////                        view.findNavController()
////                            .navigate(R.id.action_nbpa_to_nbpaList)
//                    } else {
//                        showSnackBar(response.body()?.message.orEmpty())
//                    }
//                }
//
//                override fun error(message: String) {
//                    super.error(message)
//                    showSnackBar(message)
//                }
//
//                override fun loading() {
//                    super.loading()
//                }
//            }
//        )
//    }






//    private var itemProducstResult = MutableLiveData<ItemNBPAFormRoot>()
//    val itemProducts: LiveData<ItemNBPAFormRoot> get() = itemProducstResult
    fun checkAadhaarNo(emptyMap: JSONObject, callBack: ItemNBPAFormRoot.() -> Unit) =
        viewModelScope.launch {
                repository.callApi(
                    callHandler = object : CallHandler<Response<ItemNBPAFormRoot>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.checkAadhaarNo(emptyMap.getJsonRequestBody())

                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<ItemNBPAFormRoot>) {
                            if (response.isSuccessful) {
                                callBack(response.body()!!)
                            }
                        }

                        override fun error(message: String) {

                        }

                        override fun loading() {
                            super.loading()
                        }
                    }
                )
        }
}