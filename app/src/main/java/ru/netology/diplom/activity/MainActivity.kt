package ru.netology.diplom.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.utils.MediaLifecycleObserver
import ru.netology.diplom.viewmodel.AuthViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiquration: AppBarConfiguration

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiquration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiquration)

        var previousMenuProvider: MenuProvider? = null
        authViewModel.data.observe(this) {
            previousMenuProvider?.let(::removeMenuProvider)

            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_auth, menu)
                    menu.setGroupVisible(R.id.unauthorization, !authViewModel.authorized)
                    menu.setGroupVisible(R.id.authorization, authViewModel.authorized)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.login -> {
                            findNavController(R.id.fragmentContainerView).navigate(R.id.loginFragment)
                            true
                        }
                        R.id.register -> {
                            findNavController(R.id.fragmentContainerView).navigate(R.id.registerFragment)
                            true
                        }
                        R.id.logout -> {
                            findNavController(R.id.fragmentContainerView).navigate(R.id.logoutFragment)
                            true
                        }
                        else -> false
                    }
            }.also {
                previousMenuProvider = it
            })
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}