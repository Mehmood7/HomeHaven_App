package com.example.homehaven
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.homehaven.ui.gallery.homeGallery
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso

class drawerActivity : AppCompatActivity(), homeGallery {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var db:dataStore
    private lateinit var sharedPref: SharedPreferences
    private lateinit var dpView: ImageView
    private lateinit var nameView: TextView
    private lateinit var mailView: TextView
    private lateinit var signOut: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val hView =  navView.getHeaderView(0)
        dpView = hView.findViewById(R.id.nav_dp_iv)
        nameView = hView.findViewById(R.id.nav_name_tv)
        mailView = hView.findViewById(R.id.nav_mail_tv)
        val menuNav:Menu = navView.getMenu();
        signOut = menuNav.findItem(R.id.nav_sign_out)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_network), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        sharedPref = getSharedPreferences("prefs", Context.MODE_PRIVATE)?:return
        db = dataStore(applicationContext)

        val email = sharedPref.getString("email", "")
        val name = sharedPref.getString("name", "")

        val imageUri = "https://homehaven.website/api/getimage?email=" + email
        Picasso.get().isLoggingEnabled = true
        Picasso.get().load(imageUri).resize(150, 150)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .transform(PicassoCircleTransformation())
            .centerCrop().into(dpView)

        mailView.text = email
        nameView.text = name

        signOut.setOnMenuItemClickListener {
            with (sharedPref.edit()) {
                putString("token", "")
                commit()
            }
            val i = Intent(this, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            true
        }

    }

    override fun getDatastore(): dataStore {
        return db;
    }

    override fun getSharedPref(): SharedPreferences {
        return sharedPref;
    }

    override fun doToast(str:String){
        Toast.makeText(applicationContext,str, Toast.LENGTH_SHORT).show();
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.drawer, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}