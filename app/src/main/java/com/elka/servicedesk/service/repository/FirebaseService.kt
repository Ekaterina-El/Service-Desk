package com.elka.servicedesk.service.repository

import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*


object FirebaseService {
  suspend fun loadPhoto(url: String): String {
    val uri = Uri.parse(url)
    val path = Calendar.getInstance().time.toString()
    val doc = storage.reference.child(path).putFile(uri).await()
    return doc.storage.downloadUrl.await().toString()
  }

  private const val USERS_COLLECTION = "users"
  private const val DIVISIONS_COLLECTION = "divisions"
  private const val LOGS_COLLECTION = "logs"
  private const val ACCIDENTS_COLLECTION = "accidents"

  val usersCollection by lazy { Firebase.firestore.collection(USERS_COLLECTION) }
  val divisionsCollection by lazy { Firebase.firestore.collection(DIVISIONS_COLLECTION) }
  val logsCollection by lazy { Firebase.firestore.collection(LOGS_COLLECTION) }
  val accidentsCollection by lazy { Firebase.firestore.collection(ACCIDENTS_COLLECTION) }

  val storage = FirebaseStorage.getInstance()
}