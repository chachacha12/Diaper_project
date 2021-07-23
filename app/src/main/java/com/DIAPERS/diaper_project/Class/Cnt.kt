package com.DIAPERS.diaper_project.Class

import com.google.gson.annotations.SerializedName

data class Cnt(@SerializedName("inner_product")var inner_product:String, @SerializedName("deactivated")var deactivated:Boolean, @SerializedName("description")var description:String, @SerializedName("name")var name:String, @SerializedName("outer_product")var outer_product:String, @SerializedName("birth")var birth:String ) {
}