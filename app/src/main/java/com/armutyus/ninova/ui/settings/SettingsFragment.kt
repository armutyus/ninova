package com.armutyus.ninova.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Constants.ABOUT_INTENT
import com.armutyus.ninova.constants.Constants.CHANGE_EMAIL
import com.armutyus.ninova.constants.Constants.CHANGE_PASSWORD
import com.armutyus.ninova.constants.Constants.DARK_THEME
import com.armutyus.ninova.constants.Constants.LIGHT_THEME
import com.armutyus.ninova.constants.Constants.LOGIN_INTENT
import com.armutyus.ninova.constants.Constants.MAIN_INTENT
import com.armutyus.ninova.constants.Constants.PRIVACY_POLICY_URL
import com.armutyus.ninova.constants.Constants.REGISTER
import com.armutyus.ninova.constants.Constants.REGISTER_INTENT
import com.armutyus.ninova.constants.Constants.SETTINGS_ACTION_KEY
import com.armutyus.ninova.constants.Constants.SYSTEM_THEME
import com.armutyus.ninova.constants.Constants.VERSION_NAME
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SettingsFragment @Inject constructor(
    auth: FirebaseAuth
) : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Named(LOGIN_INTENT)
    @Inject
    lateinit var loginIntent: Intent

    @Named(MAIN_INTENT)
    @Inject
    lateinit var mainIntent: Intent

    @Named(REGISTER_INTENT)
    @Inject
    lateinit var registerIntent: Intent

    @Named(ABOUT_INTENT)
    @Inject
    lateinit var aboutIntent: Intent

    private val booksViewModel by activityViewModels<BooksViewModel>()
    private val settingsViewModel by activityViewModels<SettingsViewModel>()
    private val shelvesViewModel by activityViewModels<ShelvesViewModel>()
    private var shouldUploadBeforeSignOut = false
    private var showSuccessToast = true
    private var showUploadToast = true
    private val user = auth.currentUser!!

    private val sharedPreferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(requireContext())

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        if (user.isAnonymous) {
            setPreferencesFromResource(R.xml.anonymous_preferences, rootKey)
        } else {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }


        val aboutNinova = findPreference<Preference>("about_ninova")
        val changeEmail = findPreference<Preference>("change_email")
        val changePassword = findPreference<Preference>("change_password")
        val privacyPolicy = findPreference<Preference>("privacy_policy")
        val register = findPreference<Preference>("register")
        val signOut = findPreference<Preference>("sign_out")
        val switchAccount = findPreference<Preference>("switch_account")
        val uploadLibrary = findPreference<Preference>("upload_library")

        val changeEmailListener = Preference.OnPreferenceClickListener {
            registerIntent.putExtra(SETTINGS_ACTION_KEY, CHANGE_EMAIL)
            goToRegisterActivity()
            true
        }
        changeEmail?.onPreferenceClickListener = changeEmailListener

        val changePasswordListener = Preference.OnPreferenceClickListener {
            registerIntent.putExtra(SETTINGS_ACTION_KEY, CHANGE_PASSWORD)
            goToRegisterActivity()
            true
        }
        changePassword?.onPreferenceClickListener = changePasswordListener

        val aboutNinovaListener = Preference.OnPreferenceClickListener {
            goToAboutActivity()
            true
        }
        aboutNinova?.onPreferenceClickListener = aboutNinovaListener
        aboutNinova?.summary = "Version: $VERSION_NAME"

        val privacyPolicyListener = Preference.OnPreferenceClickListener {
            val privacyPolicyIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
            ContextCompat.startActivity(requireContext(), privacyPolicyIntent, null)
            true
        }
        privacyPolicy?.onPreferenceClickListener = privacyPolicyListener

        val signOutListener = Preference.OnPreferenceClickListener {
            showSignOutDialog()
            true
        }
        signOut?.onPreferenceClickListener = signOutListener

        val uploadLibraryListener = Preference.OnPreferenceClickListener {
            showSuccessToast = true
            showUploadToast = true
            shouldUploadBeforeSignOut = false
            uploadBooks()
            true
        }
        uploadLibrary?.onPreferenceClickListener = uploadLibraryListener
        uploadLibrary?.summary = "Link your library with your account: ${user.email}"

        val registerListener = Preference.OnPreferenceClickListener {
            registerIntent.putExtra(SETTINGS_ACTION_KEY, REGISTER)
            goToRegisterActivity()
            true
        }
        register?.onPreferenceClickListener = registerListener

        val switchAccountListener = Preference.OnPreferenceClickListener {
            startActivity(loginIntent)
            true
        }
        switchAccount?.onPreferenceClickListener = switchAccountListener

    }

    override fun onResume() {
        super.onResume()
        removeBackButtonAndMenu()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        removeBackButtonAndMenu()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        val themePref = p0?.getString("theme", "system")

        if (p1 == "theme") {
            when (themePref) {
                LIGHT_THEME -> {
                    p0.edit().putString("theme", LIGHT_THEME).apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                DARK_THEME -> {
                    p0.edit().putString("theme", DARK_THEME).apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                SYSTEM_THEME -> {
                    p0.edit().putString("theme", SYSTEM_THEME).apply()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }

    private fun removeBackButtonAndMenu() {
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun uploadBooks() {
        booksViewModel.loadBookList().invokeOnCompletion {
            val localBookList = booksViewModel.localBookList.value
            if (localBookList != null) {
                var i = 0
                while (i < localBookList.size) {
                    settingsViewModel.uploadUserBooksToFirestore(localBookList[i]) { response ->
                        when (response) {
                            is Response.Loading -> Log.i("booksUpload", "Books uploading")
                            is Response.Success -> {
                                if (showUploadToast) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Uploading library..",
                                        Toast.LENGTH_SHORT
                                    ).show().also {
                                        showUploadToast = false
                                    }
                                }
                                when (i) {
                                    localBookList.size -> {
                                        Log.i("bookUpload", "Books uploaded")
                                        uploadShelves()
                                    }
                                }
                            }
                            is Response.Failure -> {
                                Log.e("Books Upload Error", response.errorMessage)
                                Toast.makeText(
                                    requireContext(),
                                    response.errorMessage,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    }
                    i++
                }
            } else {
                Log.i("bookUpload", "No books in library")
            }
        }
    }

    private fun uploadShelves() {
        shelvesViewModel.loadShelfList().invokeOnCompletion {
            val localShelfList = shelvesViewModel.shelfList.value
            if (localShelfList != null) {
                var i = 0
                while (i < localShelfList.size) {
                    settingsViewModel.uploadUserShelvesToFirestore(localShelfList[i]) { response ->
                        when (response) {
                            is Response.Loading ->
                                Log.i("shelvesUpload", "Shelves uploading")
                            is Response.Success -> {
                                when (i) {
                                    localShelfList.size -> {
                                        Log.i("shelvesUpload", "Shelves uploaded")
                                        uploadCrossRefs()
                                    }
                                }
                            }
                            is Response.Failure -> {
                                Log.e("Shelves Upload Error", response.errorMessage)
                                Toast.makeText(
                                    requireContext(),
                                    response.errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    i++
                }
            } else {
                Log.i("shelvesUpload", "No shelves")
            }
        }
    }

    private fun uploadCrossRefs() {
        booksViewModel.loadBookShelfCrossRef().invokeOnCompletion {
            val localCrossRefList = booksViewModel.bookShelfCrossRefList.value
            if (localCrossRefList != null) {
                var i = 0
                while (i < localCrossRefList.size) {
                    settingsViewModel.uploadUserCrossRefToFirestore(localCrossRefList[i]) { response ->
                        when (response) {
                            is Response.Loading ->
                                Log.i("crossRefsUpload", "CrossRefs uploading")
                            is Response.Success -> {
                                when (i) {
                                    localCrossRefList.size -> {
                                        if (shouldUploadBeforeSignOut) {
                                            if (showSuccessToast) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Library uploaded to: ${user.email}",
                                                    Toast.LENGTH_SHORT
                                                ).show().also {
                                                    showSuccessToast = false
                                                }
                                                signOut()
                                            }
                                        } else {
                                            if (showSuccessToast) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Library uploaded to: ${user.email}",
                                                    Toast.LENGTH_SHORT
                                                ).show().also {
                                                    showSuccessToast = false
                                                }
                                            }
                                        }
                                        Log.i("crossRefsUpload", "CrossRefs uploaded")
                                    }
                                }
                            }
                            is Response.Failure -> {
                                Log.e("CrossRefs Upload Error", response.errorMessage)
                                Toast.makeText(
                                    requireContext(),
                                    response.errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    i++
                }
            } else {
                Log.i("crossRefsUpload", "No crossrefs")
            }
        }
    }

    private fun signOut() {
        settingsViewModel.signOut { response ->
            when (response) {
                is Response.Loading ->
                    Toast.makeText(requireContext(), "Please wait..", Toast.LENGTH_SHORT).show()
                is Response.Success -> {
                    shouldUploadBeforeSignOut = false
                    Toast.makeText(requireContext(), "Signed out!", Toast.LENGTH_SHORT).show()
                    clearDatabase()
                    goToLogInActivity()
                }
                is Response.Failure -> {
                    Log.e("Sign Out Error", response.errorMessage)
                    Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun showSignOutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.sign_out))
            .setMessage(resources.getString(R.string.upload_library_message))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                signOut()
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                showSuccessToast = true
                showUploadToast = true
                shouldUploadBeforeSignOut = true
                uploadBooks()
            }
            .show()
    }

    private fun clearDatabase() {
        settingsViewModel.clearDatabase()
    }

    private fun goToLogInActivity() {
        startActivity(loginIntent)
        activity?.finish()
    }

    private fun goToRegisterActivity() {
        startActivity(registerIntent)
    }

    private fun goToAboutActivity() {
        startActivity(aboutIntent)
    }

}