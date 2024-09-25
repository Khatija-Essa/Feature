package com.example.nobrainer2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nobrainer2.OwnQuiz.MyQuiz
import com.example.nobrainer2.OwnQuiz.OwnQuizActivity
import com.example.nobrainer2.databinding.ActivityMainBinding
import com.example.nobrainer2.otherGames.OtherGamesActivity

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {

            createbtn.setOnClickListener {
                val intent = Intent(this@MainActivity, OwnQuizActivity::class.java)
                startActivity(intent)
            }
            myQuiz.setOnClickListener{
                val intent = Intent(this@MainActivity, MyQuiz::class.java)
                startActivity(intent)
            }

            btnOtherGames.setOnClickListener{
                val intent = Intent(this@MainActivity, OtherGamesActivity::class.java)
                startActivity(intent)
            }
        }


    }
}