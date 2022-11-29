package com.donaboyev.tictactoe

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.donaboyev.tictactoe.Util.NIGHT_MODE
import com.donaboyev.tictactoe.Util.SHARED_PREF_MODE
import com.donaboyev.tictactoe.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var background: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences(SHARED_PREF_MODE, MODE_PRIVATE)
        val isNightMode = prefs.getBoolean(NIGHT_MODE, true)
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding.ivRunningCat.setImageResource(R.drawable.tic_tac_toe_loader)
        val cat = binding.ivRunningCat.drawable as AnimationDrawable
        cat.start()

        background = Thread {
            try {
                Thread.sleep(5000)
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        background?.start()
    }

    override fun onPause() {
        super.onPause()
        background?.interrupt()
    }
}