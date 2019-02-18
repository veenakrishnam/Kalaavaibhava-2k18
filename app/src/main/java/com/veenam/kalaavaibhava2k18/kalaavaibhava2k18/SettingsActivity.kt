package com.veenam.kalaavaibhava2k18.kalaavaibhava2k18

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*



class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeTheme()
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        //Change the theme text in settings layout
        val themePreferences = applicationContext.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)

        val theme_name = themePreferences.getString("theme_name", "")
        val themeTextView = findViewById<View>(R.id.settings_theme_sub) as TextView
        themeTextView.text = theme_name
        super.onStart()
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

    fun settingsOption(view: View) {
        val id = view.getId()
        when (id) {
            R.id.settings_theme -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Theme Color")
                val inflater = this.layoutInflater
                val dialogView = inflater.inflate(R.layout.theme_dialog, null)
                builder.setView(dialogView)
                builder.setNegativeButton("CLOSE", DialogInterface.OnClickListener { dialog, which -> {}})
                builder.create()
                builder.show()
            }
        }
    }

    fun changeThemeColor(view: View) {
        val themePreferences = applicationContext.getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
        val id = view.id
        when (id) {
            R.id.theme1 -> {
                themePreferences.edit().putString("theme", "1").apply()
                themePreferences.edit().putString("theme_name", "Blue").apply()
                restartApplication()
            }
            R.id.theme2 -> {
                themePreferences.edit().putString("theme", "2").apply()
                themePreferences.edit().putString("theme_name", "Violet").apply()
                restartApplication()
            }
            R.id.theme3 -> {
                themePreferences.edit().putString("theme", "3").apply()
                themePreferences.edit().putString("theme_name", "Red").apply()
                restartApplication()
            }
            R.id.theme4 -> {
                themePreferences.edit().putString("theme", "4").apply()
                themePreferences.edit().putString("theme_name", "Cement").apply()
                restartApplication()
            }
            R.id.theme5 -> {
                themePreferences.edit().putString("theme", "5").apply()
                themePreferences.edit().putString("theme_name", "White").apply()
                restartApplication()
            }
            R.id.theme6 -> {
                themePreferences.edit().putString("theme", "6").apply()
                themePreferences.edit().putString("theme_name", "Cyan").apply()
                restartApplication()
            }
            R.id.theme7 -> {
                themePreferences.edit().putString("theme", "7").apply()
                themePreferences.edit().putString("theme_name", "Green").apply()
                restartApplication()
            }
            R.id.theme8 -> {
                themePreferences.edit().putString("theme", "8").apply()
                themePreferences.edit().putString("theme_name", "Gold").apply()
                restartApplication()
            }
        }
    }

    fun restartApplication() {
        val i = baseContext.packageManager
            .getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)

        //Comment the next following lines
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(settingsIntent)
        finish()
    }

}
