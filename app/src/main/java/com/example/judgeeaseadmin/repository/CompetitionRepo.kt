package com.example.judgeeaseadmin.repository

import com.example.judgeeaseadmin.model.Competition
import com.example.judgeeaseadmin.model.Teams
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId

@Suppress("UNCHECKED_CAST")
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

    fun getCompetitionById(compId: String): Flow<Competition?> {
        val docRef = competitionsCollection.document(compId)

        return docRef.snapshots().map { snapshot ->
            if (snapshot.exists()) {
                // Manually deserialize the teams list
                val competition = snapshot.toObject<Competition>()
                val teamsData = snapshot.get("teams") as? List<HashMap<String, Any>> ?: emptyList()

                val teamsList = teamsData.map { teamMap ->
                    Teams(
                        id = teamMap["id"] as? String ?: "",
                        name = teamMap["name"] as? String ?: "No Name",
                        leader = teamMap["leader"] as? String ?: "N/A",
                        problemStatement = teamMap["problemStatement"] as? String ?: "",
                        members = teamMap["members"] as? List<String> ?: emptyList(),
                        eventId = teamMap["eventId"] as? String ?: "",
                        adminId = teamMap["adminId"] as? String ?: ""
                    )
                }
                // Return the competition object with the correctly mapped teams list
                competition?.copy(teams = teamsList)
            } else {
                null
            }
        }
    }

    suspend fun createCompetition(title: String, desc: String, venue: String, organizer: String, startDateTime: LocalDateTime, endDateTime: LocalDateTime, status: String = "active") {
        val startMillis = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val adminUid = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("Admin not found")
        val docRef = competitionsCollection.document()
        val comp = Competition(id = docRef.id, name = title, description = desc , venue = venue, organizer = organizer, startDateTime = startMillis, endDateTime = endMillis, status = status, adminId = adminUid)
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

    suspend fun addTeam(compId: String, team: Teams) {
        try {
            // Get the reference to the specific competition document
            val competitionRef = competitionsCollection.document(compId)
            competitionRef.update("teams", FieldValue.arrayUnion(team)).await()
        } catch (e: Exception) {
            // Handle exceptions, e.g., log the error or expose it to the UI
            println("Error adding team: ${e.message}")
        }
    }

    suspend fun deleteCompetition(id: String) {
        competitionsCollection.document(id).delete().await()
    }
}
