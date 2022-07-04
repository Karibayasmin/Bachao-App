package com.singularitybd.bachaoapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.singularitybd.bachaoapp.R
import com.singularitybd.bachaoapp.preference.PreferenceUtil

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        if(PreferenceUtil.getIsLoggedIn(this) == true){
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            var intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}