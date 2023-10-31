package com.pupup.testno80_profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID


class MainActivity : AppCompatActivity() {
    var auth = FirebaseAuth.getInstance()

    @SuppressLint("MissingInflatedId")
    private var fileUri: Uri? = null
    lateinit var profileImage: ImageView
    val IMAGE_REQUEST_CODE = 134
    companion object{
        const val CAMERA_REQUEST = 123
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val moreIcon: ImageView = findViewById(R.id.moreIcon)
        moreIcon.setOnClickListener {
            val popupMenu = PopupMenu(this, moreIcon)
            popupMenu.menuInflater.inflate(R.menu.option_menu_item, popupMenu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener { id ->
                when (id.itemId) {
                    R.id.logout -> {
                        auth.signOut()
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                        Toast.makeText(this@MainActivity, "you are logged out!", Toast.LENGTH_SHORT)
                            .show()
                    }

                    R.id.phone -> {
                        val dialIntent = Intent(Intent.ACTION_DIAL)
                        startActivity(dialIntent)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.email -> {
                        val emailIntent = Intent(Intent.ACTION_SENDTO)
                        emailIntent.data =
                            Uri.parse("mailto:recipient@example.com") // Replace with the recipient's email address
                        startActivity(emailIntent)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.delete -> {
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.setPositiveButton("Yes") { _, _ ->
                            auth.currentUser?.delete()
                            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            Toast.makeText(
                                this@MainActivity,
                                "Your account is deleted successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                            .setNegativeButton("No") { _, _ ->
                                Toast.makeText(this@MainActivity, "No...", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        val dialog = alertDialog.create()
                        dialog.setTitle("Delete Account")
                        dialog.setMessage("Do you want to delete your account!")
                        dialog.setIcon(R.drawable.baseline_delete_24)
                        dialog.show()
                    }
                }
                true
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        val menuIocn: ImageView = findViewById(R.id.menuIocn)
        menuIocn.setOnClickListener {
            drawerLayout.open()
        }
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.update -> {
//                    Toast.makeText(this@MainActivity, "update", Toast.LENGTH_SHORT).show()
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setPositiveButton("Yes") { _, _ ->
                        startActivity(Intent(this@MainActivity, UpdateActivity::class.java))
                    }
                    alertDialog.setNegativeButton("No") { _, _ ->
                        Toast.makeText(this@MainActivity, "Nooo..", Toast.LENGTH_SHORT).show()
                    }
                    val dialog = alertDialog.create()
                    dialog.setMessage("Are you sure to update profile")
                    dialog.setTitle("Update")
                    dialog.setIcon(R.drawable.baseline_drive_file_rename_outline_24)
                    dialog.show()
                }

                R.id.logout -> {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setPositiveButton("Yes") { _, _ ->
                        auth.signOut()
                        Toast.makeText(this@MainActivity, "You are logged out!", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                    alertDialog.setNegativeButton("No") { _, _ ->
                        Toast.makeText(this@MainActivity, "Nooo..", Toast.LENGTH_SHORT).show()
                    }
                    val dialog = alertDialog.create()
                    dialog.setMessage("Are you sure to logged out!")
                    dialog.setTitle("Logout")
                    dialog.setIcon(R.drawable.baseline_logout_24)
                    dialog.show()
                }

                R.id.phone -> {
                    val diaCall = Intent(Intent.ACTION_DIAL)
                    diaCall.setData(Uri.parse("tel:6202599162"))
                    startActivity(diaCall)

                }

                R.id.email -> {
                    val emailIntent = Intent(Intent.ACTION_SENDTO)
                    emailIntent.data = Uri.parse("mailto:rk3818487@gmail.com")
                    startActivity(emailIntent)
                }

                R.id.home -> {
                    drawerLayout.close()
                }

            }
            true
        }

        val headerView: View = navigationView.getHeaderView(0)
        val userName: TextView = headerView.findViewById(R.id.getName)
        val userEmail: TextView = headerView.findViewById(R.id.getEmail)
        profileImage = headerView.findViewById(R.id.profileImage)
        val uploadBtn: androidx.appcompat.widget.AppCompatButton =
            headerView.findViewById(R.id.uploadBtn)
        val uploadImage: ImageView = headerView.findViewById(R.id.uploadImage)
        uploadImage.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet, null)
            val fabGallery : FloatingActionButton= bottomSheetView.findViewById(R.id.fabGallery)
            val fabCamera : FloatingActionButton = bottomSheetView.findViewById(R.id.fabCamera)

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
            drawerLayout.close()
            fabGallery.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Pick your image to upload"), 22)
                drawerLayout.open()
            }
            fabCamera.setOnClickListener{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,CAMERA_REQUEST)
                bottomSheetDialog.hide()
            }

        }

//        uploadBtn.setOnClickListener {
//            uploadImage()
//        }
        auth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()
//        get user data from firestore
        db.collection("users").document(user.toString()).get()
            .addOnSuccessListener { documents ->
                if (documents.exists()) {
                    val userData = documents.toObject(Users::class.java)
                    userName.text = userData?.u_name.toString()
                    userEmail.text = userData?.u_email.toString()
                    Glide.with(this).load(Uri.parse(userData?.u_image.toString()))
                        .into(profileImage)
                }
            }
//        end
//        get frame layout
        val frameLayout:FrameLayout = findViewById(R.id.frameLayout)
//        get bottomNavigationView
        val bottomNavigationView : BottomNavigationView= findViewById(R.id.bottomNavigationView)
        changeFragment(HomeFragment())
       bottomNavigationView.setOnItemSelectedListener {
           when(it.itemId){
               R.id.bottomHome->changeFragment(HomeFragment())
               R.id.bottomAddProduct->changeFragment(AddFragment())
               R.id.bottomProfile->changeFragment(ProfileFragment())
           }
           true
       }
    }

    // on below line adding on activity result method this method is called when user picks the image.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 22 && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
                profileImage.setImageBitmap(bitmap)
                uploadImage()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        else  if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            val cPhonto =data!!.extras?.get("data") as Bitmap
            profileImage.setImageBitmap(cPhonto)

            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef = storageRef.child(UUID.randomUUID().toString())
            val baos = ByteArrayOutputStream()
            cPhonto.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val uploadTask = imageRef.putBytes(data)
            uploadTask.addOnSuccessListener {

                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val db = FirebaseFirestore.getInstance()
                    val uid = auth.currentUser?.uid
                    db.collection("users").document(uid.toString())
                        .update("u_image", downloadUrl)
                        .addOnSuccessListener {
                            Toast.makeText(this@MainActivity, "update successfully", Toast.LENGTH_SHORT).show()
                        }
                }
            }

        }
    }

    fun uploadImage() {
        if (fileUri != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.setMessage("Uploading your image..")
            progressDialog.setIcon(R.drawable.baseline_sync_24)
            progressDialog.show()

            val ref: StorageReference =
                FirebaseStorage.getInstance().getReference().child(UUID.randomUUID().toString())
            ref.putFile(fileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // After a successful upload, get the download URL
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUri = uri   // download Uri
                        progressDialog.dismiss()

                        val db = FirebaseFirestore.getInstance()
                        val uid = auth.currentUser?.uid
                        db.collection("users").document(uid.toString())
                            .update("u_image", downloadUri)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@MainActivity,
                                    "update successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@MainActivity,
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        Toast.makeText(applicationContext, "Image Uploaded..", Toast.LENGTH_SHORT)
                            .show()

                    }
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Fail to Upload Image..", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }
//    fragment funtion for switch our fragment
        fun changeFragment (fragment: Fragment){
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout,fragment).commit()
        }
}

