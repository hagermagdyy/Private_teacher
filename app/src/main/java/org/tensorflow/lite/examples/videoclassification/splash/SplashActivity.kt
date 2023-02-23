package org.tensorflow.lite.examples.videoclassification.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.examples.videoclassification.MainActivity
import org.tensorflow.lite.examples.videoclassification.databinding.SplashActivityBinding

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var splashBinding: SplashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        // Handler().postDelayed({
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashActivity , MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }, 3000)
    }
}
