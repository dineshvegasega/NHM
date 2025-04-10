package com.nhm.distribution.screens.mainActivity.menu

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ItemChildMenuModel(
    @SerializedName("title")
    var title: String? = null
) : Serializable