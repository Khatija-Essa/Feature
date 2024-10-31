package com.example.nobrainer2

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nobrainer2.databinding.ActivitySettingsBinding
import java.util.Locale

class ActivitySettings : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.app_name)

        binding.apply {
            buttonLanguages.setOnClickListener {
                showLanguageSelectionDialog()
            }

            buttonLightMode.setOnClickListener {
                val intent = Intent(this@ActivitySettings, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "Zulu")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change the Language")
        builder.setSingleChoiceItems(languages, -1) { dialog, which ->
            val selectedLanguage = if (which == 0) "en" else "zu"
            setLocale(selectedLanguage)
            dialog.dismiss()

            // Restart the app to apply the language change globally
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        builder.create().show()
    }

    private fun setLocale(languageCode: String) {
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", languageCode)
        editor.apply()
        recreate()
    }
}