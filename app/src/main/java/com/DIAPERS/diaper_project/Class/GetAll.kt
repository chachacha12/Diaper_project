package com.DIAPERS.diaper_project.Class

import com.google.gson.annotations.SerializedName

data class GetAll(@SerializedName("success")var success: Boolean, @SerializedName("result")var result:List<Any>) {

}