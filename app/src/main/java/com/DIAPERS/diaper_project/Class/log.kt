package com.DIAPERS.diaper_project.Class

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class log(@SerializedName("cnt") var cnt: String, @SerializedName("time") var time: String, @SerializedName("inner_opened") var inner_opened:Number,
               @SerializedName("inner_new") var inner_new:Number, @SerializedName("outer_opened") var outer_opened:Number, @SerializedName("outer_new") var outer_new:Number,
               @SerializedName("comment") var comment:String,  @SerializedName("created_by") var created_by:String? =null, @SerializedName("modified_by") var modified_by:String? =null , @SerializedName("id") var id:String? =null ):Serializable {
}