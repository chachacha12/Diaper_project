package com.DIAPERS.diaper_project
//etc액티비티에서 사용자가 한벗둥지 네이버 카페 이동버튼 클릭시 보여줄 화면

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.activity_service.*

class ServiceActivity : BasicActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)
        init()
    }

    fun init(){
        webView_service.webViewClient = WebViewClient()
        webView_service.settings.javaScriptEnabled = true
        webView_service.settings.builtInZoomControls = true //줌인 기능 가능하게함
        webView_service.settings.displayZoomControls=false  //줌인할때 나오는 버튼 안보이게함
        //처음에 웹뷰에 보이는 사이트가 핸드폰 화면에 맞게 전체화면으로 보이게 하는 기능
        webView_service.settings.loadWithOverviewMode = true
        webView_service.settings.useWideViewPort = true
        webView_service.settings.defaultTextEncodingName = "utf-8"
        webView_service.loadUrl("https://blog.naver.com/doongji365")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView_service.canGoBack()) { // 웹뷰에서 뒤로가기 버튼을 누르면 뒤로 이동
            webView_service.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
