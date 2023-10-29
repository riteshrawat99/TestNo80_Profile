package com.pupup.testno80_profile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.collection.LLRBNode
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    @SuppressLint("ResourceType")
    var verficationId  = ""
    var auth = FirebaseAuth.getInstance()
    lateinit var callbacks: OnVerificationStateChangedCallbacks
    lateinit var edPhone : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

         edPhone  = findViewById(R.id.edPhone)

        val sendBtn : Button = findViewById(R.id.sendBtnForOtp)
        sendBtn.setOnClickListener {
            sendOtp(edPhone.text.toString())
            val layout = layoutInflater.inflate(R.layout.custome_otp_verifyt_dialog,null)
            val verifyPhone : EditText = layout.findViewById(R.id.verifyPhone)
            val verfiyBtnForOtp : Button = layout.findViewById(R.id.verfiyBtnForOtp)
            val closeIcon : ImageView = layout.findViewById(R.id.closeIcon)

            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setView(layout)
            val dialog = alertDialog.create()
            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.show()
            closeIcon.setOnClickListener {
                dialog.dismiss()
                Toast.makeText(this@LoginActivity, "Dialog is close", Toast.LENGTH_SHORT).show()
            }
            verfiyBtnForOtp.setOnClickListener {
                val credential = PhoneAuthProvider.getCredential(verficationId,verifyPhone.text.toString())
                verifyOtp(credential)
            }
        }


        callbacks = object : OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                verifyOtp(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {

            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verficationId = p0
            }
        }

    }

    fun sendOtp(phone :String){
        var phoneAuthOption = PhoneAuthOptions.newBuilder()
            .setPhoneNumber("+91$phone")
            .setActivity(this)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOption)
    }
    private fun verifyOtp(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                Toast.makeText(this, "verify successfully", Toast.LENGTH_SHORT).show()
                checkDetails()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }

    fun checkDetails(){
       val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users")
        userRef.document(uid!!).get()
            .addOnCompleteListener { task->
                if (task.isSuccessful){
                    val document = task.result
                    if (document.exists()){
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    }
                    else{
                        startActivity(Intent(this@LoginActivity,RegistrationActivity::class.java))
                    }
                }
                else{
                    Toast.makeText(this, "document is missiing", Toast.LENGTH_SHORT).show()
                }
            }
    }

}


