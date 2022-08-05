package com.armutyus.ninova.constants

import com.armutyus.ninova.BuildConfig

object Constants {

    const val VERSION_NAME = BuildConfig.VERSION_NAME

    //Messages
    const val ERROR_MESSAGE = "Unexpected error!"

    //Intents
    const val ABOUT_INTENT = "aboutIntent"
    const val BOOK_DETAILS_INTENT = "bookDetailsIntent"
    const val LOGIN_INTENT = "loginIntent"
    const val MAIN_INTENT = "mainIntent"
    const val REGISTER_INTENT = "registerIntent"
    const val SPLASH_INTENT = "splashIntent"

    //Preferences
    const val CHANGE_EMAIL = "change_email"
    const val CHANGE_PASSWORD = "change_password"
    const val DARK_THEME = "dark"
    const val DETAILS_EXTRA = "fromDetails"
    const val DETAILS_STRING_EXTRA = "detailsActivity"
    const val FORGOT_PASSWORD = "forgot_password"
    const val FROM_DETAILS_ACTIVITY = "bookDetailsActivity"
    const val FROM_DETAILS_TO_NOTES_EXTRA = "detailsToNotes"
    const val LIGHT_THEME = "light"
    const val REGISTER = "register"
    const val SETTINGS_ACTION_KEY = "action"
    const val SYSTEM_THEME = "system"


    //References
    const val BASE_URL = "https://www.googleapis.com/books/v1/"
    const val DB_REF = "userLibraryCollection"
    const val DOCUMENT_REF = "userLibraryDocument"
    const val USERS_REF = "users"

    //UserFields
    const val CREATED_AT = "createdAt"
    const val EMAIL = "email"
    const val NAME = "name"
    const val PHOTO_URL = "photoUrl"
    const val USER_TYPE = "userType"

    //BookViewType
    const val BOOK_TYPE_FOR_DETAILS = "bookTypeForDetails"
    const val GOOGLE_BOOK_TYPE = 0
    const val LOCAL_BOOK_TYPE = 1


}