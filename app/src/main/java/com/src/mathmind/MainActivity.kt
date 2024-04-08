package com.src.mathmind

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.src.mathmind.databinding.ActivityMainBinding
import com.src.mathmind.databinding.AppBarMainBinding
import com.src.mathmind.models.LoginViewState
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
                if (!navController.navigateUp()) {

//        TODO() implement logic for which destination I am and if it is home ask for signing out
                    finish()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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

    fun setShowSignOutVisible(boolean: Boolean){
        showSignOutVisible = boolean
        invalidateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.action_sign_out -> {
                val loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
                //        TODO() implement logic for destination and you can activate sign out button for all pages
                val currentDestination = navController.currentDestination
                loginViewModel.clear()
                setShowSignOutVisible(false)
                navController.navigate(R.id.action_nav_home_to_login)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}


