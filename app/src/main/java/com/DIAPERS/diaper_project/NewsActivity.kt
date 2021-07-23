package com.DIAPERS.diaper_project   //etc액티비티에서 사용자가 뉴스조회버튼을 클릭시 보여줄 복지관련 뉴스창

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

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
        webView_news.loadUrl("http://www.bokjiro.go.kr/nwel/welfareinfo/livwelnews/news/retireveNewsList.do")

    }
}
