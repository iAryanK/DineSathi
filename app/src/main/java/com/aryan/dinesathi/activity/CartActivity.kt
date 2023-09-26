package com.aryan.dinesathi.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aryan.dinesathi.R
import com.aryan.dinesathi.adapter.CartRecyclerAdapter
import com.aryan.dinesathi.adapter.MenuRecyclerAdapter
import com.aryan.dinesathi.database.OrderEntity
import com.aryan.dinesathi.database.RestaurantDatabase
import com.aryan.dinesathi.model.MenuItems
import com.android.volley.Response
import com.aryan.dinesathi.fragment.MenuFragment
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    //    Declaring variables
    lateinit var toolbar: Toolbar
    lateinit var rlCart: RelativeLayout
    lateinit var txtCartResName: TextView
    lateinit var recyclerCartItems: RecyclerView
    lateinit var btnConfirmOrder: Button
    lateinit var cartRecyclerAdapter: CartRecyclerAdapter
    var orderList = ArrayList<MenuItems>()

    val bundle = intent.getBundleExtra("data")
    var resId : Int = bundle?.getInt("resId", 0)  as Int
    var resName : String = bundle?.getString("resName", "") as String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

//        Initializing variables
        toolbar = findViewById(R.id.toolbar)
        rlCart = findViewById(R.id.rlCart)
        txtCartResName = findViewById(R.id.txtCartResName)
        txtCartResName.text = MenuFragment.resName
        recyclerCartItems = findViewById(R.id.recyclerCartItems)

//        Tool bar set up
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        Cart list set up
        val dbList = GetItemFromDBAsync(applicationContext).execute().get()

        for (element in dbList) {
            orderList.addAll(
                Gson().fromJson(element.foodItems, Array<MenuItems>::class.java).asList()
            )
        }

        if (orderList.isEmpty()) {
            rlCart.visibility = View.GONE
        } else {
            rlCart.visibility = View.VISIBLE
        }

        cartRecyclerAdapter = CartRecyclerAdapter(orderList, this@CartActivity)
        val layoutManager = LinearLayoutManager(this@CartActivity)
        recyclerCartItems.layoutManager = layoutManager
        recyclerCartItems.adapter = cartRecyclerAdapter

        placeOrder()

    }

    fun placeOrder() {
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder)

        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].cost_for_one
        }
        val total = "Place Order (Total Rs. $sum)"
        btnConfirmOrder.text = total

        btnConfirmOrder.setOnClickListener {
            rlCart.visibility = View.INVISIBLE
            sendServerRequest()
        }
    }

    private fun sendServerRequest() {
        val url = "http://13.235.250.119/v2/place_order/fetch_result"
        val queue = Volley.newRequestQueue(this)

        val jsonParams = JSONObject()
        jsonParams.put(
            "user_id",
            this@CartActivity.getSharedPreferences("FoodApp", Context.MODE_PRIVATE)
                .getString("user_id", null) as String
        )
        jsonParams.put(
            "restaurant_id", MenuFragment.resId?.toString() as String
        )

        var sum = 0
        for (i in 0 until orderList.size){
            sum += orderList[i].cost_for_one
        }
        jsonParams.put("totalCost", sum.toString())
        val foodArray = JSONArray()
        for (i in 0 until orderList.size) {
            val foodId = JSONObject()
            foodId.put("food_item_id", orderList[i].id)
            foodArray.put(i, foodId)
        }
        jsonParams.put("food", foodArray)

        val jsonObjectRequest = object: JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
            try{
                val data = it.getJSONObject("data")
                val success = data.getBoolean("success")

                if (success) {
                    val clearCart = ClearDBAsync(applicationContext, resId.toString()).execute().get()
                    MenuRecyclerAdapter.isCartEmpty = true

                    val dialog = Dialog(this@CartActivity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                    dialog.setContentView(R.layout.order_placed_dialog)
//                    dialog.create()
                    dialog.show()
                    dialog.setCancelable(false)
                    val btnOk = findViewById<Button>(R.id.btnOk)
                    btnOk.setOnClickListener {
                        dialog.dismiss()
                        startActivity(Intent(this@CartActivity, DashboardActivity::class.java))
                        ActivityCompat.finishAffinity(this@CartActivity)
                    }
                } else {
                    rlCart.visibility = View.VISIBLE
                    Toast.makeText(this@CartActivity, "Some error occurred!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                rlCart.visibility = View.VISIBLE
                e.printStackTrace()
            }
        }, Response.ErrorListener{
            rlCart.visibility = View.VISIBLE
            Toast.makeText(this@CartActivity, "Volley error occurred!", Toast.LENGTH_SHORT).show()
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "ef602b4288c7af"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    class GetItemFromDBAsync(context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg p0: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }
    }

    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        if  (ClearDBAsync(applicationContext, resId.toString()).execute().get()) {
            MenuRecyclerAdapter.isCartEmpty = true
            onBackPressed()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        ClearDBAsync(applicationContext, resId.toString()).execute().get()
        MenuRecyclerAdapter.isCartEmpty = true
        super.onBackPressed()
    }
}