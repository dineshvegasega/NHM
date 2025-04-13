package com.nhm.distribution.screens.main.NBPA.forms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.nhm.distribution.R
import com.nhm.distribution.databinding.Form2Binding
import com.nhm.distribution.screens.main.NBPA.NBPAViewModel
import com.nhm.distribution.utils.showDropDownDialog
import com.nhm.distribution.utils.showSnackBar
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class NBPA_Form2 : Fragment() {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form2Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Form2Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
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

            btSignIn.singleClick {

                if (editTextPatientCheckupDate.text.toString() == "") {
                    showSnackBar(getString(R.string.selectCheckupDate))
                } else if (editTextHemoglobinLevelAge.text.toString() == "") {
                    showSnackBar(getString(R.string.hemoglobin_level))
                } else if (editTextHemoglobinCheckupDate.text.toString() == "") {
                    showSnackBar(getString(R.string.selectCheckupDate))
                } else if (editTextMuktiID.text.toString() == "") {
                    showSnackBar(getString(R.string.enterMuktiID))
                } else if (editTextNakshayID.text.toString() == "") {
                    showSnackBar(getString(R.string.enterNakshayID))
                } else if (editTextAadhaarNumber.text.toString() == "") {
                    showSnackBar(getString(R.string.enterAadhaarNumber))
                } else if (editTextBusiness.text.toString() == "") {
                    showSnackBar(getString(R.string.enterBusiness))
                } else if (editTextBankAccount.text.toString() == "") {
                    showSnackBar(getString(R.string.enterBankAccount))
                } else if (editTextBankIFSC.text.toString() == "") {
                    showSnackBar(getString(R.string.enterBankIFSC))
                } else if (editTextTreatmentSupporterName.text.toString() == "") {
                    showSnackBar(getString(R.string.enter_Treatment_Supporter_Name))
                } else if (editTextTreatmentSupporterPost.text.toString() == "") {
                    showSnackBar(getString(R.string.enter_Treatment_Supporter_Post))
                } else if (editTextTreatmentSupporterMobileNumber.text.toString() == "") {
                    showSnackBar(getString(R.string.enter_Treatment_Supporter_Mobile_Number))
                }  else if (editTextTreatmentEndDate.text.toString() == "") {
                    showSnackBar(getString(R.string.enter_TreatmentEndDate))
                }  else if (editTextTreatmentResult.text.toString() == "") {
                    showSnackBar(getString(R.string.enter_TreatmentResult))
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
                    NBPA.callBackListener!!.onCallBack(1002)
                }
            }
        }
    }
}