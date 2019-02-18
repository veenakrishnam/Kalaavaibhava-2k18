package com.veenam.kalaavaibhava2k18.kalaavaibhava2k18

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.LruCache
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, EventsFragment.OnLayoutManagerInteractionListener, HomeFragment.OnViewInteractionListener{

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private lateinit var mMemoryCache: LruCache<String, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8

        mMemoryCache = object: LruCache<String, Bitmap>(cacheSize){
            override fun sizeOf(key: String, value: Bitmap): Int {
                return value.byteCount / 1024
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        changeTheme();

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val profileActivity = Intent(this,ProfileActivity::class.java)
            startActivity(profileActivity)
        }

        val homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, homeFragment).commit()
        setTitle("Home")

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    fun checkCompletedProfile(){
        val sharedPreferences1 = getSharedPreferences("profile_complete_pref", Context.MODE_PRIVATE)
        val profile_phone_number = sharedPreferences1.getString("profile_phone_number", "")

        if (profile_phone_number.equals("")){
            val intent = Intent(this, ProfileCompleteActivity::class.java)
            startActivity(intent)

            finish()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {

            val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
            val headerView = navigationView.getHeaderView(0)

            val navUserName = headerView.findViewById(R.id.nav_user_name) as TextView
            val navUserEmail = headerView.findViewById(R.id.nav_user_email) as TextView
            val navUserImage = headerView.findViewById(R.id.nav_user_image) as ImageView

            navUserName.text = user.displayName
            navUserEmail.text = user.email

            val user_image_url = user.photoUrl.toString()
            val task = DownloadUserImage(navUserImage)
            task.execute(user_image_url)

            checkCompletedProfile()

        } else {
            val googleSignInActivity = Intent(this,GoogleSignInActivity::class.java)
            startActivity(googleSignInActivity)
            finish()
        }
    }

    inner class DownloadUserImage(img : ImageView): AsyncTask<String, Void, Bitmap>() {
        val weakImg = WeakReference<ImageView>(img)
        override fun doInBackground(vararg params: String?): Bitmap {
            var errorresult: Bitmap? = null
            errorresult = Bitmap.createBitmap(50,50, Bitmap.Config.ARGB_8888)

            val result = MyUtility.downloadImageusingHTTPGetRequest(params[0]!!)
            if(result != null){
                mMemoryCache.put(params[0]!!, result)
            }else{
                return errorresult
            }
            return result!!
        }

        // after completing download -> this is changing main UI thread 's View
        override fun onPostExecute(result : Bitmap?) {
            super.onPostExecute(result)
            val img = weakImg.get()
            if(img != null){
                img.setImageBitmap(result)
            }
        }
    }

    override fun onViewClicked(value: String) {
        if (value.equals("Youtube")){
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=f_eBXpZrIbE&feature=youtu.be")
            )
            intent.component = ComponentName("com.google.android.youtube", "com.google.android.youtube.PlayerActivity")

            val manager = applicationContext.getPackageManager()
            val infos = manager.queryIntentActivities(intent, 0)
            if (infos.size > 0) {
                applicationContext.startActivity(intent)
            } else {
                Toast.makeText(applicationContext,"Youtube is not installed in your device", Toast.LENGTH_LONG).show()
            }
        }
        else if (value.equals("Instagram")) {
            val url = "https://www.instagram.com/kalaavaibhava_2k18/"

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
        else if (value.equals("Facebook")){
            val url = "https://www.facebook.com/kalaavaibhava2k18"

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    override fun onItemClicked(eventsData: EventsData) {
        val eventActivity = Intent(this,EventActivity::class.java)
        eventActivity.putExtra("eventId",eventsData.id.toString())
        startActivity(eventActivity)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_profile ->
            {
                val profileActivity = Intent(this,ProfileActivity::class.java)
                startActivity(profileActivity)
                return true
            }
            R.id.action_settings ->
            {
                val settingsActivity = Intent(this,SettingsActivity::class.java)
                startActivity(settingsActivity)
                return true
            }
            R.id.action_signout ->
            {
                Toast.makeText(applicationContext,"Signed Out !!!", Toast.LENGTH_LONG).show()
                auth.signOut()
                updateUI(null)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, homeFragment).commit()
                setTitle("Home")
            }
            R.id.nav_events -> {
                val eventsFragment = EventsFragment.newInstance(1)
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, eventsFragment).commit()
                setTitle("Events")
            }
            R.id.nav_directions -> {
                val mapsIntentUri = Uri.parse("google.navigation:q=Sri Venkateshwara College of Engineering, Bangalore")
                val mapIntent = Intent(Intent.ACTION_VIEW, mapsIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
            R.id.nav_sponsors -> {
                val sponsorsFragment = SponsorsFragment.newInstance(1)
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, sponsorsFragment).commit()
                setTitle("Sponsors")
            }
            R.id.nav_crew -> {
                val crewFragment = CrewFragment.newInstance(1)
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, crewFragment).commit()
                setTitle("Crew")
            }
            R.id.nav_about_developer -> {
                val aboutFragment = AboutDeveloperFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, aboutFragment).commit()
                setTitle("About Developer")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun changeTheme() {
        val themePreferences = applicationContext.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
        val theme = themePreferences.getString("theme", "")
        when (theme) {
            "1" -> setTheme(R.style.AppTheme1)
            "2" -> setTheme(R.style.AppTheme2)
            "3" -> setTheme(R.style.AppTheme3)
            "4" -> setTheme(R.style.AppTheme4)
            "5" -> setTheme(R.style.AppTheme5)
            "6" -> setTheme(R.style.AppTheme6)
            "7" -> setTheme(R.style.AppTheme7)
            "8" -> setTheme(R.style.AppTheme8)
        }
    }
}
