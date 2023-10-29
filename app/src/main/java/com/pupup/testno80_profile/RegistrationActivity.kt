package com.pupup.testno80_profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class RegistrationActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    lateinit var auth : FirebaseAuth

    @SuppressLint("MissingInflatedId", "RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val editName : EditText = findViewById(R.id.editName)
        val editEmail : EditText =findViewById(R.id.editEmail)
        val submitBtn : Button = findViewById(R.id.submitBtn)

        auth = FirebaseAuth.getInstance()

        submitBtn.setOnClickListener {
            val uid = auth.currentUser?.uid
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid.toString()).set(Users(editName.text.toString(),editEmail.text.toString(),uid))
                .addOnSuccessListener {
                    Toast.makeText(this@RegistrationActivity, "Insert Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegistrationActivity,MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this@RegistrationActivity, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }

    }
}