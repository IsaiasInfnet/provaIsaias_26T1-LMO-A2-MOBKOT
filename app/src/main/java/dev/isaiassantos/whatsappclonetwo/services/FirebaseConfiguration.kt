package dev.isaiassantos.whatsappclonetwo.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseConfiguration {
    companion object {
        private var databaseReference: DatabaseReference? = null
        private var firebaseAuth: FirebaseAuth? = null
        private var firestore: FirebaseFirestore? = null

        private const val DATABASE_URL = "https://mercadomobil-fd698-default-rtdb.firebaseio.com/"

        @JvmStatic
        fun getFirebaseDatabase(): DatabaseReference {
            if (databaseReference == null) {
                databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).reference
            }
            return databaseReference!!
        }

        @JvmStatic
        fun getFirebaseAuth(): FirebaseAuth {
            if (firebaseAuth == null) {
                firebaseAuth = FirebaseAuth.getInstance()
            }
            return firebaseAuth!!
        }

        @JvmStatic
        fun getFirebaseFirestore(): FirebaseFirestore {
            if (firestore == null) {
                firestore = FirebaseFirestore.getInstance()
            }
            return firestore!!
        }
    }
}