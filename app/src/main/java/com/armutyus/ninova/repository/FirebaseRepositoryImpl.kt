package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Constants.BOOKSHELFCROSS_REF
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : FirebaseRepositoryInterface {

    override suspend fun signInWithEmailPassword(email: String, password: String) = flow {
        try {
            emit(Response.Loading)
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun anonymousToPermanent(credential: AuthCredential) = flow {
        try {
            emit(Response.Loading)
            val authResult = auth.currentUser!!.linkWithCredential(credential).await()
            authResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun signInAnonymous() = flow {
        try {
            emit(Response.Loading)
            val authResult = auth.signInAnonymously().await()
            authResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun createUserInFirestore() = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                db.collection(USERS_REF).document(uid).set(
                    mapOf(
                        NAME to displayName,
                        EMAIL to email,
                        PHOTO_URL to photoUrl?.toString(),
                        CREATED_AT to FieldValue.serverTimestamp()
                    )
                ).await().also {
                    emit(Response.Success(it))
                }
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun downloadUserBooksFromFirestore() = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                db.collection(USERS_REF).document(uid).collection(BOOKS_REF)
                    .get().await().toObjects(DataModel.LocalBook::class.java).also {
                        emit(Response.Success(it))
                    }
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun downloadUserShelvesFromFirestore() = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                db.collection(USERS_REF).document(uid).collection(SHELVES_REF)
                    .get().await().toObjects(LocalShelf::class.java).also {
                        emit(Response.Success(it))
                    }
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun downloadUserCrossRefFromFirestore() = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                db.collection(USERS_REF).document(uid).collection(BOOKSHELFCROSS_REF)
                    .get().await().toObjects(BookShelfCrossRef::class.java).also {
                        emit(Response.Success(it))
                    }
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun uploadUserBooksToFirestore(localBook: DataModel.LocalBook) = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                db.collection(USERS_REF).document(uid).collection(BOOKS_REF)
                    .document(localBook.bookTitle!!).set(
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
                    ).await().also {
                        emit(Response.Success(it))
                    }
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun uploadUserShelvesToFirestore(shelf: LocalShelf) = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                db.collection(USERS_REF).document(uid).collection(SHELVES_REF)
                    .document(shelf.shelfTitle!!).set(
                        mapOf(
                            "shelfId" to shelf.shelfId,
                            "shelfTitle" to shelf.shelfTitle,
                            "createdAt" to shelf.createdAt,
                            "shelfCover" to shelf.shelfCover
                        )
                    ).await().also {
                        emit(Response.Success(it))
                    }
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun uploadUserCrossRefToFirestore(bookShelfCrossRef: BookShelfCrossRef) =
        flow {
            try {
                emit(Response.Loading)
                auth.currentUser?.apply {
                    val crossRefDocumentId =
                        db.collection(USERS_REF).document(uid).collection(BOOKSHELFCROSS_REF)
                            .document().id
                    db.collection(USERS_REF).document(uid).collection(BOOKSHELFCROSS_REF)
                        .document(crossRefDocumentId).set(
                            mapOf(
                                "bookId" to bookShelfCrossRef.bookId,
                                "shelfId" to bookShelfCrossRef.shelfId
                            )
                        ).await().also {
                            emit(Response.Success(it))
                        }
                }
            } catch (e: Exception) {
                emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
            }
        }

    override suspend fun signUpWithEmailPassword(email: String, password: String) = flow {
        try {
            emit(Response.Loading)
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun signOut() = flow {
        try {
            emit(Response.Loading)
            auth.signOut().apply {
                emit(Response.Success(this))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun reAuthUser(credential: AuthCredential) = flow {
        try {
            emit(Response.Loading)
            val reAuthResult = auth.currentUser!!.reauthenticate(credential).await()
            reAuthResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun changeUserEmail(email: String) = flow {
        try {
            emit(Response.Loading)
            val reAuthResult = auth.currentUser!!.updateEmail(email).await()
            reAuthResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun changeUserPassword(password: String) = flow {
        try {
            emit(Response.Loading)
            val reAuthResult = auth.currentUser!!.updatePassword(password).await()
            reAuthResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun sendResetPassword(email: String) = flow {
        try {
            emit(Response.Loading)
            auth.sendPasswordResetEmail(email).await().apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

}