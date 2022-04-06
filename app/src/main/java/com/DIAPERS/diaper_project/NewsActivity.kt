package com.DIAPERS.diaper_project   //etc액티비티에서 사용자가 인스타 이동버튼 클릭시

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : BasicActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        init()
    }

    fun init(){
        webView_news.webViewClient = WebViewClient()
        webView_news.settings.javaScriptEnabled = true
        webView_news.settings.builtInZoomControls = true //줌인 기능 가능하게함
        webView_news.settings.displayZoomControls=false  //줌인할때 나오는 버튼 안보이게함
        //처음에 웹뷰에 보이는 사이트가 핸드폰 화면에 맞게 전체화면으로 보이게 하는 기능
        webView_news.settings.loadWithOverviewMode = true
        webView_news.settings.useWideViewPort = true
        webView_news.settings.defaultTextEncodingName = "utf-8"
        webView_news.loadUrl("https://instagram.com/doongji365?utm_medium=copy_link")
        }

    //웹뷰에서 사용자가 뒤로가기 버튼 클릭시
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView_news.canGoBack()) { // 웹뷰에서 뒤로가기 버튼을 누르면 뒤로 이동
            webView_news.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
