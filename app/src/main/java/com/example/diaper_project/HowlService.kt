package com.example.diaper_project


import com.example.diaper_project.Class.GetAll
import com.example.diaper_project.Class.Users
import com.example.diaper_project.Class.currentUser
import com.example.diaper_project.Class.success
import retrofit2.Call
import retrofit2.http.*

//우리가 무슨 데이터를 서버에 요구했을때, 서버에서는 result라는 키값에 value를 넣어서 반환해줌.
//이때 안드스튜디오에서 그 result값을 받을수있는 모델이 있어야함. 그래서 지금 이 data class가 그 모델역할.
//data class ResponseDTO(var result:String?=null)

interface HowlService {      //서버로 오고가는 api들을 관리해주는 인터페이스임  //서버와 앱 간의 연결역할..?

    //사용자 등록
    //post는 @Field는 FormData형식으로 보냄. @Body는 json으로 서버에 보내줌.
    //@FormUrlEncoded       //밑에 함수에 field가 있다면 넣어야됨.
    @POST("api/auth/register")
    fun postResquest(@Body users: Users):Call<success>

    //로그인
    @POST("api/auth/login")
    fun loginRequest(@Body users: Users):Call<currentUser>

    //로그아웃
    @POST("api/auth/logoutc")
    fun logoutRequest(@Body currentUser: currentUser):Call<success>



    //이용자 모두조회
    @GET("api/cnts")
    fun getAllRequest(@Header("Authorization")authorization:String): Call<GetAll>











}