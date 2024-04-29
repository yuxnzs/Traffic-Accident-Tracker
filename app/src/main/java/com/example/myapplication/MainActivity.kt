package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    // 目標網址，用來取得資料
    private val targetUrl =
        "https://api.kcg.gov.tw/api/service/get/ad197194-6db9-4f14-ad38-2adceea831c3"
    private var getString: String = ""
    // 顯示於 RecyclerView 中的資料集合
    private lateinit var displayedData: ArrayList<OneItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化介面元件
        val spinner = findViewById<Spinner>(R.id.spinner)
        val btn = findViewById<Button>(R.id.btnUpdate)
        val myRecyclerView = findViewById<RecyclerView>(R.id.rv)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val selectedData = ArrayList<OneItem>()

        // RecyclerView 設定 LayoutManager
        myRecyclerView.layoutManager = LinearLayoutManager(this)
        // 初始化 displayedData 集合
        displayedData = ArrayList<OneItem>()

        // 一開始先將 ProgressBar 設定為隱藏
        progressBar?.visibility = ProgressBar.GONE

        btn.setOnClickListener {
            // 在開始取得資料時顯示 ProgressBar
            progressBar?.visibility = ProgressBar.VISIBLE

            // 初始化 OkHttpClient 實例進行網路請求
            val client = OkHttpClient.Builder().build()
            val request = Request.Builder().url(targetUrl).build()

            // 使用 GlobalScope 啟動新的協程，非阻塞方式執行網路請求
            GlobalScope.launch {
                runBlocking {
                    val response = client.newCall(request).execute()
                    response.body.run {
                        getString = string()
                        try {
                            // 解析 JSON 字串並將資料加入 displayedData 中
                            val allData =
                                JSONObject(getString).getJSONArray("data") // getJSONArray("data")為網站的 data
                            var jObj: JSONObject
                            var jDistrict: String
                            var jPrecinct: String
                            var jStatus: String
                            var jInjured: String
                            var jDeath: String

                            for (i in 0 until allData.length()) {
                                jObj = allData.getJSONObject(i) //迴圈轉幾次就抓幾筆
                                jDistrict = jObj.getString("鄉鎮市區") //抓什麼資料就什麼
                                jPrecinct = jObj.getString("單位名稱")
                                jStatus = jObj.getString("事故類型及型態說明")
                                jInjured = jObj.getString("受傷人數")
                                jDeath = jObj.getString("死亡人數")
                                displayedData.add(
                                    OneItem(
                                        jDistrict,
                                        jPrecinct,
                                        jStatus,
                                        jInjured,
                                        jDeath
                                    )
                                )

                                // 根據 Spinner 選項過濾資料
                                if (spinner.selectedItem.toString() == displayedData[i].iDistrict) {
                                    selectedData.add(
                                        OneItem(
                                            jDistrict,
                                            jPrecinct,
                                            jStatus,
                                            jInjured,
                                            jDeath
                                        )
                                    )
                                }
                            }
                        } catch (e: IOException) {
                            Log.d("myTag", e.toString())
                        }
                    }
                }
                // 更新 UI，在主線程中執行
                runOnUiThread {
                    var myAdapter = MyRecyclerViewAdapter(displayedData)
                    // 根據 Spinner 選項更新 RecyclerView 的顯示內容
                    myAdapter = if (spinner.selectedItem.toString() != "請選擇") {
                        MyRecyclerViewAdapter(selectedData)
                    } else {
                        MyRecyclerViewAdapter(displayedData)
                    }
                    myRecyclerView.adapter = myAdapter

                    // 取得完畢後隱藏 progressBar
                    progressBar?.visibility = ProgressBar.GONE
                }
            }
            // 每次點擊後清空上次選擇的資料集合
            selectedData.clear();
        }
        // 初始化 Spinner 元件
        val distArr = arrayOf("請選擇",
            "鹽埕區", "鼓山區", "左營區", "楠梓區",
            "三民區", "新興區", "前金區", "苓雅區",
            "前鎮區", "旗津區", "小港區", "鳳山區",
            "林園區", "大寮區", "大樹區", "大社區",
            "仁武區", "鳥松區", "岡山區", "橋頭區",
            "燕巢區", "阿蓮區", "路竹區", "湖內區",
            "茄萣區", "梓官區", "旗山區", "美濃區",
            "六龜區", "甲仙區", "杉林區", "內門區",
            "茂林區", "桃源區", "那瑪夏區", "田寮區",
            "永安區", "彌陀區"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, distArr)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item) // 使用自定義布局，加寬打開選單後的間距
        spinner.adapter = adapter
    }
}