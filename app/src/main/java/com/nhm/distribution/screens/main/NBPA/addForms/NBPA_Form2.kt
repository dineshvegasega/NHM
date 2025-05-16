package com.nhm.distribution.screens.main.NBPA.addForms

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.nhm.distribution.R
import com.nhm.distribution.databinding.Form2Binding
import com.nhm.distribution.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distribution.datastore.DataStoreUtil.readData
import com.nhm.distribution.models.Login
import com.nhm.distribution.networking.*
import com.nhm.distribution.screens.interfaces.CallBackListener
import com.nhm.distribution.screens.main.NBPA.NBPAViewModel
import com.nhm.distribution.screens.main.NBPA.addForms.NBPA_Form1.Companion.formFill1
import com.nhm.distribution.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distribution.utils.callNetworkDialog
import com.nhm.distribution.utils.getNotNullData
import com.nhm.distribution.utils.showDropDownDialog
import com.nhm.distribution.utils.showSnackBar
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class NBPA_Form2 : Fragment() , CallBackListener {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form2Binding? = null
    private val binding get() = _binding!!

    companion object {
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Form2Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
       callBackListener = this
        binding.apply {
            radioGroupPulmonaryRadioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                    when (checkedId) {
                        radioButtonPulmonary.id ->  viewModel.typeOfPatient = 1
                        radioButtonExtraPulmonary.id ->  viewModel.typeOfPatient = 2
                        radioButtonOther.id ->  viewModel.typeOfPatient = 3
                    }
                }
            })

            ivMenu.singleClick {
                NBPA.callBackListener!!.onCallBack(1000)
            }

            editTextPatientCheckupDate.singleClick {
                requireActivity().showDropDownDialog(type = 20){
                    editTextPatientCheckupDate.setText(title)
                    viewModel.patientCheckupDate = title
                }
            }
            editTextHemoglobinCheckupDate.singleClick {
                requireActivity().showDropDownDialog(type = 20){
                    editTextHemoglobinCheckupDate.setText(title)
                    viewModel.hemoglobinCheckupDate = title
                }
            }
            editTextTreatmentEndDate.singleClick {
                requireActivity().showDropDownDialog(type = 20){
                    editTextTreatmentEndDate.setText(title)
                    viewModel.treatmentSupporterEndDate = title
                }
            }


            if (viewModel.start == "no") {
                btSignIn.visibility = View.VISIBLE
                editTextAadhaarNumber.setText(""+viewModel.aadhaarNumber)
                editTextAadhaarNumber.isEnabled = false
            } else {
                var model = viewModel.editDataNew!!.data

                    if (model.typeOfPatient == "1") {
                        radioButtonPulmonary.isChecked = true
                    } else if (model.typeOfPatient == "2") {
                        radioButtonExtraPulmonary.isChecked = true
                    } else {
                        radioButtonOther.isChecked = true
                    }
                    for (i in 0..<radioGroupPulmonaryRadioGroup.getChildCount()) {
                        radioGroupPulmonaryRadioGroup.getChildAt(i).setEnabled(false)
                    }

                    editTextPatientCheckupDate.setText(""+model.patientCheckupDate)
//                editTextHemoglobinLevelAge.setText(""+model.hemoglobinLevelAge)
//                editTextHemoglobinCheckupDate.setText(""+model.hemoglobinCheckupDate)
//                    if (model.hemoglobinLevelAge.isNullOrEmpty()){
//                        editTextHemoglobinLevelAge.setText("")
//                    }else{
//                        editTextHemoglobinLevelAge.setText(""+model.hemoglobinLevelAge)
//                    }
//
//                    if (model.hemoglobinCheckupDate.isNullOrEmpty()){
//                        editTextHemoglobinCheckupDate.setText("")
//                    }else{
//                        editTextHemoglobinCheckupDate.setText(""+model.hemoglobinCheckupDate)
//                    }
//
//                    if (model.muktiID.isNullOrEmpty()){
//                        editTextMuktiID.setText("")
//                    }else{
//                        editTextMuktiID.setText(""+model.muktiID)
//                    }


//                    if (model.aadhaarNumber.isNullOrEmpty()){
//                        editTextAadhaarNumber.setText("")
//                    }else{
//                        editTextAadhaarNumber.setText(""+model.aadhaarNumber)
//                    }


                editTextHemoglobinLevelAge.setText(model.hemoglobinLevelAge.getNotNullData())
                editTextHemoglobinCheckupDate.setText(model.hemoglobinCheckupDate.getNotNullData())
                editTextAadhaarNumber.setText(model.aadhaarNumber.getNotNullData())


//                editTextMuktiID.setText(""+model.muktiID)
                    editTextNakshayID.setText(""+model.nakshayID)
//                editTextAadhaarNumber.setText(""+model.aadhaarNumber)
                    editTextBusiness.setText(""+model.business)
//                editTextBankAccount.setText(""+model.bankAccount)
//                editTextBankIFSC.setText(""+model.bankIFSC)
//                editTextTreatmentSupporterName.setText(""+model.treatmentSupporterName)
//                editTextTreatmentSupporterPost.setText(""+model.treatmentSupporterPost)
//                editTextTreatmentSupporterMobileNumber.setText(""+model.treatmentSupporterMobileNumber)
//                editTextTreatmentEndDate.setText(""+model.treatmentSupporterEndDate)
//                editTextTreatmentResult.setText(""+model.treatmentSupporterResult)


//                    if (model.bankAccount.isNullOrEmpty()){
//                        editTextBankAccount.setText("")
//                    }else{
//                        editTextBankAccount.setText(""+model.bankAccount)
//                    }
//
//                    if (model.bankIFSC.isNullOrEmpty()){
//                        editTextBankIFSC.setText("")
//                    }else{
//                        editTextBankIFSC.setText(""+model.bankIFSC)
//                    }
//
//                    if (model.treatmentSupporterName.isNullOrEmpty()){
//                        editTextTreatmentSupporterName.setText("")
//                    }else{
//                        editTextTreatmentSupporterName.setText(""+model.treatmentSupporterName)
//                    }
//
//                    if (model.treatmentSupporterPost.isNullOrEmpty()){
//                        editTextTreatmentSupporterPost.setText("")
//                    }else{
//                        editTextTreatmentSupporterPost.setText(""+model.treatmentSupporterPost)
//                    }
//
//                    if (model.treatmentSupporterMobileNumber.isNullOrEmpty()){
//                        editTextTreatmentSupporterMobileNumber.setText("")
//                    }else{
//                        editTextTreatmentSupporterMobileNumber.setText(""+model.treatmentSupporterMobileNumber)
//                    }
//
//                    if (model.treatmentSupporterEndDate.isNullOrEmpty()){
//                        editTextTreatmentEndDate.setText("")
//                    }else{
//                        editTextTreatmentEndDate.setText(""+model.treatmentSupporterEndDate)
//                    }
//
//                    if (model.treatmentSupporterResult.isNullOrEmpty()){
//                        editTextTreatmentResult.setText("")
//                    }else{
//                        editTextTreatmentResult.setText(""+model.treatmentSupporterResult)
//                    }

                editTextBankAccount.setText(model.bankAccount.getNotNullData())
                editTextBankIFSC.setText(model.bankIFSC.getNotNullData())
                editTextTreatmentSupporterName.setText(model.treatmentSupporterName.getNotNullData())
                editTextTreatmentSupporterPost.setText(model.treatmentSupporterPost.getNotNullData())
                editTextTreatmentSupporterMobileNumber.setText(model.treatmentSupporterMobileNumber.getNotNullData())
                editTextTreatmentEndDate.setText(model.treatmentSupporterEndDate.getNotNullData())
                editTextTreatmentResult.setText(model.treatmentSupporterResult.getNotNullData())

                btSignIn.visibility = View.GONE
                editTextPatientCheckupDate.isEnabled = false
                editTextHemoglobinLevelAge.isEnabled = false
                editTextHemoglobinCheckupDate.isEnabled = false
                editTextMuktiID.isEnabled = false
                editTextNakshayID.isEnabled = false
                editTextAadhaarNumber.isEnabled = false
                editTextBusiness.isEnabled = false
                editTextBankAccount.isEnabled = false
                editTextBankIFSC.isEnabled = false
                editTextTreatmentSupporterName.isEnabled = false
                editTextTreatmentSupporterPost.isEnabled = false
                editTextTreatmentSupporterMobileNumber.isEnabled = false
                editTextTreatmentEndDate.isEnabled = false
                editTextTreatmentResult.isEnabled = false
                for (i in 0..<radioGroupPulmonaryRadioGroup.getChildCount()) {
                    radioGroupPulmonaryRadioGroup.getChildAt(i).setEnabled(false)
                }
            }

            btSignIn.singleClick {

                Log.e("TAG", "formFill1: "+formFill1)

//                if (!formFill1) {
//                    showSnackBar(getString(R.string.please_fill_required_entries))
//                } else

                    if (editTextPatientCheckupDate.text.toString() == "") {
                    showSnackBar(getString(R.string.selectCheckupDate))
//                } else if (editTextHemoglobinLevelAge.text.toString() == "") {
//                    showSnackBar(getString(R.string.hemoglobin_level))
//                } else if (editTextHemoglobinCheckupDate.text.toString() == "") {
//                    showSnackBar(getString(R.string.selectCheckupDate))
//                } else if (editTextMuktiID.text.toString() == "") {
//                    showSnackBar(getString(R.string.enterMuktiID))
                } else if (editTextNakshayID.text.toString() == "") {
                    showSnackBar(getString(R.string.enterNakshayID))
                } else if (editTextAadhaarNumber.text.toString() == "") {
                    showSnackBar(getString(R.string.enterAadhaarNumber))
                } else if (editTextBusiness.text.toString() == "") {
                    showSnackBar(getString(R.string.enterBusiness))
//                } else if (editTextBankAccount.text.toString() == "") {
//                    showSnackBar(getString(R.string.enterBankAccount))
//                } else if (editTextBankIFSC.text.toString() == "") {
//                    showSnackBar(getString(R.string.enterBankIFSC))
//                } else if (editTextTreatmentSupporterName.text.toString() == "") {
//                    showSnackBar(getString(R.string.enter_Treatment_Supporter_Name))
//                } else if (editTextTreatmentSupporterPost.text.toString() == "") {
//                    showSnackBar(getString(R.string.enter_Treatment_Supporter_Post))
//                } else if (editTextTreatmentSupporterMobileNumber.text.toString() == "") {
//                    showSnackBar(getString(R.string.enter_Treatment_Supporter_Mobile_Number))
//                }  else if (editTextTreatmentEndDate.text.toString() == "") {
//                    showSnackBar(getString(R.string.enter_TreatmentEndDate))
//                }  else if (editTextTreatmentResult.text.toString() == "") {
//                    showSnackBar(getString(R.string.enter_TreatmentResult))
                } else {
                    viewModel.hemoglobinLevelAge = editTextHemoglobinLevelAge.text.toString()
                    viewModel.muktiID = editTextMuktiID.text.toString()
                    viewModel.nakshayID = editTextNakshayID.text.toString()
                    viewModel.aadhaarNumber = editTextAadhaarNumber.text.toString()
                    viewModel.business = editTextBusiness.text.toString()
                    viewModel.bankAccount = editTextBankAccount.text.toString()
                    viewModel.bankIFSC = editTextBankIFSC.text.toString()
                    viewModel.treatmentSupporterName = editTextTreatmentSupporterName.text.toString()
                    viewModel.treatmentSupporterPost = editTextTreatmentSupporterPost.text.toString()
                    viewModel.treatmentSupporterMobileNumber = editTextTreatmentSupporterMobileNumber.text.toString()
                    viewModel.treatmentSupporterResult = editTextTreatmentResult.text.toString()
//                    NBPA.callBackListener!!.onCallBack(1002)


                    readData(LOGIN_DATA) { loginUser ->
                        if (loginUser != null) {
                            val dataId = Gson().fromJson(loginUser, Login::class.java).id
                            val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart(user_id, ""+dataId)

                            if (viewModel.name != null) {
                                requestBody.addFormDataPart(formName, ""+viewModel.name)
                            }
                            if (viewModel.fatherHusbandType != null) {
                                requestBody.addFormDataPart(fatherHusbandType, ""+viewModel.fatherHusbandType)
                            }
                            if (viewModel.fatherHusband != null) {
                                requestBody.addFormDataPart(fatherHusband, ""+viewModel.fatherHusband)
                            }
                            if (viewModel.mother != null) {
                                requestBody.addFormDataPart(mother, viewModel.mother)
                            }
                            if (viewModel.gender != null) {
                                requestBody.addFormDataPart(formGender, viewModel.gender)
                            }
                            if (viewModel.age != null) {
                                requestBody.addFormDataPart(age, viewModel.age)
                            }
                            if (viewModel.height != null) {
                                requestBody.addFormDataPart(height, viewModel.height)
                            }
                            if (viewModel.weight != null) {
                                requestBody.addFormDataPart(weight, viewModel.weight)
                            }
                            if (viewModel.numberOfMembers != null) {
                                requestBody.addFormDataPart(numberOfMembers, viewModel.numberOfMembers)
                            }
                            if (viewModel.numberOfChildren != null) {
                                requestBody.addFormDataPart(numberOfChildren, viewModel.numberOfChildren)
                            }
                            if (viewModel.address != null) {
                                requestBody.addFormDataPart(address, viewModel.address)
                            }
                            if (viewModel.dmcName != null) {
                                requestBody.addFormDataPart(dmcName, viewModel.dmcName)
                            }
                            if (viewModel.block != null) {
                                requestBody.addFormDataPart(block, viewModel.block)
                            }
                            if (viewModel.mobileNumber != null) {
                                requestBody.addFormDataPart(mobileNumber, viewModel.mobileNumber)
                            }
                            if (viewModel.districtState != null) {
                                requestBody.addFormDataPart(districtState, viewModel.districtState)
                            }
                            if (viewModel.cardTypeAPLBPL != null) {
                                requestBody.addFormDataPart(cardTypeAPLBPL, ""+viewModel.cardTypeAPLBPL)
                            }

                            if (viewModel.typeOfPatient != null) {
                                requestBody.addFormDataPart(typeOfPatient, ""+viewModel.typeOfPatient)
                            }
                            if (viewModel.patientCheckupDate != null) {
                                requestBody.addFormDataPart(patientCheckupDate, viewModel.patientCheckupDate)
                            }
                            if (viewModel.hemoglobinLevelAge != null) {
                                requestBody.addFormDataPart(hemoglobinLevelAge, viewModel.hemoglobinLevelAge)
                            }
                            if (viewModel.hemoglobinCheckupDate != null) {
                                requestBody.addFormDataPart(hemoglobinCheckupDate, viewModel.hemoglobinCheckupDate)
                            }
                            if (viewModel.muktiID != null) {
                                requestBody.addFormDataPart(muktiID, viewModel.muktiID)
                            }
                            if (viewModel.nakshayID != null) {
                                requestBody.addFormDataPart(nakshayID, viewModel.nakshayID)
                            }
                            if (viewModel.aadhaarNumber != null) {
                                requestBody.addFormDataPart(aadhaarNumber, viewModel.aadhaarNumber)
                            }
                            if (viewModel.business != null) {
                                requestBody.addFormDataPart(business, viewModel.business)
                            }
                            if (viewModel.bankAccount != null) {
                                requestBody.addFormDataPart(bankAccount, viewModel.bankAccount)
                            }
                            if (viewModel.bankIFSC != null) {
                                requestBody.addFormDataPart(bankIFSC, viewModel.bankIFSC)
                            }
                            if (viewModel.treatmentSupporterName != null) {
                                requestBody.addFormDataPart(treatmentSupporterName, viewModel.treatmentSupporterName)
                            }
                            if (viewModel.treatmentSupporterPost != null) {
                                requestBody.addFormDataPart(treatmentSupporterPost, viewModel.treatmentSupporterPost)
                            }
                            if (viewModel.treatmentSupporterMobileNumber != null) {
                                requestBody.addFormDataPart(treatmentSupporterMobileNumber, viewModel.treatmentSupporterMobileNumber)
                            }
                            if (viewModel.treatmentSupporterEndDate != null) {
                                requestBody.addFormDataPart(treatmentSupporterEndDate, viewModel.treatmentSupporterEndDate)
                            }
                            if (viewModel.treatmentSupporterResult != null) {
                                requestBody.addFormDataPart(treatmentSupporterResult, viewModel.treatmentSupporterResult)
                            }




//                            MaterialAlertDialogBuilder(requireActivity(), R.style.LogoutDialogTheme)
//                                .setTitle(resources.getString(R.string.app_name))
//                                .setMessage(resources.getString(R.string.are_your_sure_want_to_submit))
//                                .setPositiveButton(resources.getString(R.string.submit)) { dialog, _ ->
//                                    dialog.dismiss()
                                    if(networkFailed) {
                                        viewModel.registerWithFiles(
                                            view = requireView(),
                                            requestBody.build()
                                        ){
                                            Log.e("TAG", "this.scheme_id.toString()" +this.scheme_id.toString())
                                            viewModel.scheme_id = this.scheme_id.toString()
                                            NBPA.callBackListener!!.onCallBack(1002)
                                        }
                                    } else {
                                        requireContext().callNetworkDialog()
                                    }
                                }
//                                .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
//                                    dialog.dismiss()
//                                    NBPA.callBackListener!!.onCallBack(1000)
//                                }
//                                .setCancelable(false)
//                                .show()
//
//                        }
                    }

                }
            }
        }
    }



    override fun onCallBack(pos: Int) {
        Log.e("TAG", "onCallBackNo2 "+pos)
//        NBPA.callBackListener!!.onCallBack(1000)
    }
}