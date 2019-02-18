package com.veenam.kalaavaibhava2k18.kalaavaibhava2k18

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile_complete.*
import java.lang.ref.WeakReference

class ProfileCompleteActivity : AppCompatActivity() {

    val BASE_URL = "**your url comes here**"

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    var name: String? = ""
    var email: String? = ""
    var phoneNumber: String? = ""
    var college: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_complete)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]

        checkCompletedProfile()

    }

    fun checkCompletedProfile(){
        val sharedPreferences1 = getSharedPreferences("profile_complete_pref", Context.MODE_PRIVATE)
        val profile_phone_number = sharedPreferences1.getString("profile_phone_number", "")

        if (profile_phone_number.equals("")){

        }
        else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }
    }

    fun completeProfile(view: View){
        val currentUser = auth.currentUser

        name = currentUser!!.displayName
        email = currentUser!!.email

        phoneNumber = profileEditPhoneNumber.text.toString()
        college = profileEditCollege.text.toString()

        var flag: Int = 1
        if (phoneNumber!!.length < 10){
            flag = 0

            Toast.makeText(applicationContext,"Phone Number can't be less than 10 digits",Toast.LENGTH_LONG).show()
        }

        if (college!!.length < 2){
            flag = 0

            Toast.makeText(applicationContext,"Please enter the correct College Name",Toast.LENGTH_LONG).show()
        }

        if (flag == 1){
            val user = "{\n" +
                    "      \"name\": \"User Name\",\n" +
                    "      \"email\": \"User email\",\n" +
                    "      \"phoneNumber\": \"User phoneNumber\",\n" +
                    "      \"college\": \"User college\"\n" +
                    "    }".trimIndent()

            var userObject: ProfileData = Gson().fromJson(user,ProfileData::class.java)

            userObject.name = name
            userObject.email = email
            userObject.phoneNumber = phoneNumber
            userObject.college = college

            Log.i("name  ", userObject.name)
            Log.i("email  ", userObject.email)
            Log.i("phoneNumber  ", userObject.phoneNumber)
            Log.i("college  ", userObject.college)

            val url = BASE_URL + "createprofile.php?name=" + name + "&email=" + email + "&phoneNumber=" + phoneNumber + "&college=" + college
            val userpostdata = Gson().toJson(userObject)

            Log.i("Data  ", userpostdata)
            Log.i("Url", url)

            val task = SendProfileData(userObject)
            task.execute(url)
        }
    }

    inner class SendProfileData(data: ProfileData): AsyncTask<String, Void, String>(){
        val weakData = WeakReference<ProfileData>(data)
        override fun doInBackground(vararg params: String?): String {
            var result :String? = " "
            result = MyUtility.downloadJSONusingHTTPGetRequest(params[0]!!)
            //Log.i("Events ",result)
            return result!!
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val sharedPreferences_eng = getSharedPreferences("profile_complete_pref", Context.MODE_PRIVATE)
            val editor = sharedPreferences_eng.edit()
            editor.putString("profile_name", name)
            editor.putString("profile_email", email)
            editor.putString("profile_phone_number", phoneNumber)
            editor.putString("profile_college", college)
                .apply()

            val intent = Intent(this@ProfileCompleteActivity, MainActivity::class.java)
            startActivity(intent)

            finish()
        }

    }

    inner class SendPostRequest: AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg p0: String?): String? {
            val message = MyUtility.sendHttPostRequest(p0[0]!!,p0[1]!!)

            Log.i("Result", message)
            return message
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            //val response = Gson().fromJson(result, HttpMessage::class.java)

            val sharedPreferences_eng = getSharedPreferences("profile_complete_pref", Context.MODE_PRIVATE)
            val editor = sharedPreferences_eng.edit()
            editor.putString("profile_name", name)
            editor.putString("profile_email", email)
            editor.putString("profile_phone_number", phoneNumber)
            editor.putString("profile_college", college)
                .apply()

            val intent = Intent(this@ProfileCompleteActivity, MainActivity::class.java)
            startActivity(intent)

            finish()
        }
    }
}
