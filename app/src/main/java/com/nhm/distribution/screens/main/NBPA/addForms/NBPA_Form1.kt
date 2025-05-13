package com.nhm.distribution.screens.main.NBPA.addForms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nhm.distribution.R
import com.nhm.distribution.databinding.Form1Binding
import com.nhm.distribution.screens.main.NBPA.NBPAViewModel
import com.nhm.distribution.utils.showDropDownDialog
import com.nhm.distribution.utils.showSnackBar
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPA_Form1 : Fragment() {
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


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)

        binding.apply {

            radioGroupFatherHusbandRadioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                    when (checkedId) {
                        radioButtonFather.id ->  viewModel.fatherHusbandType = 1
                        radioButtonHusband.id ->  viewModel.fatherHusbandType = 2
                    }
                }
            })

            radioGroupCardAPLBPLGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                    when (checkedId) {
                        radioButtonAPL.id ->  viewModel.cardTypeAPLBPL = 1
                        radioButtonBPL.id ->  viewModel.cardTypeAPLBPL = 2
                    }
                }
            })

            editTextGender.singleClick {
                requireActivity().showDropDownDialog(type = 1) {
                    editTextGender.setText(name)
                    when (position) {
                        0 -> viewModel.gender = getString(R.string.maleGender)
                        1 -> viewModel.gender = getString(R.string.femaleGender)
                        2 -> viewModel.gender = getString(R.string.otherGender)
                    }
                }
            }


            btSignIn.singleClick {
                if (editTextName.text.toString() == "") {
                    showSnackBar(getString(R.string.enterName))
                } else if (editTextFatherHusband.text.toString() == "") {
                    showSnackBar(
                        if (viewModel.fatherHusbandType == 1) {
                            getString(R.string.enterFatherName)
                        } else {
                            getString(R.string.enterHusbandName)
                        })
//                } else if (editTextMother.text.toString() == "") {
//                    showSnackBar(getString(R.string.enterMotherName))
                } else if (editTextGender.text.toString() == "") {
                    showSnackBar(getString(R.string.selectGender))
                } else if (editTextAge.text.toString() == "") {
                    showSnackBar(getString(R.string.enterAge))
                } else if (editTextHeight.text.toString() == "") {
                    showSnackBar(getString(R.string.enterHeight))
                } else if (editTextWeight.text.toString() == "") {
                    showSnackBar(getString(R.string.enterWeight))
                } else if (editTextNumberOfMembers.text.toString() == "") {
                    showSnackBar(getString(R.string.enterNumbersOfMembers))
                } else if (editTextNumberOfChildrens.text.toString() == "") {
                    showSnackBar(getString(R.string.enterNumbersOfChildrens))
                } else if (editTextAddress.text.toString() == "") {
                    showSnackBar(getString(R.string.enterAddress))
                } else if (editTextDMCName.text.toString() == "") {
                    showSnackBar(getString(R.string.enterDMCName))
                } else if (editTextBlock.text.toString() == "") {
                    showSnackBar(getString(R.string.enterBlock))
                } else if (editTextMobileNumbar.text.toString() == "") {
                    showSnackBar(getString(R.string.enterMobileNumberForm))
                } else if (editTextDistrictState.text.toString() == "") {
                    showSnackBar(getString(R.string.enterDistrictState))
                } else {
                    viewModel.name = editTextName.text.toString()
                    viewModel.fatherHusband = editTextFatherHusband.text.toString()
                    viewModel.mother = editTextMother.text.toString()
                    viewModel.age = editTextAge.text.toString()
                    viewModel.height = editTextHeight.text.toString()
                    viewModel.weight = editTextWeight.text.toString()
                    viewModel.numberOfMembers = editTextNumberOfMembers.text.toString()
                    viewModel.numberOfChildren = editTextNumberOfChildrens.text.toString()
                    viewModel.address = editTextAddress.text.toString()
                    viewModel.dmcName = editTextDMCName.text.toString()
                    viewModel.block = editTextBlock.text.toString()
                    viewModel.mobileNumber = editTextMobileNumbar.text.toString()
                    viewModel.districtState = editTextDistrictState.text.toString()
                    NBPA.callBackListener!!.onCallBack(1001)

                }

            }
        }
    }
}