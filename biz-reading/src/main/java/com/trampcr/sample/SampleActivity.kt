package com.trampcr.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.trampcr.router.annotations.Destination

@Destination(
    url = "router://page-sample",
    description = "举例页面"
)
class SampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}