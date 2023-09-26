package com.aryan.dinesathi.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aryan.dinesathi.R
import com.aryan.dinesathi.adapter.DashboardRecyclerAdapter
import com.aryan.dinesathi.model.Restaurants

class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DashboardRecyclerAdapter

    val restaurants = arrayListOf<Restaurants>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the toolbar for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        val jsonObjectRequest = object: JsonObjectRequest(Method.GET, url, null, Response.Listener {
            val data = it.getJSONObject("data")
            val success = data.getBoolean("success")
            if (success){
                val resArray = data.getJSONArray("data")
                for (i in 0 until resArray.length()){
                    val restaurantJsonObject = resArray.getJSONObject(i)
                    val restaurantObject = Restaurants(
                        restaurantJsonObject.getString("id").toInt(),
                        restaurantJsonObject.getString("name"),
                        restaurantJsonObject.getString("rating"),
                        restaurantJsonObject.getString("cost_for_one").toInt(),
                        restaurantJsonObject.getString("image_url")
                    )
                    restaurants.add(restaurantObject)
                    recyclerAdapter = DashboardRecyclerAdapter(restaurants, activity as Context)

                    recyclerDashboard.adapter = recyclerAdapter
                    recyclerDashboard.layoutManager = layoutManager
                }
            }
            else {
                Toast.makeText(activity as Context, "Some error occurred!", Toast.LENGTH_SHORT).show()
            }

        }, Response.ErrorListener {
            print("Some error here $it")
        }){
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
}