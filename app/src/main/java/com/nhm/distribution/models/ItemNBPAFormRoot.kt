package com.nhm.distribution.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ItemNBPAFormRoot(
    @SerializedName("data")
    val data: List<ItemNBPAForm> = emptyList(),
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("status_code")
    val statusCode: Int? = null,
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("success")
    val success: Boolean? = false,
    @SerializedName("vendor_id")
    val vendor_id: String? = null,
    @SerializedName("meta")
    val meta: @RawValue Meta? = null,
) : Parcelable {



}

//@Parcelize
//data class Meta(
//    val first_page: String,
//    val last_page: String,
//    val per_page: Int,
//    val prev_page_url: String,
//    val total_items: Int,
//    val total_pages: Int
//): Parcelable {
//
//}




@Parcelize
data class ItemNBPAForm(
    val aadhaarNumber: String,
    var address: String,
    val age: String,
    val bankAccount: String,
    val bankIFSC: String,
    val block: String,
    val business: String,
    val cardTypeAPLBPL: Int,
    val districtState: String,
    val dmcName: String,
    val fatherHusband: String,
    val fatherHusbandType: Int,
    val foodDate: String,
    val foodHeight: String,
    val foodIdentityImage: @RawValue FoodIdentityImage,
    val foodItemImage: @RawValue FoodItemImage,
    val foodMonth: String,
    val foodSignatureImage: @RawValue FoodSignatureImage,
    val gender: String,
    val height: String,
    val hemoglobinCheckupDate: String,
    val hemoglobinLevelAge: String,
    val id: Int,
    val mobileNumber: String,
    val mother: String,
    val muktiID: String,
    val nakshayID: String,
    var name: String,
    val numberOfChildren: Int,
    val numberOfMembers: Int,
    val patientCheckupDate: String,
    val treatmentSupporterEndDate: String,
    val treatmentSupporterMobileNumber: String,
    val treatmentSupporterName: String,
    val treatmentSupporterPost: String,
    val treatmentSupporterResult: String,
    val typeOfPatient: String,
    val weight: String,
    val created_at: String,
    val updated_at: String
): Parcelable {

}

@Parcelize
data class FoodIdentityImage(
    val name: String,
    val url: String
): Parcelable{

}


@Parcelize
data class FoodItemImage(
    val name: String,
    val url: String
): Parcelable{

}


@Parcelize
data class FoodSignatureImage(
    val name: String,
    val url: String
): Parcelable{

}