package com.DIAPERS.diaper_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_etc.*


class EtcActivity : AppCompatActivity() {


    private val PERMISSION_CODE = 0;  //권한코드 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_etc)
        init()
    }

    fun init(){
        CallAction()
    }



    //권한요청 받았을때 사용자가 무엇을 선택했는지에 대한 결과값이 들어옴
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_CODE->{  //인터넷 권한요청에 대한 결과
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){  //권한요청에 수락한 경우
                    CallAction() //인터넷 권한있는지 확인해주는 함수

                    Log.e("태그","onRequestPermissionsResult---권한있는 경우")

                }else{  //권한요청에 거절한 경우
                    CallAlertDlg()  //다이얼로그 창 띄워서 한번더 사용자에게 경고해줌
                } //else
            } //PERMISSION_CODE
        }//when
    }


    fun CallAction(){   //인터넷 권한있는지 확인해주고 없으면 요청하기
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED ){
            //권한없을때
            //권한요청 해주기
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), PERMISSION_CODE)
            Log.e("태그","권한없어서 init에서 권한요청해주기완료")
        }else {
            //권한 있을때
            button_news.setOnClickListener {
                var i = Intent(this, NewsActivity::class.java)
                startActivity(i)
            }
            button_service.setOnClickListener {
                var i = Intent(this, ServiceActivity::class.java)
                startActivity(i)
            }
        }
    }


    fun  CallAlertDlg(){
        Log.e("태그","onRequestPermissionsResult---권한없는 경우")
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("권한허용")
        alertDialog.setMessage("해당 앱의 다음 기능을 이용하시려면 애플리케이션 정보>권한>에서 모든 권한을 허용하십시오")
        //권한설정 클릭시 이벤트 발생
        alertDialog.setPositiveButton("ok"){
                _,_-> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET),PERMISSION_CODE )
        }
        val dlg = alertDialog.create()
        dlg.show()
    }

}
