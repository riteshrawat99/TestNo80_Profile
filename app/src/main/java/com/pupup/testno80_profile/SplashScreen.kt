package com.pupup.testno80_profile

import android.content.Intent
import android.content.LocusId
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    lateinit var auth :FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        auth = FirebaseAuth.getInstance()


        Handler().postDelayed({
            if (auth.currentUser?.uid !=null){
            startActivity(Intent(this@SplashScreen,MainActivity::class.java))
            }
            else{
            startActivity(Intent(this@SplashScreen,LoginActivity::class.java))
            }
            finish()
        },1500)
    }
}