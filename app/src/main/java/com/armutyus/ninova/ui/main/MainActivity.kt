package com.armutyus.ninova.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.armutyus.ninova.MobileNavigationDirections
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentLocalBook
import com.armutyus.ninova.constants.Cache.currentShelf
import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.constants.Constants.DETAILS_EXTRA
import com.armutyus.ninova.constants.Constants.FROM_DETAILS_TO_NOTES_EXTRA
import com.armutyus.ninova.constants.Constants.MAIN_SHARED_PREF
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.ActivityMainBinding
import com.armutyus.ninova.fragmentfactory.NinovaFragmentFactoryEntryPoint
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.armutyus.ninova.ui.splash.SplashViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Named(Constants.LOGIN_INTENT)
    @Inject
    lateinit var loginIntent: Intent

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val booksViewModel by viewModels<BooksViewModel>()
    private val shelvesViewModel by viewModels<ShelvesViewModel>()
    private val splashViewModel by viewModels<SplashViewModel>()
    private val sharedPreferences: SharedPreferences
        get() = this.getSharedPreferences(MAIN_SHARED_PREF, Context.MODE_PRIVATE)
    private var themePreferences: SharedPreferences? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        val entryPoint = EntryPointAccessors.fromActivity(
            this,
            NinovaFragmentFactoryEntryPoint::class.java
        )
        supportFragmentManager.fragmentFactory = entryPoint.getFragmentFactory()

        installSplashScreen()
        checkUserThemePreference()

        super.onCreate(savedInstanceState)

        if (!splashViewModel.isUserAuthenticated) {
            startActivity(loginIntent)
            finish()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        auth = FirebaseAuth.getInstance()

        if (sharedPreferences.getBoolean(
                "first_time",
                true
            ) && !auth.currentUser!!.isAnonymous
        ) {
            fetchBooks()
            fetchShelves()
            fetchCrossRefs()
        }

        if (currentLocalBook?.bookId != null) {
            when (intent.getStringExtra(DETAILS_EXTRA)) {
                currentLocalBook!!.bookId -> {
                    val action =
                        MobileNavigationDirections.actionMainToBookToShelfFragment(currentLocalBook!!.bookId)
                    navController.navigate(action)
                }

                FROM_DETAILS_TO_NOTES_EXTRA -> {
                    val action = MobileNavigationDirections.actionMainToBookUserNotesFragment()
                    navController.navigate(action)
                }

                else -> {}
            }
        }

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.settings_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {

                    R.id.menu_search -> {
                        navController.navigate(R.id.action_main_to_search)
                    }

                    R.id.settings -> {
                        navController.navigate(R.id.action_main_to_settings)
                    }

                }
                return true
            }
        })

        destinationChangeListener(navView)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_books, R.id.navigation_discovery, R.id.navigation_shelves
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        bottomNavItemChangeListener(navView)

    }

    private fun bottomNavItemChangeListener(navView: BottomNavigationView) {
        navView.setOnItemSelectedListener { item ->
            if (item.itemId != navView.selectedItemId) {
                navController.navigate(item.itemId)
            }
            true
        }

        navView.setOnItemReselectedListener { selectedItem ->
            if (selectedItem.itemId == navView.selectedItemId) {
                navController.navigate(navView.selectedItemId)
            }
        }
    }

    private fun checkUserThemePreference() {
        themePreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themePref = themePreferences?.getString("theme", Constants.SYSTEM_THEME)

        when (themePref) {
            Constants.LIGHT_THEME -> {
                themePreferences?.edit()?.putString("theme", Constants.LIGHT_THEME)?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            Constants.DARK_THEME -> {
                themePreferences?.edit()?.putString("theme", Constants.DARK_THEME)?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            Constants.SYSTEM_THEME -> {
                themePreferences?.edit()?.putString("theme", Constants.SYSTEM_THEME)?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun destinationChangeListener(navView: BottomNavigationView) {
        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {

                R.id.mainSearchFragment -> {
                    supportActionBar?.hide()
                    navView.visibility = View.GONE
                }

                R.id.settingsFragment -> {
                    supportActionBar?.show()
                    navView.visibility = View.GONE
                }

                R.id.shelfWithBooksFragment -> {
                    supportActionBar?.show()
                    navView.visibility = View.VISIBLE
                    supportActionBar?.title = currentShelf?.shelfTitle
                }

                R.id.bookUserNotesFragment -> {
                    supportActionBar?.show()
                    navView.visibility = View.GONE
                    supportActionBar?.title = currentLocalBook?.bookTitle
                }

                R.id.bookToShelfFragment -> {
                    supportActionBar?.show()
                    navView.visibility = View.GONE
                    supportActionBar?.title = currentLocalBook?.bookTitle
                }

                else -> {
                    supportActionBar?.show()
                    navView.visibility = View.VISIBLE
                }

            }

        }
    }

    private fun fetchBooks() {
        booksViewModel.collectBooksFromFirestore().invokeOnCompletion {
            booksViewModel.firebaseBookList.observe(this) { response ->
                when (response) {
                    is Response.Loading -> {
                        Toast.makeText(
                            this,
                            "Checking user library, please wait..", Toast.LENGTH_SHORT
                        ).show()
                        Log.i("booksDownload", "Books downloading")
                    }
                    is Response.Success -> {
                        val firebaseBookList = response.data
                        var i = 0
                        while (i < firebaseBookList.size) {
                            val book = firebaseBookList[i]
                            booksViewModel.insertBook(book).invokeOnCompletion {
                                booksViewModel.getBookList()
                            }
                            i++
                        }
                        Log.i("booksDownload", "Books downloaded")
                    }
                    is Response.Failure -> {
                        Log.e("Firebase Fetch Books Error:", response.errorMessage)
                        Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun fetchCrossRefs() {
        booksViewModel.collectCrossRefFromFirestore().invokeOnCompletion {
            booksViewModel.firebaseCrossRefList.observe(this) { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.i("crossRefsDownload", "CrossRefs downloading")
                    }
                    is Response.Success -> {
                        val firebaseCrossRefList = response.data
                        var i = 0
                        while (i < firebaseCrossRefList.size) {
                            val crossRef = firebaseCrossRefList[i]
                            shelvesViewModel.insertBookShelfCrossRef(crossRef).invokeOnCompletion {
                                shelvesViewModel.getShelfWithBookList()
                            }
                            i++
                        }
                        with(sharedPreferences.edit()) {
                            //putBoolean("library_downloaded", true).apply()
                            putBoolean("first_time", false).apply()
                        }
                        Log.i("crossRefsDownload", "CrossRefs downloaded")
                    }
                    is Response.Failure -> {
                        Log.e("Firebase Fetch CrossRefs Error:", response.errorMessage)
                        Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun fetchShelves() {
        booksViewModel.collectShelvesFromFirestore().invokeOnCompletion {
            booksViewModel.firebaseShelfList.observe(this) { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.i("shelvesDownload", "Shelves downloading")
                    }
                    is Response.Success -> {
                        val firebaseShelvesList = response.data
                        var i = 0
                        while (i < firebaseShelvesList.size) {
                            val shelf = firebaseShelvesList[i]
                            shelvesViewModel.insertShelf(shelf).invokeOnCompletion {
                                shelvesViewModel.getShelfList()
                            }
                            i++
                        }
                        Log.i("shelvesDownload", "Shelves downloaded")
                        Toast.makeText(this, "Library successfully synced..", Toast.LENGTH_LONG)
                            .show()
                    }
                    is Response.Failure -> {
                        Log.e("Firebase Fetch CrossRefs Error:", response.errorMessage)
                        Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }

}