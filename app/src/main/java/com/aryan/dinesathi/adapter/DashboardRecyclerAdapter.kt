package com.aryan.dinesathi.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.aryan.dinesathi.R
import com.aryan.dinesathi.fragment.MenuFragment
import com.aryan.dinesathi.model.Restaurants
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val restaurants: ArrayList<Restaurants>, val context: Context) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtCostForOne : TextView = view.findViewById(R.id.txtCostForOne)
        val txtRestaurantRating : TextView = view.findViewById(R.id.txtRestaurantRating)
        val imgRestaurantThumbnail : ImageView = view.findViewById(R.id.imgRestaurantThumbnail)
        val cardRestaurant: CardView = view.findViewById(R.id.cardRestaurant)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)
        return DashboardViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val eachRes = restaurants[position]
        holder.txtRestaurantName.text = eachRes.name
        holder.txtRestaurantRating.text = eachRes.rating
        val costForOne = "₹ ${eachRes.cost_for_one}/person"
        holder.txtCostForOne.text = costForOne
        Picasso.get().load(eachRes.image_url).into(holder.imgRestaurantThumbnail)

        holder.cardRestaurant.setOnClickListener {
            val fragment = MenuFragment()
            val args = Bundle()
            args.putInt("resId", eachRes.id)
            fragment.arguments = args
            (context as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.frame, fragment).commit()
            (context as AppCompatActivity).supportActionBar?.title = holder.txtRestaurantName.text.toString()
        }
    }
}