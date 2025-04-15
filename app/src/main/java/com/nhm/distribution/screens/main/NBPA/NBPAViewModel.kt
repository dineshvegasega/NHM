package com.nhm.distribution.screens.main.NBPA

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.nhm.distribution.R
import com.nhm.distribution.databinding.ItemAllSchemesBinding
import com.nhm.distribution.databinding.LoaderBinding
import com.nhm.distribution.genericAdapter.GenericAdapter
import com.nhm.distribution.models.BaseResponseDC
import com.nhm.distribution.models.ItemLiveScheme
import com.nhm.distribution.models.ItemNBPAForm
import com.nhm.distribution.models.ItemNBPAFormRoot
import com.nhm.distribution.networking.ApiInterface
import com.nhm.distribution.networking.CallHandler
import com.nhm.distribution.networking.Repository
import com.nhm.distribution.networking.getJsonRequestBody
//import com.nhm.distribution.screens.main.NBPA.NBPADetail.Companion.change
import com.nhm.distribution.screens.mainActivity.MainActivity
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.isProductLoadMember
import com.nhm.distribution.utils.glideImagePortrait
import com.nhm.distribution.utils.mainThread
import com.nhm.distribution.utils.showSnackBar
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NBPAViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    var results: MutableList<ItemLiveScheme> = ArrayList()


    var name = ""
    var fatherHusbandType = 1 // 1 = Father, 2 = Husband
    var fatherHusband = ""
    var mother = ""
    var gender = ""
    var age = ""
    var height = ""
    var weight = ""
    var numberOfMembers = ""
    var numberOfChildren = ""
    var address = ""
    var dmcName = ""
    var block = ""
    var mobileNumber = ""
    var districtState = ""
    var cardTypeAPLBPL = 1 // 1 = APL, 2 = BPL

    var typeOfPatient = 1 // 1 = Pulmonary, 2 = Extra Pulmonary, 3 = Other
    var patientCheckupDate = ""
    var hemoglobinLevelAge = ""
    var hemoglobinCheckupDate = ""
    var muktiID = ""
    var nakshayID = ""
    var aadhaarNumber = ""
    var business = ""
    var bankAccount = ""
    var bankIFSC = ""
    var treatmentSupporterName = ""
    var treatmentSupporterPost = ""
    var treatmentSupporterMobileNumber = ""
    var treatmentSupporterEndDate = ""
    var treatmentSupporterResult = ""

    var foodMonth = ""
    var foodDate = ""
    var foodHeight = ""
    var foodSignatureImage = ""
    var foodItemImage = ""
    var foodIdentityImage = ""


    fun registerWithFiles(
        view: View,
        hashMap: RequestBody
    ) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.createFormWithFiles(hashMap)

                override fun success(response: Response<BaseResponseDC<Any>>) {
                    if (response.isSuccessful) {
                        isProductLoad = true
                        isProductLoadMember = true
                        showSnackBar(view.resources.getString(R.string.forms_added_successfully))
                        view.findNavController()
                            .navigate(R.id.action_nbpa_to_products)
                    } else {
                        showSnackBar(response.body()?.message.orEmpty())
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


    val itemsCustomerOrders: ArrayList<ItemLiveScheme> = ArrayList()

//    val adapter by lazy { NBPAListAdapter(this) }


//    val customerOrders =
//        object : GenericAdapter<ItemAllSchemesBinding, ItemLiveScheme>() {
//            override fun onCreateView(
//                inflater: LayoutInflater,
//                parent: ViewGroup,
//                viewType: Int
//            ) = ItemAllSchemesBinding.inflate(inflater, parent, false)
//
//            override fun onBindHolder(
//                binding: ItemAllSchemesBinding,
//                dataClass: ItemLiveScheme,
//                position: Int
//            ) {
//                binding.apply {
//
//
//
////                    val date =
////                        dataClass.updatedtime.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy")
////                    btDate.text = date
////
//                    textTitle.text = dataClass.name
//                    textDesc.text = dataClass.address
//                    dataClass.foodIdentityImage?.url?.glideImagePortrait(root.context, ivIcon)
////                    textMobile.text = dataClass.customerMobile
////                    when (dataClass.status) {
////                        "pending" -> {
////                            btStatus.text = "Pending"
////                            btStatus.backgroundTintList =
////                                ColorStateList.valueOf(
////                                    ContextCompat.getColor(
////                                        binding.root.context,
////                                        R.color._E87103
////                                    )
////                                )
////                            btStatus.visibility = View.VISIBLE
////                        }
////                        "inprogress" -> {
////                            btStatus.text = "In Progress"
////                            btStatus.backgroundTintList =
////                                ColorStateList.valueOf(
////                                    ContextCompat.getColor(
////                                        binding.root.context,
////                                        R.color._F7879A
////                                    )
////                                )
////                            btStatus.visibility = View.VISIBLE
////                        }
////                        "dispatched" -> {
////                            btStatus.text = "Dispatched"
////                            btStatus.backgroundTintList =
////                                ColorStateList.valueOf(
////                                    ContextCompat.getColor(
////                                        binding.root.context,
////                                        R.color._2eb82e
////                                    )
////                                )
////                            btStatus.visibility = View.VISIBLE
////                        }
////                        "complete" -> {
////                            btStatus.text = "Complete"
////                            btStatus.backgroundTintList =
////                                ColorStateList.valueOf(
////                                    ContextCompat.getColor(
////                                        binding.root.context,
////                                        R.color._2A3740
////                                    )
////                                )
////                            btStatus.visibility = View.VISIBLE
////                        }
////                        else -> {
////                            btStatus.visibility = View.GONE
////                        }
//                }
//
//                binding.apply {
//                    root.singleClick {
//                        root.findNavController()
//                            .navigate(R.id.action_members_to_nbpaDetail, Bundle().apply {
//                                putParcelable("key", dataClass)
//                            })
//                    }
//                }
//
//            }
//        }


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


    private var itemLiveSchemesResult = MutableLiveData<List<ItemLiveScheme>>()
    val itemLiveSchemes : LiveData<List<ItemLiveScheme>> get() = itemLiveSchemesResult

//    internal var itemLiveSchemesResult = MutableLiveData<Boolean>(false)

    fun liveScheme(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.schemeHistoryList(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        val typeToken = object : TypeToken<List<ItemLiveScheme>>() {}.type
                        val changeValue =
                            Gson().fromJson<List<ItemLiveScheme>>(
                                Gson().toJson(response.body()!!.data),
                                typeToken
                            )
                        itemLiveSchemesResult.value = changeValue

//                        mainThread {
//                            if (itemsCustomerOrders.size == 0) {
//                                changeValue.forEach { mapData ->
//                                    Log.e("TAG", "schemeDataInnerFalseA: " + mapData.id)
//                                    itemsCustomerOrders.add(mapData)
//                                }
//                            } else {
//
//
//
//                                changeValue.forEach { mapData ->
//                                    var aaa = getData(mapData)
//                                    Log.e( "TAG",  "aaa: " + mapData.id + " :::: "+ aaa)
//                                    if (aaa == true) {
//                                        itemsCustomerOrders.add(mapData)
//                                    }
//
////                                    getData
//
////                                    itemsCustomerOrders.forEach { schemeDataInner ->
////                                        if (schemeDataInner.id != mapData.id) {
////                                            Log.e( "TAG",  "schemeDataInnerFalseB: " + mapData.id + " :::: "+ schemeDataInner.id)
////                                            itemsCustomerOrders.add(mapData)
////                                        }
////                                    }
//
////                                    itemsCustomerOrders.forEach { schemeDataInner ->
////                                        if (schemeDataInner.id != mapData.id) {
////                                            Log.e(
////                                                "TAG",
////                                                "schemeDataInnerFalse: " + schemeDataInner.id
////                                            )
////                                            itemsCustomerOrders.add(schemeDataInner)
////                                        }
////
////                                        if (schemeDataInner.id == mapData.id) {
////                                            Log.e(
////                                                "TAG",
////                                                "schemeDataInnerTrue: " + schemeDataInner.id
////                                            )
////                                        }
////                                    }
//                                }
//                            }
////                                itemsCustomerOrders.map { mapData ->
////                                    changeValue.map { schemeDataInner ->
////                                        if (schemeDataInner.id != mapData.id) {
////                                            Log.e("TAG", "schemeDataInner: " + schemeDataInner.id)
////                                            itemsCustomerOrders.add(schemeDataInner)
////                                        }
////                                    }
////                                }
//////                            }
//
//                            Log.e("TAG", "onViewCreatedXXX: " + itemsCustomerOrders.size)
//                            itemLiveSchemesResult.value = true
//                        }


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


    private var itemLiveSchemesResultSecond = MutableLiveData<BaseResponseDC<Any>>()
    val itemLiveSchemesSecond: LiveData<BaseResponseDC<Any>> get() = itemLiveSchemesResultSecond
    fun liveSchemeSecond(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.schemeHistoryList(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        itemLiveSchemesResultSecond.value = response.body() as BaseResponseDC<Any>
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


    var applyLink = MutableLiveData<Int>(-1)
    fun applyLink(jsonObject: JSONObject, position: Int) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.applyLink(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        applyLink.value = position
                    } else {
                        applyLink.value = -1
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

//
//    fun viewDetail(oldItemLiveScheme: ItemLiveScheme, position: Int, root: View, status : Int) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.schemeDetail(id = ""+oldItemLiveScheme.scheme_id)
//                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
//                    if (response.isSuccessful){
//                        val data = Gson().fromJson(response.body()!!.data, ItemSchemeDetail::class.java)
//
//                        when(status){
//                            in 1..2 -> {
//                                val dialogBinding = DialogBottomLiveSchemeBinding.inflate(root.context.getSystemService(
//                                    Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                                )
//                                val dialog = BottomSheetDialog(root.context)
//                                dialog.setContentView(dialogBinding.root)
//                                dialog.setOnShowListener { dia ->
//                                    val bottomSheetDialog = dia as BottomSheetDialog
//                                    val bottomSheetInternal: FrameLayout =
//                                        bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
//                                    bottomSheetInternal.setBackgroundResource(R.drawable.bg_top_round_corner)
//                                }
//                                dialog.show()
//
//                                dialogBinding.apply {
//                                    data.scheme_image?.url?.glideImage(root.context, ivMap)
//                                    textTitle.setText(oldItemLiveScheme.name)
//                                    textDesc.setText(oldItemLiveScheme.description)
//
//                                    if (data.status == "Active" && oldItemLiveScheme.user_scheme_status == "applied"){
//                                        btApply.visibility = View.GONE
//                                        textHeaderTxt4.text = root.context.resources.getText(R.string.applied)
//                                        textHeaderTxt4.backgroundTintList = ContextCompat.getColorStateList(root.context,R.color._138808)
//                                    } else if (data.status == "Active"){
//                                        btApply.visibility = View.VISIBLE
//                                        textHeaderTxt4.text = root.context.resources.getText(R.string.active)
//                                        textHeaderTxt4.backgroundTintList = ContextCompat.getColorStateList(root.context,R.color._138808)
//                                    }  else {
//                                        btApply.visibility = View.GONE
//                                        textHeaderTxt4.text = root.context.resources.getText(R.string.expired)
//                                        textHeaderTxt4.backgroundTintList = ContextCompat.getColorStateList(root.context,R.color._F02A2A)
//                                    }
//
//                                    data.start_at?.let {
//                                        textStartDate.text = HtmlCompat.fromHtml("${root.context.resources.getString(R.string.start_date, "<b>"+data.start_at.changeDateFormat("yyyy-MM-dd", "dd MMM, yyyy")+"</b>")}", HtmlCompat.FROM_HTML_MODE_LEGACY)
//                                    }
//
//                                    data.end_at?.let {
//                                        textEndDate.text = HtmlCompat.fromHtml("${root.context.resources.getString(R.string.end_date, "<b>"+data.end_at.changeDateFormat("yyyy-MM-dd", "dd MMM, yyyy")+"</b>")}", HtmlCompat.FROM_HTML_MODE_LEGACY)
//                                    }
//
//
////                                    if (status == 1){
////                                        btApply.setText(view.resources.getString(R.string.view))
////                                        btApply.visibility = View.GONE
////                                    }else{
////                                        btApply.setText(view.resources.getString(R.string.apply))
////                                        btApply.visibility = View.VISIBLE
////                                    }
//
//                                    btApply.singleClick {
//                                        if (status == 1){
//                                            Handler(Looper.getMainLooper()).post(Thread {
//                                                MainActivity.activity.get()?.runOnUiThread {
//                                                    data.apply_link?.let {
//                                                        val webIntent = Intent(
//                                                            Intent.ACTION_VIEW,
//                                                            Uri.parse(data.apply_link)
//                                                        )
//                                                        try {
//                                                            root.context.startActivity(webIntent)
//                                                        } catch (ex: ActivityNotFoundException) {
//                                                        }
//                                                    }
//                                                }
//                                            })
//                                        } else {
//                                            readData(LOGIN_DATA) { loginUser ->
//                                                if (loginUser != null) {
//                                                    val obj: JSONObject = JSONObject().apply {
//                                                        put(scheme_id, data?.scheme_id)
//                                                        put(user_type, USER_TYPE)
//                                                        put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
//                                                    }
//                                                    if(networkFailed) {
//                                                        applyLink(obj, position)
//                                                    } else {
//                                                        root.context.callNetworkDialog()
//                                                    }
//                                                }
//                                            }
//                                        }
//                                        dialog.dismiss()
//                                    }
//
//                                    btClose.singleClick {
//                                        dialog.dismiss()
//                                    }
//                                }
//                            } else -> {
//                            Handler(Looper.getMainLooper()).post(Thread {
//                                MainActivity.activity.get()?.runOnUiThread {
//                                    data.apply_link?.let {
//                                        val webIntent = Intent(
//                                            Intent.ACTION_VIEW, Uri.parse(data.apply_link))
//                                        try {
//                                            root.context.startActivity(webIntent)
//                                        } catch (ex: ActivityNotFoundException) {
//                                        }
//                                    }
//                                }
//                            })
//                        }
//                        }
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
    /**/

    fun getData(mapData: ItemLiveScheme): Boolean {
        itemsCustomerOrders.forEach { schemeDataInner ->
            if (schemeDataInner.id != mapData.id) {
                Log.e("TAG", "schemeDataInnerFalseB: " + mapData.id + " :::: " + schemeDataInner.id)
                return true
            }
        }
        return false
    }


    fun callApiTranslate(_lang: String, _words: String): String {
        return repository.callApiTranslate(_lang, _words)
    }















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


    val itemsProduct: ArrayList<ItemNBPAForm> = ArrayList()


    private var itemProducstResult = MutableLiveData<ItemNBPAFormRoot>()
    val itemProducts: LiveData<ItemNBPAFormRoot> get() = itemProducstResult
    fun getProducts(emptyMap: JSONObject, pageNumber: Int) =
        viewModelScope.launch {
            if (pageNumber == 0 || pageNumber == 1) {
                repository.callApi(
                    callHandler = object : CallHandler<Response<ItemNBPAFormRoot>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.getPopularMoviesListXX(emptyMap.getJsonRequestBody())

                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<ItemNBPAFormRoot>) {
                            if (response.isSuccessful) {
                                mainThread {
                                    try {
                                        Log.e("TAG", "successAA: ${response.body().toString()}")
                                        var mMineUserEntity = response.body()!!
                                        itemProducstResult.value = mMineUserEntity
                                    } catch (e: Exception) {
                                    }
                                }

                            }
                        }

                        override fun error(message: String) {

                        }

                        override fun loading() {
                            super.loading()
                        }
                    }
                )
            } else {
                repository.callApiWithoutLoader (
                    callHandler = object : CallHandler<Response<ItemNBPAFormRoot>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.getPopularMoviesListXX(emptyMap.getJsonRequestBody())

                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<ItemNBPAFormRoot>) {
                            if (response.isSuccessful) {
                                mainThread {
                                    try {
                                        Log.e("TAG", "successAA: ${response.body().toString()}")
                                        var mMineUserEntity = response.body()!!
                                        itemProducstResult.value = mMineUserEntity
                                    } catch (e: Exception) {
                                    }
                                }

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






}