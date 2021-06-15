package com.example.diaper_project.Class

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class GetOne(@SerializedName("success")var success: Boolean, @SerializedName("result")var result:String ){

}