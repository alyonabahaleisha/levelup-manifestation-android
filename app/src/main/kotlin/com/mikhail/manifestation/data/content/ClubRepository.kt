package com.mikhail.manifestation.data.content

// iOS Migration: -> ClubRepository.swift — @Singleton -> actor, suspend fun -> async throws

import com.google.firebase.firestore.FirebaseFirestore
import com.mikhail.manifestation.data.model.Club
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class ClubRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun getClubs(): List<Club> = suspendCancellableCoroutine { cont ->
        val task = firestore.collection("clubs")
            .get()

        task.addOnSuccessListener { snapshot ->
            val clubs = snapshot.documents.mapNotNull { doc ->
                val country = doc.getString("country") ?: return@mapNotNull null
                val city = doc.getString("city") ?: return@mapNotNull null
                val region = doc.getString("region") ?: return@mapNotNull null
                val leaderName = doc.getString("leader") ?: ""
                val telegramUrl = doc.getString("telegramUrl")
                    ?: return@mapNotNull null
                val latitude = doc.getDouble("latitude") ?: return@mapNotNull null
                val longitude = doc.getDouble("longitude") ?: return@mapNotNull null
                Club(
                    id = doc.id,
                    country = country,
                    city = city,
                    region = region,
                    leaderName = leaderName,
                    telegramUrl = telegramUrl,
                    latitude = latitude,
                    longitude = longitude
                )
            }
            if (cont.isActive) cont.resume(clubs)
        }

        task.addOnFailureListener { exception ->
            if (cont.isActive) cont.resumeWithException(exception)
        }
    }
}
