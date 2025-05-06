package com.nhm.distribution.screens.main.NBPA.editForms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nhm.distribution.databinding.Form1Binding
import com.nhm.distribution.screens.main.NBPA.NBPAViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPAEdit_Form1 : Fragment() {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form1Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = Form1Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
          binding.apply {
              editTextName.setText(""+viewModel.editData!!.name)
              if (viewModel.editData!!.fatherHusbandType == 1) {
                  radioButtonFather.isChecked = true
              } else {
                  radioButtonHusband.isChecked = true
              }
//              for (i in 0..<radioGroupFatherHusbandRadioGroup.getChildCount()) {
//                  radioGroupFatherHusbandRadioGroup.getChildAt(i).setEnabled(false)
//              }

              editTextFatherHusband.setText(""+viewModel.editData!!.fatherHusband)
              editTextMother.setText(""+viewModel.editData!!.mother)
              editTextGender.setText(""+viewModel.editData!!.gender)
              editTextAge.setText(""+viewModel.editData!!.age)
              editTextHeight.setText(""+viewModel.editData!!.height)
              editTextWeight.setText(""+viewModel.editData!!.weight)
              editTextNumberOfMembers.setText(""+viewModel.editData!!.numberOfMembers)
              editTextNumberOfChildrens.setText(""+viewModel.editData!!.numberOfChildren)
              editTextAddress.setText(""+viewModel.editData!!.address)
              editTextDMCName.setText(""+viewModel.editData!!.dmcName)
              editTextBlock.setText(""+viewModel.editData!!.block)
              editTextMobileNumbar.setText(""+viewModel.editData!!.mobileNumber)
              editTextDistrictState.setText(""+viewModel.editData!!.districtState)
              if (viewModel.editData!!.cardTypeAPLBPL == 1) {
                  radioButtonAPL.isChecked = true
              } else {
                  radioButtonBPL.isChecked = true
              }
//              for (i in 0..<radioGroupCardAPLBPLGroup.getChildCount()) {
//                  radioGroupCardAPLBPLGroup.getChildAt(i).setEnabled(false)
//              }
          }
    }
}