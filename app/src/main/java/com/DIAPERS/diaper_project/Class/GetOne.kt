package com.DIAPERS.diaper_project.Class

import com.google.gson.annotations.SerializedName

data class GetOne(@SerializedName("success")var success: Boolean, @SerializedName("result")var result: OrgResult ){}

data class OrgResult(var chief: String, var phone: String, var register_on: Boolean,
                     var name: String, var fax: String, var location: String, var id: String){}