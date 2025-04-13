package com.nhm.distribution.models.aaa

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.nhm.distribution.models.Meta
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ItemProductRoot(
    @SerializedName("data")
    val data: List<ItemProduct> = emptyList(),
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

@Parcelize
data class Meta(
    val first_page: String,
    val last_page: String,
    val per_page: Int,
    val prev_page_url: String,
    val total_items: Int,
    val total_pages: Int
): Parcelable {

}


