package com.nhm.distribution.screens.main.changePassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhm.distribution.networking.ApiInterface
import com.nhm.distribution.networking.CallHandler
import com.nhm.distribution.networking.Repository
import com.google.gson.JsonElement
import com.nhm.distribution.R
import com.nhm.distribution.models.BaseResponseDC
import com.nhm.distribution.screens.mainActivity.MainActivity
import com.nhm.distribution.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ChangePasswordVM @Inject constructor(private val repository: Repository): ViewModel() {
    var isAgree = MutableLiveData<Boolean>(false)


    var itemUpdatePasswordResult = MutableLiveData<Boolean>(false)
    fun updatePassword(hashMap: RequestBody) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.passwordUpdate(hashMap)
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        itemUpdatePasswordResult.value = true
                        showSnackBar(MainActivity.mainActivity.get()!!.getString(R.string.password_changed_successfully))
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }

}