package com.veenam.kalaavaibhava2k18.kalaavaibhava2k18

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_event.*
import java.lang.ref.WeakReference


class EventActivity : AppCompatActivity() {

    val BASE_URL = "**your url comes here**"

    private lateinit var mMemoryCache: LruCache<String, Bitmap>

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

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

        setContentView(R.layout.activity_event)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val eventId = intent.getStringExtra("eventId")

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]

        val currentUser = auth.currentUser

        var user_email: String? = ""
        if (currentUser != null) {

            user_email = currentUser.email

        }

        val url = BASE_URL + "event.php?id=" + eventId
        Log.i("Event ID",eventId)
        val task = DownloadEventInfo()
        task.execute(url)

        val url_verfiy = BASE_URL + "eventregistrationverify.php?event_id=" + eventId + "&email=" + user_email
        Log.i("url verify",url_verfiy)
        val task_verify = VerifyRegisteredUser()
        task_verify.execute(url_verfiy)

        register_fab.setOnClickListener { view ->
            val url_register = BASE_URL + "eventregistration.php?event_id=" + eventId + "&email=" + user_email
            Log.i("Event ID",eventId)
            val task_register = RegisterUser()
            task_register.execute(url_register)
        }
    }

    inner class DownloadEventInfo: AsyncTask<String, Void, String>() {
        override fun doInBackground (vararg params: String?): String? {
            val result = MyUtility.downloadJSONusingHTTPGetRequest(params[0]!!)
            Log.i("Event Info",result)
            return result!!
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val event = Gson().fromJson(result, EventData::class.java)

            setTitle(event.title)

            val eventImageUrl = event.imageUrl
            val task = DownloadEventImage(eventImage)
            task.execute(eventImageUrl)

            eventDate.text = event.date
            eventTime.text = event.time
            eventQuote.text = event.quote
            eventRules.text = event.rules
            eventStudentCoordinators.text = event.student_co_ordinators
            eventFacultyCoordinators.text = event.faculty_co_ordinators
        }
    }

    inner class DownloadEventImage(img : ImageView): AsyncTask<String, Void, Bitmap>() {
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

    inner class VerifyRegisteredUser: AsyncTask<String, Void, String>() {
        override fun doInBackground (vararg params: String?): String? {
            val result = MyUtility.downloadJSONusingHTTPGetRequest(params[0]!!)
            Log.i("User Info",result)
            return result!!
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val user_info = Gson().fromJson(result, VerifyRegisteredUserData::class.java)

            if (user_info.user_email!!.equals("")){

            }
            else{
                register_fab.setImageResource(R.drawable.ic_outline_beenhere_24px)
            }
        }
    }

    inner class RegisterUser: AsyncTask<String, Void, String>() {
        override fun doInBackground (vararg params: String?): String? {
            val result = MyUtility.downloadJSONusingHTTPGetRequest(params[0]!!)
            Log.i("Register Info",result)
            return result!!
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val register_info = Gson().fromJson(result, HttpMessage::class.java)

            if (register_info.message!!.equals("User already registered for this event")){
                Toast.makeText(applicationContext,"You have already registered for this event", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(applicationContext,"Successfully registered for this event", Toast.LENGTH_LONG).show()
                register_fab.setImageResource(R.drawable.ic_outline_beenhere_24px)
            }
        }
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
