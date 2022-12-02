package com.DIAPERS.diaper_project  //관리자 메뉴 - 여기서 권한이 높은 사용자는 특정 사용자를 추가해주거나 삭제하거나 등등이 가능함

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_manager.*

class ManagerActivity : BasicActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        init()
    }

    fun init(){
        webView_manager.webViewClient = WebViewClient()
        webView_manager.settings.javaScriptEnabled = true
        webView_manager.settings.builtInZoomControls = true //줌인 기능 가능하게함
        webView_manager.settings.displayZoomControls=false  //줌인할때 나오는 버튼 안보이게함
        //처음에 웹뷰에 보이는 사이트가 핸드폰 화면에 맞게 전체화면으로 보이게 하는 기능
        webView_manager.settings.loadWithOverviewMode = true
        webView_manager.settings.useWideViewPort = true
        webView_manager.settings.defaultTextEncodingName = "utf-8"
        webView_manager.loadUrl("http://35.224.228.204:5000/admin/") //https://diapers-dungji.herokuapp.com/admin/
    }
}
