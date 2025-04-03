package com.nhm.distribution.screens.onboarding.quickRegistration

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.nhm.distribution.R
import com.nhm.distribution.databinding.QuickRegistrationBinding
import com.nhm.distribution.screens.interfaces.CallBackListener
import com.nhm.distribution.screens.mainActivity.MainActivity
import com.nhm.distribution.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distribution.networking.*
import com.nhm.distribution.utils.OtpTimer
import com.nhm.distribution.utils.callNetworkDialog
import com.nhm.distribution.utils.isValidPassword
import com.nhm.distribution.utils.loadHtml
import com.nhm.distribution.utils.mainThread
import com.nhm.distribution.utils.showDropDownDialog
import com.nhm.distribution.utils.showSnackBar
import com.nhm.distribution.utils.showSnackBarWithoutKeypad
import com.nhm.distribution.utils.singleClick
import com.nhm.distribution.utils.updatePagerHeightForChild
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import kotlin.text.get

@AndroidEntryPoint
class QuickRegistration : Fragment(), CallBackListener{
    private var _binding: QuickRegistrationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuickRegistrationVM by activityViewModels()
    var tabPosition: Int = 0;

    companion object{
        var callBackListener: CallBackListener? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = QuickRegistrationBinding.inflate(inflater)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)

        MainActivity.mainActivity.get()?.callFragment(0)
        callBackListener = this
        binding.apply {
//            val adapter= QuickRegistrationAdapter(requireActivity())
//            adapter.notifyDataSetChanged()
//            introViewPager.adapter=adapter
//            introViewPager.setUserInputEnabled(false)

            btSignIn.setEnabled(false)


            btDistributor.singleClick {
                viewModel.loginType = 1
                setSearchButtons()
//                adapter.notifyDataSetChanged()
//                introViewPager.adapter=adapter
                editTextGender.visibility = View.GONE
                checkValidation()
            }

            btController.singleClick {
                viewModel.loginType = 2
                setSearchButtons()
//                adapter.notifyDataSetChanged()
//                introViewPager.adapter=adapter
                editTextGender.visibility = View.VISIBLE
                checkValidation()
            }


            if (viewModel.loginType == 1) {
                editTextGender.visibility = View.GONE
            } else if (viewModel.loginType == 2) {
                editTextGender.visibility = View.VISIBLE
            }

            editTextGender.singleClick {
                requireActivity().showDropDownDialog(type = 1) {
                    binding.editTextGender.setText(name)
                    when (position) {
                        0 -> viewModel.data.gender = "Male"
                        1 -> viewModel.data.gender = "Female"
                        2 -> viewModel.data.gender = "Other"
                    }
                }
            }

//            viewModel.isAgree.observe(viewLifecycleOwner, Observer {
//                if (it == true){
//                    btSignIn.setEnabled(true)
//                    btSignIn.setBackgroundTintList(
//                        ColorStateList.valueOf(
//                            ResourcesCompat.getColor(
//                                getResources(), R.color._E79D46, null)))
//                } else {
//                    btSignIn.setEnabled(false)
//                    btSignIn.setBackgroundTintList(
//                        ColorStateList.valueOf(
//                            ResourcesCompat.getColor(
//                                getResources(), R.color._999999, null)))
//                }
//            })
//
//            viewModel.isSendMutable.value = false
//            viewModel.isSendMutable.observe(viewLifecycleOwner, Observer {
//                if (it == true){
//                    btSignIn.setEnabled(true)
//                    btSignIn.setBackgroundTintList(
//                        ColorStateList.valueOf(
//                            ResourcesCompat.getColor(
//                                getResources(), R.color._E79D46, null)))
//                }
//            })



            var counter = 0
            var start: Int
            var end: Int
            imgCreatePassword.singleClick {
                if(counter == 0){
                    counter = 1
                    imgCreatePassword.setImageResource(R.drawable.ic_eye_open)
                    start=editTextCreatePassword.getSelectionStart()
                    end=editTextCreatePassword.getSelectionEnd()
                    editTextCreatePassword.setTransformationMethod(null)
                    editTextCreatePassword.setSelection(start,end)
                }else{
                    counter = 0
                    imgCreatePassword.setImageResource(R.drawable.ic_eye_closed)
                    start=editTextCreatePassword.getSelectionStart()
                    end=editTextCreatePassword.getSelectionEnd()
                    editTextCreatePassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextCreatePassword.setSelection(start,end)
                }
            }


            var counter2 = 0
            var start2: Int
            var end2: Int
            imgReEnterPassword.singleClick {
                if(counter2 == 0){
                    counter2 = 1
                    imgReEnterPassword.setImageResource(R.drawable.ic_eye_open)
                    start2=editTextReEnterPassword.getSelectionStart()
                    end2=editTextReEnterPassword.getSelectionEnd()
                    editTextReEnterPassword.setTransformationMethod(null)
                    editTextReEnterPassword.setSelection(start2,end2)
                }else{
                    counter2 = 0
                    imgReEnterPassword.setImageResource(R.drawable.ic_eye_closed)
                    start2=editTextReEnterPassword.getSelectionStart()
                    end2=editTextReEnterPassword.getSelectionEnd()
                    editTextReEnterPassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextReEnterPassword.setSelection(start2,end2)
                }
            }


            cbRememberMe.singleClick {
                checkValidation()
            }

            textTerms.singleClick {
                viewModel.show()
                mainThread {
                    openDialog(3)
                }
            }



            editTextCreatePassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(!editTextCreatePassword.text.toString().isEmpty()){
                        if(editTextCreatePassword.text.toString().length >= 0 && editTextCreatePassword.text.toString().length < 8){
                            textCreatePasswrordMsg.setText(R.string.InvalidPassword)
                            textCreatePasswrordMsg.visibility = View.VISIBLE
                        } else if(!isValidPassword(editTextCreatePassword.text.toString().trim())){
                            textCreatePasswrordMsg.setText(R.string.InvalidPassword)
                            textCreatePasswrordMsg.visibility = View.VISIBLE
                        } else {
                            textCreatePasswrordMsg.visibility = View.GONE
                        }
                    }
                    editTextCreatePassword.requestFocus()
                }
            })


            editTextReEnterPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                @SuppressLint("SuspiciousIndentation")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(!editTextCreatePassword.text.toString().isEmpty()){
                        if(!editTextReEnterPassword.text.toString().isEmpty()){
                            if (editTextCreatePassword.text.toString() != editTextReEnterPassword.text.toString()){
                                textReEnterPasswrordMsg.setText(R.string.CreatePasswordReEnterPasswordisnotsame)
                                textReEnterPasswrordMsg.visibility = View.VISIBLE
                                checkValidation()
                            } else {
                                textReEnterPasswrordMsg.visibility = View.GONE
                                checkValidation()
                            }
                        }
                    }
                    editTextReEnterPassword.requestFocus()
                }
            })



            btSignIn.singleClick {
//                if (tabPosition == 0){
//                    QuickRegistration1.callBackListener!!.onCallBack(1)
//                } else if (tabPosition == 1){
//                    QuickRegistration2.callBackListener!!.onCallBack(3)
//                }
//                loadProgress(tabPosition)


                checkValidationClick()

            }

            textBack.singleClick {
//                if (tabPosition == 0){
//                    view.findNavController().navigateUp()
//                } else if (tabPosition == 1){
//                    btSignIn.setEnabled(true)
//                    btSignIn.setBackgroundTintList(
//                        ColorStateList.valueOf(
//                            ResourcesCompat.getColor(
//                                getResources(), R.color._E79D46, null)))
//                    introViewPager.setCurrentItem(0, false)
//                    btSignIn.setText(getString(R.string.continues))
//                }
//                loadProgress(tabPosition)
                view.findNavController().navigateUp()
            }



//            introViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                override fun onPageScrolled(
//                    position: Int,
//                    positionOffset: Float,
//                    positionOffsetPixels: Int
//                ) {
//                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//                }
//
//                override fun onPageSelected(position: Int) {
//                    introViewPager.requestLayout()
//                    super.onPageSelected(position)
//                    tabPosition = position
//                    if(position == 1) {
//                        btSignIn.setEnabled(false)
//                        btSignIn.setBackgroundTintList(
//                            ColorStateList.valueOf(
//                                ResourcesCompat.getColor(
//                                    getResources(), R.color._999999, null)))
//                    }
//                }
//
//                override fun onPageScrollStateChanged(state: Int) {
//                    super.onPageScrollStateChanged(state)
//                }
//            })
//
//
//            introViewPager.setPageTransformer { page, position ->
//                introViewPager.updatePagerHeightForChild(page)
//            }
        }
    }



    private fun loadProgress(tabPosition: Int) {
        val forOne = 100 / 2
        val myPro = tabPosition + 1
        val myProTotal = forOne * myPro
        binding.loading.progress = myProTotal
    }

    override fun onCallBack(pos: Int) {
        binding.apply {
            if (pos == 21){
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (pos == 2){
                introViewPager.setCurrentItem(1, false)
                btSignIn.setText(getString(R.string.RegisterNow))
            } else if (pos == 4){
                val obj: JSONObject = JSONObject().apply {
                    put(vendor_first_name, viewModel.data.vendor_first_name)
                    put(vendor_last_name, viewModel.data.vendor_last_name)
                    put(mobile_no, viewModel.data.mobile_no)
                    put(password, viewModel.data.password)
                    put(user_type, USER_TYPE)
                }
//                if(networkFailed) {
//                    viewModel.register(view = requireView(), obj)
//                } else {
//                    requireContext().callNetworkDialog()
//                }
            }
        }
        loadProgress(tabPosition)
    }




    override fun onDestroyView() {
        viewModel.isAgree.value = false
        OtpTimer.sendOtpTimerData = null
        OtpTimer.stopTimer()
        _binding = null
        super.onDestroyView()
    }



    private fun setSearchButtons() {
        if (viewModel.loginType == 1) {
            binding.btDistributor.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.white
                )
            )
            binding.btController.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.black
                )
            )
            binding.btDistributor.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
            binding.btController.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btDistributor.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
            binding.btController.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
        } else {
            binding.btDistributor.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.black
                )
            )
            binding.btController.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.white
                )
            )
            binding.btDistributor.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btController.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
            binding.btDistributor.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
            binding.btController.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
        }

    }



    fun checkValidationClick() {
        binding.apply {
            if(editTextFN.text.toString().isEmpty()){
                showSnackBarWithoutKeypad(getString(R.string.first_name))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (editTextLN.text.toString().isEmpty()){
                showSnackBarWithoutKeypad(getString(R.string.last_name))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (editTextMobileNumber.text.toString().isEmpty() || editTextMobileNumber.text.toString().length != 10){
                showSnackBarWithoutKeypad(getString(R.string.mobileNumber))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (viewModel.loginType == 2 && editTextGender.text.toString().isEmpty()) {
                showSnackBarWithoutKeypad(getString(R.string.gender))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (editTextAadhaarNumber.text.toString().isEmpty() || editTextAadhaarNumber.text.toString().length != 12){
                showSnackBarWithoutKeypad(getString(R.string.aadhaar_number))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (editTextAddress.text.toString().isEmpty()){
                showSnackBarWithoutKeypad(getString(R.string.address))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if(editTextCreatePassword.text.toString().isEmpty()){
                showSnackBarWithoutKeypad(getString(R.string.createPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if(editTextCreatePassword.text.toString().length >= 0 && editTextCreatePassword.text.toString().length < 8){
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if(!isValidPassword(editTextCreatePassword.text.toString().trim())){
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (editTextReEnterPassword.text.toString().isEmpty()){
                showSnackBarWithoutKeypad(getString(R.string.reEnterPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if(editTextReEnterPassword.text.toString().length >= 0 && editTextReEnterPassword.text.toString().length < 8){
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if(!isValidPassword(editTextReEnterPassword.text.toString().trim())){
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (editTextCreatePassword.text.toString() != editTextReEnterPassword.text.toString()){
                showSnackBarWithoutKeypad(getString(R.string.CreatePasswordReEnterPasswordisnotsame))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (cbRememberMe.isChecked == false){
                showSnackBarWithoutKeypad(getString(R.string.Pleaseselectagree))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
//            } else if (cbRememberMe.isChecked == true){
//                showSnackBarWithoutKeypad(getString(R.string.Pleaseselectagree))
//                btSignIn.setEnabled(true)
//                btSignIn.setBackgroundTintList(
//                    ColorStateList.valueOf(
//                        ResourcesCompat.getColor(
//                            getResources(), R.color._E79D46, null)))
            } else {
                viewModel.data.vendor_first_name = editTextFN.text.toString()
                viewModel.data.vendor_last_name = editTextLN.text.toString()
                viewModel.data.mobile_no = editTextMobileNumber.text.toString()
                viewModel.data.aadhar = editTextAadhaarNumber.text.toString()
                if(viewModel.loginType == 1){
                    viewModel.data.gender = ""
                } else if(viewModel.loginType == 2){
                    viewModel.data.gender = editTextGender.text.toString()
                }
                viewModel.data.address = editTextAddress.text.toString()
                viewModel.data.password = editTextCreatePassword.text.toString()


//                btSignIn.setEnabled(true)
//                btSignIn.setBackgroundTintList(
//                    ColorStateList.valueOf(
//                        ResourcesCompat.getColor(
//                            getResources(), R.color._E79D46, null)))

                val obj: JSONObject = JSONObject().apply {
                    put(vendor_first_name, viewModel.data.vendor_first_name)
                    put(vendor_last_name, viewModel.data.vendor_last_name)
                    put(mobile_no, viewModel.data.mobile_no)
                    put(availed_scheme, viewModel.data.aadhar)
                    put(gender, viewModel.data.gender)
                    put(residential_address, viewModel.data.address)
                    put(password, viewModel.data.password)
                    if(viewModel.loginType == 1){
                        put(user_type, USER_TYPE)
                        put(user_role, USER_TYPE)
                    }
                    if(viewModel.loginType == 2) {
                        put(user_type, USER_TYPE_ADMIN)
                        put(user_role, USER_TYPE_ADMIN)
                    }
                }
                if(networkFailed) {
                    viewModel.register(view = requireView(), obj)
                } else {
                    requireContext().callNetworkDialog()
                }

            }
        }
    }



    fun checkValidation() {
        binding.apply {
            if(editTextFN.text.toString().isEmpty()){
                showSnackBarWithoutKeypad(getString(R.string.first_name))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (editTextLN.text.toString().isEmpty()){
                showSnackBarWithoutKeypad(getString(R.string.last_name))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (editTextMobileNumber.text.toString().isEmpty() || editTextMobileNumber.text.toString().length != 10){
                showSnackBarWithoutKeypad(getString(R.string.mobileNumber))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (viewModel.loginType == 2 && editTextGender.text.toString().isEmpty()) {
                showSnackBarWithoutKeypad(getString(R.string.gender))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (editTextAadhaarNumber.text.toString().isEmpty() || editTextAadhaarNumber.text.toString().length != 12){
                showSnackBarWithoutKeypad(getString(R.string.aadhaar_number))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if (editTextAddress.text.toString().isEmpty()){
                showSnackBarWithoutKeypad(getString(R.string.address))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            } else if(editTextCreatePassword.text.toString().isEmpty()){
                showSnackBarWithoutKeypad(getString(R.string.createPassword))
                btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
            } else if(editTextCreatePassword.text.toString().length >= 0 && editTextCreatePassword.text.toString().length < 8){
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
            } else if(!isValidPassword(editTextCreatePassword.text.toString().trim())){
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
            } else if (editTextReEnterPassword.text.toString().isEmpty()){
                showSnackBarWithoutKeypad(getString(R.string.reEnterPassword))
                btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
            } else if(editTextReEnterPassword.text.toString().length >= 0 && editTextReEnterPassword.text.toString().length < 8){
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
            } else if(!isValidPassword(editTextReEnterPassword.text.toString().trim())){
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
            } else if (editTextCreatePassword.text.toString() != editTextReEnterPassword.text.toString()){
                showSnackBarWithoutKeypad(getString(R.string.CreatePasswordReEnterPasswordisnotsame))
                btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
            } else if (cbRememberMe.isChecked == false){
                showSnackBarWithoutKeypad(getString(R.string.Pleaseselectagree))
                btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
            } else if (cbRememberMe.isChecked == true){
                btSignIn.setEnabled(true)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._E79D46, null)))
            } else {
                viewModel.data.vendor_first_name = editTextFN.text.toString()
                viewModel.data.vendor_last_name = editTextLN.text.toString()
                viewModel.data.mobile_no = editTextMobileNumber.text.toString()
                viewModel.data.aadhar = editTextAadhaarNumber.text.toString()
                if(viewModel.loginType == 1){
                    viewModel.data.gender = ""
                } else if(viewModel.loginType == 2){
                    viewModel.data.gender = editTextGender.text.toString()
                }
                viewModel.data.address = editTextAddress.text.toString()
                viewModel.data.password = editTextCreatePassword.text.toString()


                btSignIn.setEnabled(true)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._E79D46, null)))

            }
        }
    }


    private fun openDialog(type: Int) {
        val mybuilder = Dialog(requireActivity())
        mybuilder.setContentView(R.layout.dialog_load_html)
        mybuilder.show()
        val window = mybuilder.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(R.color._00000000)
        val yes = mybuilder.findViewById<AppCompatImageView>(R.id.imageCross)
        val title = mybuilder.findViewById<AppCompatTextView>(R.id.textTitleMain)
        val text = mybuilder.findViewById<AppCompatTextView>(R.id.textTitleText)
        when (type) {
            1 -> title.text = resources.getString(R.string.about_us)
            2 -> title.text = resources.getString(R.string.privacy_policy)
            3 -> title.text = resources.getString(R.string.terms_amp_conditions)
        }
        requireContext().loadHtml(type) {
            text.text = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        yes?.singleClick {
            mybuilder.dismiss()
        }
        viewModel.hide()
    }

}