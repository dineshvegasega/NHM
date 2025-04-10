package com.nhm.distribution.screens.main.informationCenter

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhm.distribution.networking.ApiInterface
import com.nhm.distribution.networking.CallHandler
import com.nhm.distribution.networking.Repository
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.nhm.distribution.R
import com.nhm.distribution.databinding.DialogBottomInformationCenterBinding
import com.nhm.distribution.databinding.LoaderBinding
import com.nhm.distribution.models.BaseResponseDC
import com.nhm.distribution.models.ItemInformationCenter
import com.nhm.distribution.models.ItemInformationDetail
import com.nhm.distribution.networking.getJsonRequestBody
import com.nhm.distribution.screens.mainActivity.MainActivity
import com.nhm.distribution.utils.glideImage
import com.nhm.distribution.utils.showSnackBar
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class InformationCenterVM @Inject constructor(private val repository: Repository): ViewModel() {

    val adapter by lazy { InformationCenterAdapter(this) }





//    var locale: Locale = Locale.getDefault()
    var alertDialog: AlertDialog? = null
    init {
        val alert = AlertDialog.Builder(MainActivity.activity.get())
        val binding =
            LoaderBinding.inflate(LayoutInflater.from(MainActivity.activity.get()), null, false)
        alert.setView(binding.root)
        alert.setCancelable(false)
        alertDialog = alert.create()
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun show() {
        viewModelScope.launch {
            if (alertDialog != null) {
                alertDialog?.dismiss()
                alertDialog?.show()
            }
        }
    }

    fun hide() {
        viewModelScope.launch {
            if (alertDialog != null) {
                alertDialog?.dismiss()
            }
        }
    }


    private var itemInformationCenterResult = MutableLiveData<BaseResponseDC<Any>>()
    val itemInformationCenter : LiveData<BaseResponseDC<Any>> get() = itemInformationCenterResult
    fun informationCenter(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.informationCenter(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        itemInformationCenterResult.value = response.body() as BaseResponseDC<Any>
                    }
                }

                override fun error(message: String) {
                    super.error(message)
//                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }



    private var itemInformationCenterResultSecond = MutableLiveData<BaseResponseDC<Any>>()
    val itemInformationCenterSecond : LiveData<BaseResponseDC<Any>> get() = itemInformationCenterResultSecond
    fun informationCenterSecond(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.informationCenter(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        itemInformationCenterResultSecond.value =  response.body() as BaseResponseDC<Any>
                    }
                }

                override fun error(message: String) {
                    super.error(message)
//                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }





    fun viewDetail(itemInformationCenter: ItemInformationCenter, position: Int, root: View, status: Int) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.informationDetail(id = ""+itemInformationCenter.information_id)
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        var data = Gson().fromJson(response.body()!!.data, ItemInformationDetail::class.java)
                        when(status){
                            in 1..2 -> {
                                val dialogBinding = DialogBottomInformationCenterBinding.inflate(root.context.getSystemService(
                                    Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                )
                                val dialog = BottomSheetDialog(root.context)
                                dialog.setContentView(dialogBinding.root)
                                dialog.setOnShowListener { dia ->
                                    val bottomSheetDialog = dia as BottomSheetDialog
                                    val bottomSheetInternal: FrameLayout =
                                        bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
                                    bottomSheetInternal.setBackgroundResource(R.drawable.bg_top_round_corner)
                                }
                                dialog.show()

                                dialogBinding.apply {
                                    data.cover_image?.url?.glideImage(root.context, ivMap)
                                    textTitle.setText(itemInformationCenter.title)
                                    textDesc.setText(itemInformationCenter.description)
//                                    textHeaderTxt4.setText(data.status)
                                    textStartDate.visibility = View.GONE
                                    textEndDate.visibility = View.GONE

//                                    data.training_end_at?.let {
//                                        textEndDate.text = HtmlCompat.fromHtml("${root.context.resources.getString(R.string.end_date, "<b>"+data.training_end_at.changeDateFormat("yyyy-MM-dd", "dd MMM")+"</b>")}", HtmlCompat.FROM_HTML_MODE_LEGACY)
//                                    }

                                    btApply.text = root.context.resources.getString(R.string.watch)

                                    btApply.singleClick {
                                        if (status == 1){
                                            Handler(Looper.getMainLooper()).post(Thread {
                                                MainActivity.activity.get()?.runOnUiThread {
                                                    data.information_link?.let {
                                                        val webIntent = Intent(
                                                            Intent.ACTION_VIEW,
                                                            Uri.parse(data.information_link)
                                                        )
                                                        try {
                                                            root.context.startActivity(webIntent)
                                                        } catch (ex: ActivityNotFoundException) {
                                                        }
                                                    }
                                                }
                                            })
                                        }
                                        dialog.dismiss()
                                    }

                                    btClose.singleClick {
                                        dialog.dismiss()
                                    }
                                }
                            }

                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }



    fun callApiTranslate(_lang : String, _words: String) : String{
        return repository.callApiTranslate(_lang, _words)
    }
}