package com.azhar.dongeng.utils

import com.azhar.dongeng.model.ModelMain
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.UUID

class FirebaseHelper {

    private val db = FirebaseFirestore.getInstance()

    fun loginUser(
        collection: String,
        email: String,
        password: String,
        onResult: (Boolean, String?, String?, String?) -> Unit
    ) {
        db.collection(collection)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]

                    val storedPassword = userDocument.getString("password")

                    if (storedPassword == password) {
                        onResult(
                            true,
                            "Login successful",
                            userDocument.getString("id"),
                            userDocument.getString("name")
                        )
                    } else {
                        onResult(false, "Incorrect password", null, null)
                    }
                } else {
                    onResult(false, "Email not registered!", null, null)
                }
            }
            .addOnFailureListener { exception ->
                onResult(false, "Error logging in: ${exception.message}", null, null)
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

    // new
    fun insertDataToFirebase(title: String, story: String, callback: (Boolean, String?) -> Unit) {
        val id = UUID.randomUUID().toString()

        val data = mapOf(
            "id" to id,
            "strJudul" to title,
            "strCerita" to story
        )

        db.collection("dongeng")
            .document(id)
            .set(data)
            .addOnSuccessListener {
                callback(true, "Data berhasil ditambahkan!")
            }
            .addOnFailureListener { e ->
                callback(false, "Gagal menambahkan data: ${e.message}")
            }
    }

    fun getAllDataFromFirebase(
        callback: FirebaseCallback?
    ) {
        if (callback == null) {
            println("Callback is null!")
            return
        }
        db.collection("dongeng")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val dataList = mutableListOf<ModelMain>()
                for (document in querySnapshot.documents) {
                    val data = document.toObject(ModelMain::class.java)
                    if (data != null) {
                        dataList.add(data)
                    }
                }
                callback.onComplete(true, dataList)
            }
            .addOnFailureListener { e ->
                println("Gagal mengambil data: ${e.message}")
                callback.onComplete(false, null)
            }
    }

    fun addFavorite(
        id: String,
        title: String,
        story: String,
        idUser: String,
        callback: FavoriteCallback
    ) {
        val data = mapOf(
            "id" to id,
            "strJudul" to title,
            "strCerita" to story,
            "idUser" to idUser
        )

        db.collection("favorite")
            .document(id)
            .set(data)
            .addOnSuccessListener {
                callback.onComplete(true)
            }
            .addOnFailureListener { e ->
                println("addFavorite: ${e.message}")
                callback.onComplete(false)
            }
    }

    fun removeFavorite(
        idUser: String,
        callback: FavoriteCallback
    ) {
        db.collection("favorite")
            .whereEqualTo("idUser", idUser)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        document.reference.delete()
                    }
                    callback.onComplete(true)
                } else {
                    callback.onComplete(false)
                }
            }
            .addOnFailureListener { e ->
                println("removeFavorite: ${e.message}")
                callback.onComplete(false)
            }
    }

    fun favoriteExist(
        idStory: String,
        idUser: String,
        callback: FavoriteCallback
    ) {
        db.collection("favorite")
            .whereEqualTo("id", idStory)
            .whereEqualTo("idUser", idUser)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val exists = task.result != null && !task.result.isEmpty
                    callback.onComplete(exists)
                } else {
                    println("favoriteExist: " + task.exception?.message)
                    callback.onComplete(false)
                }
            }
            .addOnFailureListener { e ->
                println("favoriteExist: ${e.message}")
                callback.onComplete(false)
            }
    }

    fun getAllDataFavorite(
        idUser: String,
        callback: (Boolean, MutableList<ModelMain>?) -> Unit
    ) {
        db.collection("favorite")
            .whereEqualTo("idUser", idUser)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val dataList = mutableListOf<ModelMain>()
                for (document in querySnapshot.documents) {
                    val data = document.toObject(ModelMain::class.java)
                    if (data != null) {
                        dataList.add(data)
                    }
                }
                callback(true, dataList)
            }
            .addOnFailureListener { e ->
                println("getAllDataFavorite: ${e.message}")
                callback(false, null)
            }
    }
}

interface FirebaseCallback {
    fun onComplete(success: Boolean, dataList: MutableList<ModelMain>?)
}

interface FavoriteCallback {
    fun onComplete(isFavorite: Boolean)
}
