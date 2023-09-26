package com.aryan.dinesathi.fragment

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aryan.dinesathi.R
import com.aryan.dinesathi.activity.CartActivity
import com.aryan.dinesathi.adapter.MenuRecyclerAdapter
import com.aryan.dinesathi.database.OrderEntity
import com.aryan.dinesathi.database.RestaurantDatabase
import com.aryan.dinesathi.model.MenuItems
import com.google.gson.Gson

class MenuFragment : Fragment() {

    lateinit var recyclerMenu: RecyclerView
    lateinit var layoutManager: LayoutManager
    lateinit var recyclerAdapter: MenuRecyclerAdapter
    var resId: Int? = 0

    val menuList = arrayListOf<MenuItems>()
    val orderList = arrayListOf<MenuItems>()

    lateinit var goToCart: Button

    companion object {
        var resId: Int? = 0
        var resName: String? = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the toolbar for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        recyclerMenu = view.findViewById(R.id.recyclerMenu)
        layoutManager = LinearLayoutManager(activity)
        resId = arguments?.getInt("resId", 0)

        goToCart = view.findViewById(R.id.btnGoToCart) as Button
        goToCart.setOnClickListener {
            proceedToCart()
        }

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.GET, url + resId, null, Response.Listener {
                try{
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val menuArray = data.getJSONArray("data")
                        for (i in 0 until menuArray.length()) {
                            val menuJsonObject = menuArray.getJSONObject(i)
                            val menuObject = MenuItems(
                                menuJsonObject.getString("id").toInt(),
                                menuJsonObject.getString("name"),
                                menuJsonObject.getString("cost_for_one").toInt(),
                            )
                            menuList.add(menuObject)
                            recyclerAdapter = MenuRecyclerAdapter(
                                activity as Context,
                                menuList,
                                object : MenuRecyclerAdapter.OnItemClickListener {

                                    override fun onAddItemClick(menuItem: MenuItems) {
                                        orderList.add(menuItem)
                                        if (orderList.size > 0) {
                                            goToCart.visibility = View.VISIBLE
                                            MenuRecyclerAdapter.isCartEmpty = false
                                        }
                                    }

                                    override fun onRemoveItemClick(menuItem: MenuItems) {
                                        orderList.remove(menuItem)
                                        if (orderList.isEmpty()) {
                                            goToCart.visibility = View.GONE
                                            MenuRecyclerAdapter.isCartEmpty = true
                                        }
                                    }
                                })
                            recyclerMenu.adapter = recyclerAdapter
                            recyclerMenu.layoutManager = layoutManager
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {
                print("Some error here $it")
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "ef602b4288c7af"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)

        return view
    }

    fun proceedToCart() {
        val gson = Gson()
        val foodItems = gson.toJson(orderList)
        val async = ItemsOfCart(activity as Context, resId.toString(), foodItems, 1).execute()
        val result = async.get()

        if (result) {
            val data = Bundle()
            data.putInt("resId", resId as Int)
            data.putString("resName", resName)
            val intent = Intent(activity, CartActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        } else {
            Toast.makeText(activity as Context, "Some error occurred!", Toast.LENGTH_SHORT).show()
        }
    }

    class ItemsOfCart(
        context: Context,
        private val restaurantId: String,
        private val foodItems: String,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteOrder(OrderEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}