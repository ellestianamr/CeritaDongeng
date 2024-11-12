package com.azhar.dongeng.utils

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FirebaseHelper {

    private val db = FirebaseFirestore.getInstance()

    fun loginUser(
        collection: String,
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection(collection)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]

                    val storedPassword = userDocument.getString("password")

                    if (storedPassword == password) {
                        onResult(true, "Login successful")
                    } else {
                        onResult(false, "Incorrect password")
                    }
                } else {
                    onResult(false, "Email not registered!")
                }
            }
            .addOnFailureListener { exception ->
                onResult(false, "Error logging in: ${exception.message}")
            }
    }

    fun checkEmailExists(collection: String, email: String, onResult: (Boolean) -> Unit) {
        db.collection(collection)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onResult(!querySnapshot.isEmpty)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun registerUser(
        collection: String,
        documentId: String,
        data: Map<String, Any>,
        callback: (Boolean, String?) -> Unit
    ) {
        db.collection(collection).document(documentId)
            .set(data)
            .addOnSuccessListener {
                callback(true, "Registration Successful!")
            }
            .addOnFailureListener { e ->
                callback(false, e.message)
            }
    }

    // Membaca dokumen tunggal dari Firestore
    fun getDocumentFromFirestore(
        collection: String,
        documentId: String,
        callback: (DocumentSnapshot?) -> Unit
    ) {
        db.collection(collection).document(documentId)
            .get()
            .addOnSuccessListener { document ->
                callback(document) // Sukses
            }
            .addOnFailureListener {
                callback(null) // Gagal
            }
    }

    // Membaca semua dokumen dari koleksi di Firestore
    fun getAllDocumentsFromFirestore(collection: String, callback: (QuerySnapshot?) -> Unit) {
        db.collection(collection)
            .get()
            .addOnSuccessListener { result ->
                callback(result) // Sukses
            }
            .addOnFailureListener {
                callback(null) // Gagal
            }
    }

    // Mengupdate data di Firestore
    fun updateDocumentInFirestore(
        collection: String,
        documentId: String,
        data: Map<String, Any>,
        callback: (Boolean) -> Unit
    ) {
        db.collection(collection).document(documentId)
            .update(data)
            .addOnSuccessListener {
                callback(true) // Sukses
            }
            .addOnFailureListener {
                callback(false) // Gagal
            }
    }

    // Menghapus dokumen dari Firestore
    fun deleteDocumentFromFirestore(
        collection: String,
        documentId: String,
        callback: (Boolean) -> Unit
    ) {
        db.collection(collection).document(documentId)
            .delete()
            .addOnSuccessListener {
                callback(true) // Sukses
            }
            .addOnFailureListener {
                callback(false) // Gagal
            }
    }

    // Menghapus field dari dokumen di Firestore
    fun deleteFieldInFirestore(
        collection: String,
        documentId: String,
        field: String,
        callback: (Boolean) -> Unit
    ) {
        val updates = hashMapOf<String, Any>(
            field to FieldValue.delete()
        )
        db.collection(collection).document(documentId)
            .update(updates)
            .addOnSuccessListener {
                callback(true) // Sukses
            }
            .addOnFailureListener {
                callback(false) // Gagal
            }
    }

    // Mendengarkan perubahan pada dokumen di Firestore
    fun listenToDocumentChanges(
        collection: String,
        documentId: String,
        callback: (DocumentSnapshot?) -> Unit
    ) {
        db.collection(collection).document(documentId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    callback(null) // Gagal
                    return@addSnapshotListener
                }
                callback(snapshot) // Sukses
            }
    }

    // Mendengarkan perubahan pada koleksi dokumen di Firestore
    fun listenToCollectionChanges(collection: String, callback: (QuerySnapshot?) -> Unit) {
        db.collection(collection)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    callback(null) // Gagal
                    return@addSnapshotListener
                }
                callback(snapshots) // Sukses
            }
    }

    // Menjalankan query untuk mengambil dokumen berdasarkan kondisi
    fun getDocumentsWithCondition(
        collection: String,
        field: String,
        value: Any,
        callback: (QuerySnapshot?) -> Unit
    ) {
        db.collection(collection)
            .whereEqualTo(field, value)
            .get()
            .addOnSuccessListener { result ->
                callback(result) // Sukses
            }
            .addOnFailureListener {
                callback(null) // Gagal
            }
    }
}
