package org.tensorflow.lite.examples.videoclassification

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import org.tensorflow.lite.examples.videoclassification.custom.CustomDialogView
import org.tensorflow.lite.examples.videoclassification.custom.CustomDialogArgs
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.examples.videoclassification.databinding.ActivityMainBinding
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val navHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_) as NavHostFragment }
    private val navController by lazy { navHostFragment.navController }

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    private lateinit var sharedPreference: SharedPreferences

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
         //set saved language
//        LanguageUtil.setLocale(
//            this.baseContext,
//            //LocalSharedPreference.getInstance(this.applicationContext)
//        )


        super.onCreate(savedInstanceState)

        sharedPreference =
            application.getSharedPreferences("Parcelable", Context.MODE_PRIVATE)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set graph based on user login or not
        val graph =
            if (sharedPreference.getBoolean("loggedIn", false))
                R.navigation.app_graph
            else
                R.navigation.auth

        navController.setGraph(graph)


        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }


    override fun onBackPressed() {

        if (navHostFragment.childFragmentManager.backStackEntryCount < 1) {
            CustomDialogView.show(
                manager = supportFragmentManager,
                args = object : CustomDialogArgs(
                    data = Data(
                        header = getString(R.string.exit),
                        message = getString(R.string.exit_desc),
                        positiveText = getString(R.string.yes),
                        negativeText = getString(R.string.no)
                    )
                ) {
                    override fun onPositive() {
                        super.onPositive()
                        finish()
                    }
                }
            )
            return
        }

        super.onBackPressed()
    }

    private fun exitDialog() {

    }

//    override fun attachBaseContext(newBase: Context?) {
//        super.attachBaseContext(
//            LanguageUtil.setLocale(
//                newBase!!,
//                LocalSharedPreference.getInstance(newBase)
//            )
//        )
//    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (Build.VERSION.SDK_INT in 19..25) {
            //Use you logic to update overrideConfiguration locale
            //your own implementation here;
            val locale = Locale.getDefault()
            overrideConfiguration?.let {
                overrideConfiguration.setLocale(locale)
            }
        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }
}