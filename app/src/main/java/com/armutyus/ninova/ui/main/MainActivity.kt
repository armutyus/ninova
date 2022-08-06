package com.armutyus.ninova.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.armutyus.ninova.MobileNavigationDirections
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Cache.currentLocalBook
import com.armutyus.ninova.constants.Cache.currentShelf
import com.armutyus.ninova.constants.Constants.DETAILS_EXTRA
import com.armutyus.ninova.constants.Constants.FROM_DETAILS_TO_NOTES_EXTRA
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.databinding.ActivityMainBinding
import com.armutyus.ninova.fragmentfactory.NinovaFragmentFactoryEntryPoint
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val viewModel by viewModels<MainViewModel>()
    private val shelvesViewModel by viewModels<ShelvesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        val entryPoint = EntryPointAccessors.fromActivity(
            this,
            NinovaFragmentFactoryEntryPoint::class.java
        )
        supportFragmentManager.fragmentFactory = entryPoint.getFragmentFactory()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //registerLauncher()

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

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

                    R.id.export_db -> {
                        //exportDbClicked()
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

    private fun exportDbToStorage(dbFileUri: Uri) {

        viewModel.exportDbToStorage(dbFileUri).observe(this) { response ->
            when (response) {
                is Response.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Response.Success -> {
                    Toast.makeText(this, "Your library uploaded successfully", Toast.LENGTH_LONG).show()
                    binding.progressBar.visibility = View.GONE
                }
                is Response.Failure -> {
                    println("Create Error: " + response.errorMessage)
                    Toast.makeText(this, response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

    }

    private fun exportDbClicked() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Snackbar.make(this.window.decorView.rootView, "Permission needed!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission") {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val data: File = filesDir
            val currentDBPath = getDatabasePath("NinovaLocalDB.db").absolutePath
            println("exportClicked: $currentDBPath")
            val destDir = File(currentDBPath)
            val dbFileUri = FileProvider.getUriForFile(this, "${this.packageName}.provider", destDir)
            val exportDbIntent =
                Intent(Intent.ACTION_SEND, dbFileUri)
            exportDbIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            activityResultLauncher.launch(exportDbIntent)
        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {

                val resultUri = result.data!!.data
                println("resultUri: $resultUri")

                if (resultUri != null) {
                    exportDbToStorage(resultUri)
                }
            }
        }
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { result ->
            if (result) {
                val data: File = filesDir
                val currentDBPath = getDatabasePath("NinovaLocalDB.db").absolutePath
                println("registerLauncher: $currentDBPath")
                val destDir = File(currentDBPath)
                val dbFileUri = FileProvider.getUriForFile(this, "${this.packageName}.provider", destDir)
                val exportDbIntent =
                    Intent(Intent.ACTION_SEND, dbFileUri)
                exportDbIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                activityResultLauncher.launch(exportDbIntent)
            } else {
                Toast.makeText(this, "Permission needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
                || super.onSupportNavigateUp()
    }

}