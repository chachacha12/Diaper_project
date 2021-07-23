package com.DIAPERS.diaper_project.Class

import com.google.gson.annotations.SerializedName

data class Users(@SerializedName("username") var username:String, @SerializedName("password") var password:String, @SerializedName("realname") var realname: String? =null, @SerializedName("description") var description: String? =null) {

}