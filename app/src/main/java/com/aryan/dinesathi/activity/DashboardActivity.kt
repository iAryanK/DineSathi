package com.aryan.dinesathi.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.aryan.dinesathi.R
import com.aryan.dinesathi.fragment.DashboardFragment
import com.aryan.dinesathi.fragment.FaqsFragment
import com.aryan.dinesathi.fragment.FavouritesFragment
import com.aryan.dinesathi.fragment.HistoryFragment
import com.aryan.dinesathi.fragment.ProfileFragment
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    var previousMenuItem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)

        openDashboard()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null)
                previousMenuItem?.isChecked = false
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.home -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                }

                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, ProfileFragment())
                        .commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }

                R.id.favouriteRestaurants -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouritesFragment()).commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }

                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, HistoryFragment())
                        .commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }

                R.id.faqs -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame, FaqsFragment())
                        .commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }

                R.id.logout -> {
                    val dialog = AlertDialog.Builder(this@DashboardActivity as Context)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to log out?")
                    dialog.setPositiveButton("Yes"){ _, _ ->
                        sharedPreferences = getSharedPreferences(
                            getString(R.string.dinesathiPreferences),
                            Context.MODE_PRIVATE
                        )
                        sharedPreferences.edit().clear().apply()
                        Toast.makeText(
                            this@DashboardActivity,
                            "Logged-Out Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                        finish()
                    }
                    dialog.setNegativeButton("No"){ _, _ ->

                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }

        setUpToolbar()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@DashboardActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START)
        return super.onOptionsItemSelected(item)
    }

    fun openDashboard(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, DashboardFragment()).commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when(frag){
            !is DashboardFragment -> openDashboard()

            else -> super.onBackPressed()
        }
    }
}