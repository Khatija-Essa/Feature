package com.example.nobrainer2.otherGames

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nobrainer2.OwnQuiz.OwnQuizActivity
import com.example.nobrainer2.R
import com.example.nobrainer2.databinding.ActivityMainBinding
import com.example.nobrainer2.databinding.ActivityOtherGamesBinding

class OtherGamesActivity : AppCompatActivity() {
    private var binding: ActivityOtherGamesBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherGamesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {
            btnPlaySnake.setOnClickListener {
                val intent = Intent(this@OtherGamesActivity, snake_game::class.java)
                startActivity(intent)
            }
        }
    }
}