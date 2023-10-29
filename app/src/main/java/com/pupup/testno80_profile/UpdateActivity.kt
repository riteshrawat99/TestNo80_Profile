package com.pupup.testno80_profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdateActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val upName : EditText = findViewById(R.id.upName)
        val upEmail : EditText = findViewById(R.id.upEmail)
        val upBtn : Button = findViewById(R.id.upBtn)
        val container : RelativeLayout = findViewById(R.id.container)
        container.setBackgroundResource(R.drawable.backgournd)

        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid
        db.collection("users").document(uid.toString()).get()
            .addOnSuccessListener { task->
              val data = task.data
                val name = data?.get("u_name").toString()
                upName.setText(name)
                val email = data?.get("u_email").toString()
                upEmail.setText(email)
            }
        upBtn.setOnClickListener {
            db.collection("users").document(uid.toString()).update("u_name",upName.text.toString(),"u_email",upEmail.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this@UpdateActivity, "Update Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@UpdateActivity,MainActivity::class.java))
                    finish()
                }
        }
    }
}