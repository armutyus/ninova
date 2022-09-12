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

    //Preferences
    const val CHANGE_EMAIL = "change_email"
    const val CHANGE_PASSWORD = "change_password"
    const val DARK_THEME = "dark"
    const val MAIN_SHARED_PREF = "main_shared_preferences"
    const val FORGOT_PASSWORD = "forgot_password"
    const val LIGHT_THEME = "light"
    const val REGISTER = "register"
    const val SETTINGS_ACTION_KEY = "action"
    const val SYSTEM_THEME = "system"


    //References
    const val BASE_URL = "https://www.googleapis.com/books/v1/"
    const val BOOKS_REF = "Books"
    const val BOOKSHELF_CROSS_REF = "BookShelfCrossRef"
    const val PRIVACY_POLICY_URL = "https://sites.google.com/view/ninova-bookshelf-app"
    const val SHELVES_REF = "Shelves"
    const val USERS_REF = "Users"

    //UserFields
    const val CREATED_AT = "createdAt"
    const val EMAIL = "email"
    const val NAME = "name"
    const val PHOTO_URL = "photoUrl"

    //BookViewType
    const val BOOK_TYPE_FOR_DETAILS = "bookTypeForDetails"
    const val GOOGLE_BOOK_TYPE = 0
    const val LOCAL_BOOK_TYPE = 1

    //RandomWordsList
    val randomWordList = listOf(
        "William",
        "Shakespeare",
        "Christie",
        "Agatha",
        "Cartland",
        "Barbara",
        "Danielle",
        "Steel",
        "Harold",
        "Robbins",
        "Georges",
        "Simenon",
        "Enid",
        "Blyton",
        "Sidney",
        "Sheldon",
        "Eiichiro",
        "Rowling",
        "Gilbert",
        "Patten",
        "Seuss",
        "Akira",
        "Tolstoy",
        "Dostoyevski",
        "Corin",
        "Tellado",
        "Pushkin",
        "Fyodor",
        "Aleksandr",
        "Dean",
        "Koontz",
        "Chuck",
        "Jackie",
        "Collins",
        "Hegel",
        "Darwin",
        "Yalom",
        "Harari",
        "Gogol",
        "Stephen",
        "King",
        "Paulo",
        "Coelho",
        "Edgar",
        "Allan",
        "Wallace",
        "Jiro",
        "Akagawa",
        "Tolkien",
        "Robert",
        "Ludlum",
        "Dan",
        "Brown",
        "Yuval",
        "Palahniuk",
        "Kant",
        "Heidegger",
        "James",
        "Patterson",
        "Rene",
        "Goscinny",
        "Frederik",
        "Osamu",
        "Roald",
        "Dahl",
        "Irving",
        "Wallace",
        "Karl",
        "Carter",
        "Masashi",
        "Kishimoto",
        "Fleming",
        "Robin",
        "Cook",
        "Charles",
        "Dickens",
        "Antoine",
        "Saint-Exupery",
        "Lewis",
        "Haggard",
        "Salinger",
        "Nabokov",
        "Gabriel",
        "Garcia",
        "Marquez",
        "Ursula",
        "Atwood",
        "George",
        "Orwell",
        "Lucy",
        "Maud",
        "Montgomery",
        "Machiavelli",
        "Umberto",
        "Eco",
        "Richard",
        "Adams",
        "Higgins",
        "Harper",
        "Lee",
        "Carl",
        "Sagan",
        "Anne",
        "Frank",
        "Scott",
        "Fitzgerald",
        "Stieg",
        "Larsson",
        "Margaret",
        "Mitchell",
        "Hawking",
        "Suzanne",
        "Collins",
        "Mark",
        "Twain",
        "Jane",
        "Austen",
        "Albert",
        "Camus",
        "Dante",
        "Alighieri",
        "Carlo",
        "Collodi"
    )

}