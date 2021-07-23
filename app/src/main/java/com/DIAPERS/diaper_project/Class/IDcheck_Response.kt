package com.DIAPERS.diaper_project.Class

import com.google.gson.annotations.SerializedName

data class IDcheck_Response(@SerializedName("exists")var exists: Boolean, @SerializedName("msg")var msg:String) {
}