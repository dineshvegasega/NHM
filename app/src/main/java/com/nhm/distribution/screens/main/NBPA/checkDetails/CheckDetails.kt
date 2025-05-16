package com.nhm.distribution.screens.main.NBPA.checkDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nhm.distribution.R
import com.nhm.distribution.databinding.CheckDetailsBinding
import com.nhm.distribution.networking.USER_TYPE
import com.nhm.distribution.networking.filterByAadhaar
import com.nhm.distribution.networking.filterByEndDate
import com.nhm.distribution.networking.filterByMobile
import com.nhm.distribution.networking.filterByName
import com.nhm.distribution.networking.filterByStartDate
import com.nhm.distribution.networking.user_id
import com.nhm.distribution.networking.user_type
import com.nhm.distribution.screens.main.NBPA.NBPAViewModel
import com.nhm.distribution.screens.mainActivity.MainActivity
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.nhm.distribution.screens.mainActivity.MainActivityVM.Companion.userIdForGlobal
import com.nhm.distribution.utils.showSnackBar
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class CheckDetails  : Fragment() {
    private lateinit var viewModel: CheckDetailsVM
    private var _binding: CheckDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CheckDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(CheckDetailsVM::class.java)
        MainActivity.Companion.mainActivity.get()?.callFragment(4)
        binding.apply {
            btSignIn.singleClick {
                if (editTextAadhaarNumber.text.toString()
                        .isEmpty() || editTextAadhaarNumber.text.toString().length != 12
                ) {
                    showSnackBar(getString(R.string.enter_valid_aadhaar_number))
                } else {
                    var obj: JSONObject = JSONObject()
                    obj = JSONObject().apply {
//                        put(com.nhm.distribution.networking.page,"1")
//                        put(user_type, USER_TYPE)
//                        put(user_id, userIdForGlobal)
                        put(filterByAadhaar, editTextAadhaarNumber.text.toString())
                    }
                    viewModel.checkAadhaarNo(obj){
                        var itemProducts = this.data
                        Log.e("TAG", "checkAadhaarNo: "+this.toString())
                        if (itemProducts.size == 0){
                            findNavController().navigate(R.id.action_checkDetails_to_nbpa, Bundle().apply {
                                putString("isExist", "no")
                                putString("aadhaarNumber", ""+editTextAadhaarNumber.text.toString())
                            })
                        } else {
                            if(userIdForGlobal == ""+itemProducts[0].user_id){
                                findNavController().navigate(R.id.action_checkDetails_to_nbpa, Bundle().apply {
                                    putString("isExist", "yes")
                                    putString("_id", ""+itemProducts[0].id)
                                })
                            } else {
                                showSnackBar(view.resources.getString(R.string.already_exists_aadhaar))
                            }
                        }
                    }
                }
            }
        }
    }



//    override fun onDestroyView() {
//        super.onDestroyView()
//        MainActivity.mainActivity.get()?.callFragment(4)
//        isProductLoad = false
//        isProductLoadMember = false
//    }
}