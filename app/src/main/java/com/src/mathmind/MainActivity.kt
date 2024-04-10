package com.src.mathmind

import ShowDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.src.mathmind.databinding.ActivityMainBinding
import com.src.mathmind.databinding.AppBarMainBinding
import com.src.mathmind.ui.login.LoginViewModel
import com.src.mathmind.utils.IdlingTool

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var showSignOutVisible = false
    private var idlingResource: IdlingTool? = null
    lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val appBarMain: AppBarMainBinding = binding.appBarMain
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_login)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_signOn, R.id.nav_login, R.id.nav_home
            ), drawerLayout
        )

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentDestination = navController.currentDestination
                if (currentDestination?.id == R.id.nav_home) {
                    ShowDialog().create(
                        this@MainActivity,
                        getString(R.string.sign_on_header),
                        getString(R.string.sign_out_description),
                        getString(android.R.string.ok),
                        getString(android.R.string.cancel),
                        onPositiveClick = {
                            logOut()
                        }
                    )
                }else if (currentDestination?.id == R.id.nav_login) {

                } else {
                    if (!navController.navigateUp()) {

                        finish()
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        scoreTopList = CallService().getScoreBoard()
//        updateNavMenuForScoreboard()
        navView.menu.add("1 - added to menu1")
        navView.menu.add("1 - added to menu2")
        navView.menu.add("1 - added to menu3")
        navView.menu.add("1 - added to menu4")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main, menu)
        setVisibilityOfSignOut(menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        setVisibilityOfSignOut(menu)
        super.onPrepareOptionsMenu(menu)
        return true
    }

    private fun setVisibilityOfSignOut(menu: Menu?) {

        val signOut = menu?.findItem(R.id.action_sign_out)
        signOut?.isVisible = showSignOutVisible
    }

    override fun onSupportNavigateUp(): Boolean {

        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun getIdlingTool(): IdlingTool {
        if (this.idlingResource == null) idlingResource = IdlingTool()
        return idlingResource as IdlingTool
    }

    fun setShowSignOutVisible(boolean: Boolean) {
        showSignOutVisible = boolean
        invalidateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        val currentDestination = navController.currentDestination
        return when (item.itemId) {
            R.id.action_sign_out -> {
                if(currentDestination?.id == R.id.nav_guesser)
                    logOut(R.id.action_nav_guesser_to_login)
                else if(currentDestination?.id == R.id.nav_feedbacker)
                    logOut(R.id.action_nav_feedbacker_to_login)
                else
                    logOut()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logOut(destination: Int = R.id.action_nav_home_to_login) {
        val loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        loginViewModel.clear()
        setShowSignOutVisible(false)
        navController.navigate(destination)
    }
}


