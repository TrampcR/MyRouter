package com.trampcr.myrouter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.trampcr.gradle.router.runtime.Router
import com.trampcr.router.annotations.Destination

@Destination(
    url = "router://page-home",
    description = "应用主页"
)
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn).setOnClickListener {
            Router.go(this, "router://trampcr/profile?name=dora&message=tom")
        }
    }
}