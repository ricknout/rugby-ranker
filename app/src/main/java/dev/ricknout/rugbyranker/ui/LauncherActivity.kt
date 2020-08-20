package dev.ricknout.rugbyranker.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dev.chrisbanes.insetter.setEdgeToEdgeSystemUiFlags
import dev.ricknout.rugbyranker.R
import dev.ricknout.rugbyranker.databinding.ActivityLauncherBinding

class LauncherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAvd()
        setupEdgeToEdge()
    }

    private fun setupAvd() {
        val avd = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_rugby_ranker_56dp)!!
        binding.icon.setImageDrawable(avd)
        avd.registerAnimationCallback(
            object : Animatable2Compat.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
                    finish()
                }
            }
        )
        avd.start()
    }

    private fun setupEdgeToEdge() {
        binding.root.setEdgeToEdgeSystemUiFlags()
    }
}
