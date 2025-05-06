package com.nhm.distribution.screens.main.NBPA.editForms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nhm.distribution.databinding.Form2Binding
import com.nhm.distribution.screens.main.NBPA.NBPAViewModel
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPAEdit_Form2 : Fragment() {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form2Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = Form2Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)

        binding.apply {

            ivMenu.singleClick {
                NBPAEdit.callBackListener!!.onCallBack(1000)
            }

            if (viewModel.editData!!.typeOfPatient == "1") {
                radioButtonPulmonary.isChecked = true
            } else if (viewModel.editData!!.typeOfPatient == "2") {
                radioButtonExtraPulmonary.isChecked = true
            } else {
                radioButtonOther.isChecked = true
            }
//            for (i in 0..<radioGroupPulmonaryRadioGroup.getChildCount()) {
//                radioGroupPulmonaryRadioGroup.getChildAt(i).setEnabled(false)
//            }

            editTextPatientCheckupDate.setText(""+viewModel.editData!!.patientCheckupDate)
            editTextHemoglobinLevelAge.setText(""+viewModel.editData!!.hemoglobinLevelAge)
            editTextHemoglobinCheckupDate.setText(""+viewModel.editData!!.hemoglobinCheckupDate)
            editTextMuktiID.setText(""+viewModel.editData!!.muktiID)
            editTextNakshayID.setText(""+viewModel.editData!!.nakshayID)
            editTextAadhaarNumber.setText(""+viewModel.editData!!.aadhaarNumber)
            editTextBusiness.setText(""+viewModel.editData!!.business)
            editTextBankAccount.setText(""+viewModel.editData!!.bankAccount)
            editTextBankIFSC.setText(""+viewModel.editData!!.bankIFSC)
            editTextTreatmentSupporterName.setText(""+viewModel.editData!!.treatmentSupporterName)
            editTextTreatmentSupporterPost.setText(""+viewModel.editData!!.treatmentSupporterPost)
            editTextTreatmentSupporterMobileNumber.setText(""+viewModel.editData!!.treatmentSupporterMobileNumber)
            editTextTreatmentEndDate.setText(""+viewModel.editData!!.treatmentSupporterEndDate)
            editTextTreatmentResult.setText(""+viewModel.editData!!.treatmentSupporterResult)
        }
    }
}