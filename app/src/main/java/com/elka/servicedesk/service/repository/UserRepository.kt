package com.elka.servicedesk.service.repository

import com.elka.servicedesk.other.ErrorApp
import com.elka.servicedesk.other.Errors
import com.elka.servicedesk.other.Role
import com.elka.servicedesk.service.model.User
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object UserRepository {
  private val auth = Firebase.auth
  val currentUid get() = auth.currentUser?.uid

  suspend fun registrationUser(
    email: String, password: String, onSuccess: suspend (uid: String) -> Unit
  ): ErrorApp? = try {
    val uid = auth.createUserWithEmailAndPassword(email, password).await().user!!.uid
    onSuccess(uid)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: FirebaseAuthWeakPasswordException) {
    Errors.weakPassword
  } catch (e: FirebaseAuthUserCollisionException) {
    Errors.userCollision
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun addUser(user: User, onSuccess: suspend (() -> Unit) = {}): ErrorApp? = try {
    FirebaseService.usersCollection.document(user.id).set(user).await()
    if (user.role == Role.USER) DivisionsRepository.addEmployer(user.divisionId, user.id)
    onSuccess()
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun logout(onSuccess: suspend () -> Unit) {
    auth.signOut()
    onSuccess()
  }

  suspend fun login(email: String, password: String, onSuccess: () -> Unit): ErrorApp? = try {
    auth.signInWithEmailAndPassword(email, password).await()
    onSuccess()
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: FirebaseAuthInvalidUserException) {
    Errors.invalidEmailPassword
  } catch (e: FirebaseAuthInvalidCredentialsException) {
    Errors.invalidEmailPassword
  } catch (e: Exception) {
    Errors.unknown
  }

  suspend fun loadCurrentUserProfile(onSuccess: (User) -> Unit): ErrorApp? = try {
    val profile = getUserById(currentUid!!)
    onSuccess(profile)
    null
  } catch (e: FirebaseNetworkException) {
    Errors.network
  } catch (e: Exception) {
    Errors.unknown
  }

  private suspend fun getUserById(id: String): User {
    val doc = FirebaseService.usersCollection.document(id).get().await()
    val user = doc.toObject(User::class.java)
    user!!.id = doc.id
    return user
  }
}