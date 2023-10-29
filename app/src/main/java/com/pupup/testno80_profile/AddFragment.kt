package com.pupup.testno80_profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID


class AddFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    var fileUri:Uri? = null
    lateinit var o_image : ImageView
    lateinit var title : EditText
    lateinit var des : EditText
    lateinit var price : EditText
     var downloadUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_add, container, false)
        auth = FirebaseAuth.getInstance()
         o_image  = view.findViewById(R.id.o_image)
        o_image.setOnClickListener {
            bottomSheet()
        }
         title  = view.findViewById(R.id.o_title)
         des  = view.findViewById(R.id.o_des)
         price  = view.findViewById(R.id.o_price)
        val orderBtn : Button = view.findViewById(R.id.orderBtn)
        orderBtn.setOnClickListener {
//            Toast.makeText(requireContext(),"Nothign", Toast.LENGTH_SHORT).show()
            uploadImage()
//            addOrder()
        }

        return view
    }

    fun bottomSheet(){
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val layoutView = layoutInflater.inflate(R.layout.bottom_sheet,null)
        val fabGallery : FloatingActionButton = layoutView.findViewById(R.id.fabGallery)
        val fabCamear : FloatingActionButton = layoutView.findViewById(R.id.fabCamera)
        bottomSheetDialog.setContentView(layoutView)
        bottomSheetDialog.show()
        fabGallery.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"Pick your iamge"),22)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 22 && resultCode ==Activity.RESULT_OK && data!=null && data.data !=null){
            fileUri = data.data
            try {
                val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver,fileUri)
                o_image.setImageBitmap(bitmap)
            }
            catch (e:Exception){

            }
        }
    }
//    fun addOrder(){
//        val db = FirebaseFirestore.getInstance()
//        val uid = auth.currentUser?.uid
//        val addData = OrderModel(
//            o_title = title.text.toString(),
//            o_des = des.text.toString(),
//            o_price= price.text.toString().toLong(),
//            o_id =UUID.randomUUID().toString(),
//            s_id = uid.toString()
//        )
//        db.collection("orders").add(addData)
//            .addOnSuccessListener {
//                Toast.makeText(requireContext(), "Add successfully", Toast.LENGTH_SHORT).show()
//        }
//    }

    fun uploadImage() {
        if (fileUri != null) {
            val progressDialog = ProgressDialog(requireContext())
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
                         downloadUri = uri   // download Uri
                        progressDialog.dismiss()

                        val db = FirebaseFirestore.getInstance()
                        val uid = auth.currentUser?.uid
                        val addData = OrderModel(
                            o_title = title.text.toString(),
                            o_des = des.text.toString(),
                            o_price= price.text.toString().toLong(),
                            o_id =UUID.randomUUID().toString(),
                            o_image_link = downloadUri.toString(),
                            s_id = uid.toString()
                        )
                        db.collection("orders").add(addData)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Insert successfully", Toast.LENGTH_SHORT).show()
                                fragmentManager?.beginTransaction()?.replace(R.id.frameLayout,HomeFragment())?.commit()
                            }
                        Toast.makeText(requireContext(), "Image Uploaded..", Toast.LENGTH_SHORT)
                            .show()

                    }
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Fail to Upload Image..", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

}