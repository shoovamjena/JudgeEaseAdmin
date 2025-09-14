package com.example.judgeeaseadmin.repository

import com.example.judgeeaseadmin.model.Competition
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CompetitionRepository(
    db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val competitionsCollection = db.collection("events")

    fun getCompetitions(): Flow<List<Competition>> = callbackFlow {
        val listener = competitionsCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val comps = snapshot?.documents?.mapNotNull { it.toObject(Competition::class.java) } ?: emptyList()
            trySend(comps)
        }
        awaitClose { listener.remove() }
    }

    fun getCompetitionById(id: String): Flow<Competition?> = callbackFlow {
        val listener = competitionsCollection.document(id).addSnapshotListener { snapshot, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val comp = snapshot?.toObject(Competition::class.java)
            trySend(comp)
        }
        awaitClose { listener.remove() }
    }

    suspend fun createCompetition(title: String, desc: String, venue: String, organizer: String, startDateTime: Long, endDateTime: Long, status: String = "active") {
        val docRef = competitionsCollection.document()
        val comp = Competition(id = docRef.id, name = title, description = desc , venue = venue, organizer = organizer, startDateTime = startDateTime, endDateTime = endDateTime, status = status)
        docRef.set(comp).await()
    }

    suspend fun updateCompetition(id: String, newTitle: String, newDesc: String) {
        competitionsCollection.document(id).update(
            mapOf(
                "title" to newTitle,
                "description" to newDesc
            )
        ).await()
    }

    suspend fun addTeam(id: String, team: String) {
        competitionsCollection.document(id).update(
            "teams", FieldValue.arrayUnion(team)
        ).await()
    }

    suspend fun deleteCompetition(id: String) {
        competitionsCollection.document(id).delete().await()
    }
}
