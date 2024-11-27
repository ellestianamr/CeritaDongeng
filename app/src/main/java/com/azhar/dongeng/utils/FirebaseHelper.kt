package com.azhar.dongeng.utils

import com.azhar.dongeng.model.ModelMain
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class FirebaseHelper {

    private val db = FirebaseFirestore.getInstance()
    private val collectionPath = "dongeng"

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

    private fun formatDateTime(): String {
        val timezone = TimeZone.getTimeZone("UTC+7")

        val dateFormat = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm:ss a z", Locale.ENGLISH)
        dateFormat.timeZone = timezone

        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    // new
    fun insertDataToFirebase(title: String, story: String, callback: (Boolean, String?) -> Unit) {
        val id = UUID.randomUUID().toString()

        val data = mapOf(
            "id" to id,
            "strJudul" to title,
            "strCerita" to story,
            "date" to formatDateTime()
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

    fun editData(id: String, title: String, story: String, callback: (Boolean, String?) -> Unit) {
        val data = mapOf(
            "id" to id,
            "strJudul" to title,
            "strCerita" to story,
            "date" to formatDateTime()
        )

        db.collection(collectionPath)
            .document(id)
            .set(data)
            .addOnSuccessListener {
                callback(true, "Data berhasil diperbarui!")
            }
            .addOnFailureListener { e ->
                callback(false, "Gagal memperbarui data: ${e.message}")
            }
    }

    fun deleteData(id: String, callback: FavoriteCallback) {
        db.collection(collectionPath)
            .document(id)
            .delete()
            .addOnSuccessListener {
                callback.onComplete(true)
            }
            .addOnFailureListener { e ->
                println("Gagal menghapus data: ${e.message}")
                callback.onComplete(false)
            }
    }

    fun getAllDataFromFirebase(
        callback: FirebaseCallback?
    ) {
        if (callback == null) {
            println("Callback is null!")
            return
        }
        db.collection(collectionPath)
            .orderBy("date", Query.Direction.DESCENDING)
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
            "idUser" to idUser,
            "date" to formatDateTime()
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
        idStory: String,
        idUser: String,
        callback: FavoriteCallback
    ) {
        db.collection("favorite")
            .whereEqualTo("id", idStory)
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
