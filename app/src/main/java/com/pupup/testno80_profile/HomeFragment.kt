package com.pupup.testno80_profile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    lateinit var listItem : ArrayList<OrderMode2>
    lateinit var myAdapter: MyRecycleView
    lateinit var auth: FirebaseAuth
    @SuppressLint("MissingInflatedId")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recycleView : RecyclerView = view.findViewById(R.id.recycleView)
        recycleView.layoutManager = GridLayoutManager(requireContext(),2)
        auth=FirebaseAuth.getInstance()
        listItem = arrayListOf()
        myAdapter = MyRecycleView(requireContext(),listItem)
        val db = FirebaseFirestore.getInstance()
        db.collection("orders").get()
            .addOnSuccessListener { task->
                for(document in task){
                    val data = document.data
                    val userData =OrderMode2(data["o_title"].toString(),data["o_image_link"].toString(),data["o_des"].toString())
                    listItem.add(userData)
                    myAdapter.notifyDataSetChanged()
                }
            }

     recycleView.adapter = myAdapter

        return view
    }

}