package com.example.diaper_project.Class

import com.google.gson.annotations.SerializedName

data class Log(@SerializedName("cnt") var cnt: String? =null, @SerializedName("time") var time: String? =null, @SerializedName("inner_opened") var inner_opened:Number? =null,
               @SerializedName("inner_new") var inner_new:Number? =null, @SerializedName("outer_opened") var outer_opened:Number? =null, @SerializedName("outer_new") var outer_new:Number? =null,
               @SerializedName("comment") var comment:String? =null) {

}