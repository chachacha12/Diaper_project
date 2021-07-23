package com.DIAPERS.diaper_project.Class

import com.google.gson.annotations.SerializedName

data class Org(@SerializedName("chief") var chief:String,
               @SerializedName("fax") var fax:String,
               @SerializedName("lacation") var lacation:String,
@SerializedName("name") var name:String,
@SerializedName("phone") var phone:String)  {
}