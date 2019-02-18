package com.veenam.kalaavaibhava2k18.kalaavaibhava2k18

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.content_profile.*
import java.lang.ref.WeakReference

class ProfileActivity : AppCompatActivity() {

    val BASE_URL = "**your url comes here**"

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
        setContentView(R.layout.activity_profile)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]

        val sharedPreferences1 = getSharedPreferences("profile_complete_pref", Context.MODE_PRIVATE)
        val profile_email = sharedPreferences1.getString("profile_email", "")

        if (profile_email.equals("")){

        }
        else{
            val url = BASE_URL + "user.php?email=" + profile_email
            Log.i("Email",profile_email)
            val task = DownloadUserInfo()
            task.execute(url)
        }

    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {

            profileName.text = user.displayName
            profileEmail.text = user.email

            val profile_image_url = user.photoUrl.toString()
            val task = DownloadProfileImage(profileImage)
            task.execute(profile_image_url)

        } else {
            val googleSignInActivity = Intent(this,GoogleSignInActivity::class.java)
            startActivity(googleSignInActivity)
            finish()
        }
    }

    inner class DownloadProfileImage(img : ImageView): AsyncTask<String, Void, Bitmap>() {
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

    inner class DownloadUserInfo: AsyncTask<String, Void, String>() {
        override fun doInBackground (vararg params: String?): String? {
            val result = MyUtility.downloadJSONusingHTTPGetRequest(params[0]!!)
            Log.i("User",result)
            return result!!
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val userdata = Gson().fromJson(result, ProfileData::class.java)

            profileNumber.text = userdata.phoneNumber
            profileCollege.text = userdata.college
        }
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
