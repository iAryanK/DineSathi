package com.aryan.dinesathi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aryan.dinesathi.R
import com.aryan.dinesathi.model.MenuItems

class CartRecyclerAdapter(val cartList: ArrayList<MenuItems>, val context: Context) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_single_row, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val eachCartItem = cartList[position]
        holder.txtCartItemName.text = eachCartItem.name
        holder.txtCartPrice.text = eachCartItem.cost_for_one.toString()
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCartItemName : TextView = view.findViewById(R.id.txtCartItemName)
        val txtCartPrice : TextView = view.findViewById(R.id.txtCartPrice)
    }

}