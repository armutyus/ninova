package com.armutyus.ninova.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
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
import com.armutyus.ninova.constants.Constants.REGISTER
import com.armutyus.ninova.constants.Constants.REGISTER_INTENT
import com.armutyus.ninova.constants.Constants.SETTINGS_ACTION_KEY
import com.armutyus.ninova.constants.Constants.SYSTEM_THEME
import com.armutyus.ninova.constants.Constants.VERSION_NAME
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.ui.books.BooksViewModel
import com.armutyus.ninova.ui.shelves.ShelvesViewModel
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

    private var sharedPreferences: SharedPreferences? = null
    private val settingsViewModel by activityViewModels<SettingsViewModel>()
    private val shelvesViewModel by activityViewModels<ShelvesViewModel>()
    private val booksViewModel by activityViewModels<BooksViewModel>()
    private val user = auth.currentUser!!

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        if (user.isAnonymous) {
            setPreferencesFromResource(R.xml.anonymous_preferences, rootKey)
        } else {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

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
            //intent to privacy policy
            println("Privacy policy")
            true
        }
        privacyPolicy?.onPreferenceClickListener = privacyPolicyListener

        val signOutListener = Preference.OnPreferenceClickListener {
            signOut()
            true
        }
        signOut?.onPreferenceClickListener = signOutListener

        val uploadLibraryListener = Preference.OnPreferenceClickListener {
            uploadBooks()
            uploadShelves()
            uploadCrossRefs()
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
        sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
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

    private fun uploadBooks() {
        booksViewModel.getBookList()
        booksViewModel.localBookList.observe(viewLifecycleOwner) { localBookList ->
            var i = 0
            while (i < localBookList.size) {
                settingsViewModel.uploadUserBooksToFirestore(localBookList[i]).invokeOnCompletion {
                    settingsViewModel.firebaseAuthResponse.observe(viewLifecycleOwner) { response ->
                        when (response) {
                            is Response.Loading -> Log.i("booksUpload", "Books uploading")
                            is Response.Success -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Library uploaded to: ${user.email}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.i("bookUpload", "Books uploaded")
                            }
                            is Response.Failure -> {
                                println("Book Upload Error: " + response.errorMessage)
                                Toast.makeText(
                                    requireContext(),
                                    response.errorMessage,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    }
                }
                i++
            }
        }
    }

    private fun uploadShelves() {
        shelvesViewModel.getShelfList()
        shelvesViewModel.shelfList.observe(viewLifecycleOwner) { localShelfList ->
            var i = 0
            while (i < localShelfList.size) {
                settingsViewModel.uploadUserShelvesToFirestore(localShelfList[i])
                    .invokeOnCompletion {
                        settingsViewModel.firebaseAuthResponse.observe(viewLifecycleOwner) { response ->
                            when (response) {
                                is Response.Loading ->
                                    Log.i("shelvesUpload", "Shelves uploading")
                                is Response.Success ->
                                    Log.i("shelvesUpload", "Shelves uploaded")
                                is Response.Failure -> {
                                    Log.e("Shelves Upload Error", response.errorMessage)
                                    Toast.makeText(
                                        requireContext(),
                                        response.errorMessage,
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            }
                        }
                    }
                i++
            }
        }
    }

    private fun uploadCrossRefs() {
        booksViewModel.getBookShelfCrossRef()
        booksViewModel.bookShelfCrossRefList.observe(viewLifecycleOwner) { localCrossRefList ->
            var i = 0
            while (i < localCrossRefList.size) {
                settingsViewModel.uploadUserCrossRefToFirestore(localCrossRefList[i])
                    .invokeOnCompletion {
                        settingsViewModel.firebaseAuthResponse.observe(viewLifecycleOwner) { response ->
                            when (response) {
                                is Response.Loading ->
                                    Log.i("crossRefsUpload", "CrossRefs uploading")
                                is Response.Success ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Uploading library..",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                is Response.Failure -> {
                                    println("Create Error: " + response.errorMessage)
                                    Toast.makeText(
                                        requireContext(),
                                        response.errorMessage,
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }
                            }
                        }

                    }
                i++
            }
        }
    }

    private fun signOut() {
        settingsViewModel.signOut().invokeOnCompletion {
            settingsViewModel.firebaseAuthResponse.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Response.Loading ->
                        Toast.makeText(requireContext(), "Please wait..", Toast.LENGTH_SHORT).show()
                    is Response.Success -> {
                        Toast.makeText(requireContext(), "Signed out!", Toast.LENGTH_SHORT).show()
                        clearDatabase()
                        goToLogInActivity()
                    }
                    is Response.Failure -> {
                        println("Sign Out Error: " + response.errorMessage)
                        Toast.makeText(requireContext(), response.errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }

        }
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