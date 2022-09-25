package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Constants.BOOKSHELF_CROSS_REF
import com.armutyus.ninova.constants.Constants.BOOKS_REF
import com.armutyus.ninova.constants.Constants.CREATED_AT
import com.armutyus.ninova.constants.Constants.EMAIL
import com.armutyus.ninova.constants.Constants.ERROR_MESSAGE
import com.armutyus.ninova.constants.Constants.NAME
import com.armutyus.ninova.constants.Constants.PHOTO_URL
import com.armutyus.ninova.constants.Constants.SHELVES_REF
import com.armutyus.ninova.constants.Constants.USERS_REF
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class FirebaseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : FirebaseRepositoryInterface {

    override suspend fun signInWithEmailPassword(
        email: String,
        password: String
    ): Response<Boolean> = withContext(coroutineContext) {
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.let {
                return@let Response.Success(true)
            }
        } catch (e: Exception) {
            Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
        }
    }

    override suspend fun anonymousToPermanent(credential: AuthCredential): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                val authResult = auth.currentUser!!.linkWithCredential(credential).await()
                authResult.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun signInAnonymous(): Response<Boolean> = withContext(coroutineContext) {
        try {
            val authResult = auth.signInAnonymously().await()
            authResult.let {
                return@let Response.Success(true)
            }
        } catch (e: Exception) {
            Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
        }
    }

    override suspend fun createUserInFirestore(): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                val createUser = auth.currentUser?.apply {
                    db.collection(USERS_REF).document(uid).set(
                        mapOf(
                            NAME to displayName,
                            EMAIL to email,
                            PHOTO_URL to photoUrl?.toString(),
                            CREATED_AT to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
                createUser.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun downloadUserBooksFromFirestore(): Response<List<DataModel.LocalBook>> =
        withContext(coroutineContext) {
            try {
                val uid = auth.currentUser?.uid!!
                val querySnapshot: QuerySnapshot =
                    db.collection(USERS_REF).document(uid).collection(BOOKS_REF)
                        .get().await()
                val bookList = querySnapshot.documents.mapNotNull { documentSnapshot ->
                    documentSnapshot.toObject(DataModel.LocalBook::class.java)?.apply {
                        this.bookId = documentSnapshot.id
                    }
                }
                Response.Success(bookList)
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun downloadUserShelvesFromFirestore(): Response<List<LocalShelf>> =
        withContext(coroutineContext) {
            try {
                val uid = auth.currentUser?.uid!!
                val downloadShelfTask =
                    db.collection(USERS_REF).document(uid).collection(SHELVES_REF)
                        .get().await().toObjects(LocalShelf::class.java)
                downloadShelfTask.let {
                    return@let Response.Success(it)
                }

            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun downloadUserCrossRefFromFirestore(): Response<List<BookShelfCrossRef>> =
        withContext(coroutineContext) {
            try {
                val uid = auth.currentUser?.uid!!
                val downloadCrossRefTask =
                    db.collection(USERS_REF).document(uid).collection(BOOKSHELF_CROSS_REF)
                        .get().await().toObjects(BookShelfCrossRef::class.java)
                downloadCrossRefTask.let {
                    return@let Response.Success(it)
                }

            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun uploadUserBooksToFirestore(localBook: DataModel.LocalBook): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                val uploadBooks = auth.currentUser?.apply {
                    db.collection(USERS_REF).document(uid).collection(BOOKS_REF)
                        .document(localBook.bookId).set(
                            mapOf(
                                "bookId" to localBook.bookId,
                                "bookAuthors" to localBook.bookAuthors,
                                "bookCategories" to localBook.bookCategories,
                                "bookCoverSmallThumbnail" to localBook.bookCoverSmallThumbnail,
                                "bookCoverThumbnail" to localBook.bookCoverThumbnail,
                                "bookDescription" to localBook.bookDescription,
                                "bookNotes" to localBook.bookNotes,
                                "bookPages" to localBook.bookPages,
                                "bookPublishedDate" to localBook.bookPublishedDate,
                                "bookPublisher" to localBook.bookPublisher,
                                "bookSubtitle" to localBook.bookSubtitle,
                                "bookTitle" to localBook.bookTitle
                            )
                        ).await()
                }
                uploadBooks.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun uploadUserShelvesToFirestore(shelf: LocalShelf): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                val uploadShelves = auth.currentUser?.apply {
                    db.collection(USERS_REF).document(uid).collection(SHELVES_REF).get()
                        .continueWith { querySnapshot ->
                            querySnapshot.result.documents.forEach {
                                it.reference.delete()
                            }
                        }.continueWith {
                            db.collection(USERS_REF).document(uid).collection(SHELVES_REF)
                                .document(shelf.shelfTitle!!).set(
                                    mapOf(
                                        "shelfId" to shelf.shelfId,
                                        "shelfTitle" to shelf.shelfTitle,
                                        "createdAt" to shelf.createdAt,
                                        "shelfCover" to shelf.shelfCover
                                    )
                                )
                        }.await()
                }
                uploadShelves.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun uploadUserCrossRefToFirestore(bookShelfCrossRef: BookShelfCrossRef): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                val uploadCrossRef = auth.currentUser?.apply {
                    val crossRefDocumentId =
                        db.collection(USERS_REF).document(uid).collection(BOOKSHELF_CROSS_REF)
                            .document().id
                    db.collection(USERS_REF).document(uid).collection(BOOKSHELF_CROSS_REF).get()
                        .continueWith { querySnapshot ->
                            querySnapshot.result.documents.forEach {
                                it.reference.delete()
                            }
                        }.continueWith {
                            db.collection(USERS_REF).document(uid).collection(BOOKSHELF_CROSS_REF)
                                .document(crossRefDocumentId).set(
                                    mapOf(
                                        "bookId" to bookShelfCrossRef.bookId,
                                        "shelfId" to bookShelfCrossRef.shelfId
                                    )
                                )
                        }.await()
                }
                uploadCrossRef.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun signUpWithEmailPassword(
        email: String,
        password: String
    ): Response<Boolean> = withContext(coroutineContext) {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.let {
                return@let Response.Success(true)
            }
        } catch (e: Exception) {
            Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
        }
    }

    override suspend fun signOut(): Response<Boolean> = withContext(coroutineContext) {
        try {
            auth.signOut().let {
                return@let Response.Success(true)
            }
        } catch (e: Exception) {
            Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
        }
    }

    override suspend fun reAuthUser(credential: AuthCredential): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                val reAuthResult = auth.currentUser!!.reauthenticate(credential).await()
                reAuthResult.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun changeUserEmail(email: String): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                val reAuthResult = auth.currentUser!!.updateEmail(email).await()
                reAuthResult.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun changeUserPassword(password: String): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                val reAuthResult = auth.currentUser!!.updatePassword(password).await()
                reAuthResult.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

    override suspend fun sendResetPassword(email: String): Response<Boolean> =
        withContext(coroutineContext) {
            try {
                val reAuthResult = auth.sendPasswordResetEmail(email).await()
                reAuthResult.let {
                    return@let Response.Success(true)
                }
            } catch (e: Exception) {
                Response.Failure(e.localizedMessage ?: ERROR_MESSAGE)
            }
        }

}