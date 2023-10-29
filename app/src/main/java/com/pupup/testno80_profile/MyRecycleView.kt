package com.pupup.testno80_profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyRecycleView(val context: android.content.Context, private val listItem:ArrayList<OrderMode2>) : RecyclerView.Adapter<MyRecycleView.ViewHoler>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoler {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycleview_item_design,parent,false)
        return ViewHoler(view)
    }

    override fun getItemCount(): Int {
       return listItem.size
    }

    override fun onBindViewHolder(holder: ViewHoler, position: Int) {
        val itemPosition = listItem[position]
        holder.recy_pro_title.text = itemPosition.o_title
        holder.rec_des.text = itemPosition.o_des

        // Load and display the image using Glide
        Glide.with(context)
            .load(itemPosition.o_image_link)
            .into(holder.rec_image)
    }

    class ViewHoler(itemView: View):RecyclerView.ViewHolder(itemView){
        val recy_pro_title : TextView = itemView.findViewById(R.id.recy_pro_name)
        val rec_image : ImageView = itemView.findViewById(R.id.recy_image)
        val rec_des : TextView = itemView.findViewById(R.id.recy_pro_des)
    }
}