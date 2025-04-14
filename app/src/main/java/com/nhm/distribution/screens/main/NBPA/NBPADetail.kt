package com.nhm.distribution.screens.main.NBPA

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nhm.distribution.databinding.NbpaDetailBinding
import com.nhm.distribution.models.ItemNBPAForm
import com.nhm.distribution.screens.mainActivity.MainActivity
import com.nhm.distribution.utils.glideImagePortraitForImage
import com.nhm.distribution.utils.imageZoom
import com.nhm.distribution.utils.parcelable
import com.nhm.distribution.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPADetail : Fragment() {
    private val viewModel: NBPAViewModel by viewModels()
    private var _binding: NbpaDetailBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NbpaDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(3)
//        change = false
        binding.apply {
            val model = arguments?.parcelable<ItemNBPAForm>("key")
            Log.e("TAG", "modelmodel "+model.toString())

            model?.let {
                editTextName.setText(""+model.name)
                if (model.fatherHusbandType == 1) {
                    radioButtonFather.isChecked = true
                } else {
                    radioButtonHusband.isChecked = true
                }
                for (i in 0..<radioGroupFatherHusbandRadioGroup.getChildCount()) {
                    radioGroupFatherHusbandRadioGroup.getChildAt(i).setEnabled(false)
                }

                editTextFatherHusband.setText(""+model.fatherHusband)
                editTextMother.setText(""+model.mother)
                editTextGender.setText(""+model.gender)
                editTextAge.setText(""+model.age)
                editTextHeight.setText(""+model.height)
                editTextWeight.setText(""+model.weight)
                editTextNumberOfMembers.setText(""+model.numberOfMembers)
                editTextNumberOfChildrens.setText(""+model.numberOfChildren)
                editTextAddress.setText(""+model.address)
                editTextDMCName.setText(""+model.dmcName)
                editTextBlock.setText(""+model.block)
                editTextMobileNumbar.setText(""+model.mobileNumber)
                editTextDistrictState.setText(""+model.districtState)
                if (model.cardTypeAPLBPL == 1) {
                    radioButtonAPL.isChecked = true
                } else {
                    radioButtonBPL.isChecked = true
                }
                for (i in 0..<radioGroupCardAPLBPLGroup.getChildCount()) {
                    radioGroupCardAPLBPLGroup.getChildAt(i).setEnabled(false)
                }

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
                editTextHemoglobinLevelAge.setText(""+model.hemoglobinLevelAge)
                editTextHemoglobinCheckupDate.setText(""+model.hemoglobinCheckupDate)
                editTextMuktiID.setText(""+model.muktiID)
                editTextNakshayID.setText(""+model.nakshayID)
                editTextAadhaarNumber.setText(""+model.aadhaarNumber)
                editTextBusiness.setText(""+model.business)
                editTextBankAccount.setText(""+model.bankAccount)
                editTextBankIFSC.setText(""+model.bankIFSC)
                editTextTreatmentSupporterName.setText(""+model.treatmentSupporterName)
                editTextTreatmentSupporterPost.setText(""+model.treatmentSupporterPost)
                editTextTreatmentSupporterMobileNumber.setText(""+model.treatmentSupporterMobileNumber)
                editTextTreatmentEndDate.setText(""+model.treatmentSupporterEndDate)
                editTextTreatmentResult.setText(""+model.treatmentSupporterResult)

                editTextMonth.setText(""+model.foodMonth)
                editTextDate.setText(""+model.foodDate)
                editTextHeight2.setText(""+model.foodHeight)
                model.foodSignatureImage?.url?.glideImagePortraitForImage(ivSignature.context, ivSignature)
                model.foodItemImage?.url?.glideImagePortraitForImage(ivImagePassportsizeImage.context, ivImagePassportsizeImage)
                model.foodIdentityImage?.url?.glideImagePortraitForImage(ivImageIdentityImage.context, ivImageIdentityImage)
//                ivSignature.singleClick {
//                    model.foodSignatureImage?.url?.let {
//                        arrayListOf(it).imageZoom(ivSignature, 2)
//                    }
//                }
//                ivImagePassportsizeImage.singleClick {
//                    model.foodItemImage?.url?.let {
//                        arrayListOf(it).imageZoom(ivImagePassportsizeImage, 2)
//                    }
//                }
//                ivImageIdentityImage.singleClick {
//                    model.foodIdentityImage?.url?.let {
//                        arrayListOf(it).imageZoom(ivImageIdentityImage, 2)
//                    }
//                }

                editTextName.isEnabled = false
                editTextFatherHusband.isEnabled = false
                editTextMother.isEnabled = false
                editTextGender.isEnabled = false
                editTextAge.isEnabled = false
                editTextHeight.isEnabled = false
                editTextNumberOfMembers.isEnabled = false
                editTextNumberOfChildrens.isEnabled = false
                editTextAddress.isEnabled = false
                editTextDMCName.isEnabled = false
                editTextBlock.isEnabled = false
                editTextMobileNumbar.isEnabled = false
                editTextDistrictState.isEnabled = false
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

                editTextMonth.isEnabled = false
                editTextDate.isEnabled = false
                editTextHeight2.isEnabled = false
            }

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.mainActivity.get()?.callFragment(4)
//        change = false
    }




}