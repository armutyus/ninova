package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow

interface FirebaseRepositoryInterface {

    suspend fun signInWithEmailPassword(email: String, password: String): Flow<Response<Boolean>>

    suspend fun signInAnonymous(): Flow<Response<Boolean>>

    suspend fun anonymousToPermanent(credential: AuthCredential): Flow<Response<Boolean>>

    suspend fun signUpWithEmailPassword(email: String, password: String): Flow<Response<Boolean>>

    suspend fun createUserInFirestore(): Flow<Response<Void>>

    suspend fun downloadUserBooksFromFirestore(): Flow<Response<List<DataModel.LocalBook>>>

    suspend fun downloadUserShelvesFromFirestore(): Flow<Response<List<LocalShelf>>>

    suspend fun downloadUserCrossRefFromFirestore(): Flow<Response<List<BookShelfCrossRef>>>

    suspend fun uploadUserBooksToFirestore(localBook: DataModel.LocalBook): Flow<Response<Void>>

    suspend fun uploadUserShelvesToFirestore(shelf: LocalShelf): Flow<Response<Void>>

    suspend fun uploadUserCrossRefToFirestore(bookShelfCrossRef: BookShelfCrossRef): Flow<Response<Void>>

    suspend fun signOut(): Flow<Response<Unit>>

    suspend fun reAuthUser(credential: AuthCredential): Flow<Response<Boolean>>

    suspend fun changeUserEmail(email: String): Flow<Response<Boolean>>

    suspend fun changeUserPassword(password: String): Flow<Response<Boolean>>

    suspend fun sendResetPassword(email: String): Flow<Response<Boolean>>

}