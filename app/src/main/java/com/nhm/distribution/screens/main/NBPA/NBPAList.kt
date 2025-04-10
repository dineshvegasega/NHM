package com.nhm.distribution.screens.main.NBPA

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhm.distribution.R
import com.nhm.distribution.databinding.AllSchemesBinding
import com.nhm.distribution.databinding.NbpaListBinding
import com.nhm.distribution.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distribution.datastore.DataStoreUtil.readData
import com.nhm.distribution.models.ItemLiveScheme
import com.nhm.distribution.models.Login
import com.nhm.distribution.networking.IS_LANGUAGE
import com.nhm.distribution.networking.USER_TYPE
import com.nhm.distribution.networking.page
import com.nhm.distribution.networking.search_input
import com.nhm.distribution.networking.userId
import com.nhm.distribution.networking.user_id
import com.nhm.distribution.networking.user_type
import com.nhm.distribution.screens.main.NBPA.NBPADetail.Companion.change
import com.nhm.distribution.screens.main.schemes.allSchemes.AllSchemesVM
import com.nhm.distribution.screens.mainActivity.MainActivity
import com.nhm.distribution.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.locale
import com.nhm.distribution.utils.PaginationScrollListener
import com.nhm.distribution.utils.callNetworkDialog
import com.nhm.distribution.utils.ioThread
import com.nhm.distribution.utils.isLastItemDisplaying
import com.nhm.distribution.utils.isNetworkAvailable
import com.nhm.distribution.utils.mainThread
import com.nhm.distribution.utils.onRightDrawableClicked
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.internal.StringUtil.isNumeric
import kotlin.collections.addAll
import kotlin.getValue

@AndroidEntryPoint
class NBPAList : Fragment() {
    private val viewModel: NBPAViewModel by viewModels()
    private var _binding: NbpaListBinding? = null
    private val binding get() = _binding!!


    private var LOADER_TIME: Long = 500
    private var pageStart: Int = 1
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 1
//    private var currentPage: Int = pageStart


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NbpaListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.all_schemes)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)

//            loadFirstPage()
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = viewModel.customerOrders
            recyclerView.itemAnimator = DefaultItemAnimator()

//            observerDataRequest()
//
//            recyclerViewScroll()
//
//            searchHandler()

//            change = true

            loadFirstPage()


            var isLoad = true

            viewModel.itemLiveSchemes.observe(viewLifecycleOwner) {
                if (it.size != 0) {
                    viewModel.itemsCustomerOrders.addAll(it)
                    isLoad = true
                } else {
                    isLoad = false
                }

                idPBLoading.visibility = View.GONE

                Log.e("TAG", "onViewCreatedXXX: "+viewModel.itemsCustomerOrders.size)

                viewModel.customerOrders.submitList(viewModel.itemsCustomerOrders)
                viewModel.customerOrders.notifyDataSetChanged()

                if (viewModel.itemsCustomerOrders.size == 0) {
                    binding.idDataNotFound.root.visibility = View.VISIBLE
                } else {
                    binding.idDataNotFound.root.visibility = View.GONE
                }
            }


            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (recyclerView.isLastItemDisplaying()) {
                        if (isLoad) {
                            Log.e("TAG", "addOnScrollListener " + dx + "   " + dy)
                            pageStart++
//                            loadFirstPage()
                            idPBLoading.visibility = View.VISIBLE
                            val isNumeric = isNumeric(inclideHeaderSearch.editTextSearch.text.toString())
                            if(isNumeric == true){
//                                loadData(""+editTextSearch.text.toString() , "")
                                loadFirstPage()
                            } else {
//                                loadData("" , ""+editTextSearch.text.toString())
                                loadFirstPage()
                            }
                        }
                    }
                }
            })

        }
    }


//    private fun searchHandler() {
//        binding.apply {
//            inclideHeaderSearch.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    loadFirstPage()
//                }
//                true
//            }
//
//            inclideHeaderSearch.editTextSearch.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                }
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                }
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    inclideHeaderSearch.editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, if(count >= 1) R.drawable.ic_cross_white else R.drawable.ic_search, 0);
//                }
//            })
//
//            inclideHeaderSearch.editTextSearch.onRightDrawableClicked {
//                it.text.clear()
//                loadFirstPage()
//            }
//        }
//    }


//    private fun recyclerViewScroll() {
//        binding.apply {
//            recyclerView.addOnScrollListener(object : PaginationScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
//                override fun loadMoreItems() {
//                    isLoading = true
//                    currentPage += 1
//                    if(totalPages >= currentPage){
//                        Handler(Looper.myLooper()!!).postDelayed({
//                            loadNextPage()
//                        }, LOADER_TIME)
//                    }
//                }
//                override fun getTotalPageCount(): Int {
//                    return totalPages
//                }
//                override fun isLastPage(): Boolean {
//                    return isLastPage
//                }
//                override fun isLoading(): Boolean {
//                    return isLoading
//                }
//            })
//        }
//    }


    private fun loadFirstPage() {
//        pageStart  = 1
//        isLoading = false
//        isLastPage = false
//        totalPages  = 1
//        currentPage  = pageStart
//        viewModel.results.clear()


        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val obj: JSONObject = JSONObject().apply {
                    put(page, pageStart)
                    put(user_type, USER_TYPE)
                    put(search_input, binding.inclideHeaderSearch.editTextSearch.text.toString())
                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
                }
                if (requireContext().isNetworkAvailable()) {
                    viewModel.liveScheme(obj)
                    binding.idNetworkNotFound.root.visibility = View.GONE
                } else {
//                        requireContext().callNetworkDialog()
                    binding.idNetworkNotFound.root.visibility = View.VISIBLE
                }
            }
        }
    }
//
//    fun loadNextPage() {
////        Log.e("TAG", "loadNextPage "+currentPage)
//        readData(LOGIN_DATA) { loginUser ->
//            if (loginUser != null) {
//                val obj: JSONObject = JSONObject().apply {
//                    put(page, currentPage)
//                    put(user_type, USER_TYPE)
//                    put(search_input, binding.inclideHeaderSearch.editTextSearch.text.toString())
//                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
//                }
//                if(requireContext().isNetworkAvailable()) {
//                    viewModel.liveSchemeSecond(obj)
//                } else {
//                    requireContext().callNetworkDialog()
//                }
//            }
//        }
//    }
//
//
//
//    @SuppressLint("NotifyDataSetChanged")
//    private fun observerDataRequest(){
//        viewModel.itemLiveSchemes.observe(viewLifecycleOwner, Observer {
//            viewModel.show()
//            val typeToken = object : TypeToken<List<ItemLiveScheme>>() {}.type
//            val changeValue =
//                Gson().fromJson<List<ItemLiveScheme>>(Gson().toJson(it.data), typeToken)
//            if (IS_LANGUAGE){
//                if (MainActivity.context.get()!!
//                        .getString(R.string.englishVal) == "" + locale
//                ) {
//                    val itemStateTemp = changeValue
//                    viewModel.results.addAll(itemStateTemp)
//                    viewModel.adapter.addAllSearch(viewModel.results)
//                    viewModel.hide()
//
//                    if (viewModel.adapter.itemCount > 0) {
//                        binding.idDataNotFound.root.visibility = View.GONE
//                    } else {
//                        binding.idDataNotFound.root.visibility = View.VISIBLE
//                    }
//                } else {
//                    val itemStateTemp = changeValue
//                    mainThread {
//                        itemStateTemp.forEach {
//                            delay(50)
//                            val nameChanged: String = if(it.name != null) viewModel.callApiTranslate(""+locale, it.name) else ""
//                            val descChanged: String = if(it.address != null) viewModel.callApiTranslate(""+locale, it.address) else ""
//
//                            apply {
//                                it.name = nameChanged
//                                it.address = descChanged
//                            }
//                        }
//
//                        viewModel.results.addAll(itemStateTemp)
//                        viewModel.adapter.addAllSearch(viewModel.results)
//                        viewModel.hide()
//
//                        if (viewModel.adapter.itemCount > 0) {
//                            binding.idDataNotFound.root.visibility = View.GONE
//                        } else {
//                            binding.idDataNotFound.root.visibility = View.VISIBLE
//                        }
//                    }
//                }
//            } else {
//                val itemStateTemp = changeValue
//                viewModel.results.addAll(itemStateTemp)
//                viewModel.adapter.addAllSearch(viewModel.results)
//                viewModel.hide()
//
//                if (viewModel.adapter.itemCount > 0) {
//                    binding.idDataNotFound.root.visibility = View.GONE
//                } else {
//                    binding.idDataNotFound.root.visibility = View.VISIBLE
//                }
//            }
//
//
//            totalPages = it.meta?.total_pages!!
//            if (currentPage == totalPages) {
//                viewModel.adapter.removeLoadingFooter()
//            } else if (currentPage <= totalPages) {
//                viewModel.adapter.addLoadingFooter()
//                isLastPage = false
//            } else {
//                isLastPage = true
//            }
//        })
//
//
//        viewModel.itemLiveSchemesSecond.observe(viewLifecycleOwner, Observer {
//            viewModel.show()
//            val typeToken = object : TypeToken<List<ItemLiveScheme>>() {}.type
//            val changeValue =
//                Gson().fromJson<List<ItemLiveScheme>>(Gson().toJson(it.data), typeToken)
//            if (IS_LANGUAGE){
//                if (MainActivity.context.get()!!
//                        .getString(R.string.englishVal) == "" + locale
//                ) {
//                    val itemStateTemp = changeValue
//                    viewModel.results.addAll(itemStateTemp)
//                    viewModel.adapter.addAllSearch(viewModel.results)
//                    viewModel.hide()
//                } else {
//                    val itemStateTemp = changeValue
//                    mainThread {
//                        itemStateTemp.forEach {
//                            delay(50)
//                            val nameChanged: String = if(it.name != null) viewModel.callApiTranslate(""+locale, it.name) else ""
//                            val descChanged: String = if(it.address != null) viewModel.callApiTranslate(""+locale, it.address) else ""
//
//                            apply {
//                                it.name = nameChanged
//                                it.address = descChanged
//                            }
//                        }
//
//                        viewModel.results.addAll(itemStateTemp)
//                        viewModel.adapter.addAllSearch(viewModel.results)
//                        viewModel.hide()
//                    }
//                }
//            } else {
//                val itemStateTemp = changeValue
//                viewModel.results.addAll(itemStateTemp)
//                viewModel.adapter.addAllSearch(viewModel.results)
//                viewModel.hide()
//            }
//
//
//            viewModel.adapter.removeLoadingFooter()
//            isLoading = false
//            viewModel.adapter.addAllSearch(viewModel.results)
//            if (currentPage != totalPages) viewModel.adapter.addLoadingFooter()
//            else isLastPage = true
//        })
//
//
//        viewModel.applyLink.observe(viewLifecycleOwner, Observer { position ->
////            if (position != -1){
////                val data = results.get(position).apply {
////                    user_scheme_status = "applied"
////                }
////                viewModel.adapter.notifyDataSetChanged()
////                if(networkFailed) {
////                    viewModel.viewDetail(data, position = position, requireView(), 3)
////                } else {
////                    requireContext().callNetworkDialog()
////                }
////            }
//        })
//
//    }


}