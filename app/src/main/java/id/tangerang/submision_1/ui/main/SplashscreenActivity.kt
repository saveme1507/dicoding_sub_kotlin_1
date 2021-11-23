package id.tangerang.submision_1.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import id.tangerang.submision_1.R
import id.tangerang.submision_1.ui.searchuser.SearchUsersActivity
import id.tangerang.submision_1.databinding.ActivitySplashscreenBinding

class SplashscreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashscreenBinding
    private val context = this@SplashscreenActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivLogo.startAnimation(
            AnimationUtils.loadAnimation(context, R.anim.slide_down)
        )

        Handler(Looper.getMainLooper()).postDelayed({
            binding.progress.visibility = View.VISIBLE
            binding.progress.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.fade_in)
            )
        }, 1000)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(context, SearchUsersActivity::class.java))
            finish()
        }, 3000)
    }
}