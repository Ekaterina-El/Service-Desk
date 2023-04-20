package com.elka.servicedesk.service.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object FirebaseService {
  private const val USERS_COLLECTION = "users"
  private const val DIVISIONS_COLLECTION = "divisions"

  val usersCollection by lazy { Firebase.firestore.collection(USERS_COLLECTION) }
  val divisionsCollection by lazy { Firebase.firestore.collection(DIVISIONS_COLLECTION) }

//  val storage = FirebaseStorage.getInstance()
}