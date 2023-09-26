package com.aryan.dinesathi.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aryan.dinesathi.R
import com.aryan.dinesathi.model.MenuItems

class MenuRecyclerAdapter(
    val context: Context,
    val menuList: ArrayList<MenuItems>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {

    companion object{
        var isCartEmpty = true
    }

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSNo: TextView = view.findViewById(R.id.txtSNo)
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemCost: TextView = view.findViewById(R.id.txtItemCost)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
        val btnRemoveFromCart: Button = view.findViewById(R.id.btnRemoveFromCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)
        return MenuViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    interface OnItemClickListener {
        fun onAddItemClick(menuItem: MenuItems)
        fun onRemoveItemClick(menuItem: MenuItems)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val eachMenuItem = menuList[position]
        holder.txtSNo.text = (position + 1).toString()
        holder.txtItemName.text = eachMenuItem.name
        holder.txtItemCost.text = eachMenuItem.cost_for_one.toString()

        holder.btnAddToCart.setOnClickListener {
            holder.btnAddToCart.visibility = View.GONE
            holder.btnRemoveFromCart.visibility = View.VISIBLE
            listener.onAddItemClick(eachMenuItem)
        }

        holder.btnRemoveFromCart.setOnClickListener {
            holder.btnRemoveFromCart.visibility = View.GONE
            holder.btnAddToCart.visibility = View.VISIBLE
            listener.onRemoveItemClick(eachMenuItem)
        }
    }
}