package com.example.judgeeaseadmin.model

import com.google.firebase.firestore.Exclude
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class Competition(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val startDateTime: Long? = null,
    val endDateTime: Long? = null,
    val status: String = "",
    val venue: String = "",
    val organizer: String = "",
    val adminId: String =  "",
    val teams: List<Teams> = emptyList()
){
    // ✅ STEP 2: These helpers are HIDDEN from Firestore
    // They assemble the "window" (LocalDateTime) for your app to use.
    @get:Exclude
    val startDateTimeMillis: LocalDateTime?
        get() = startDateTime?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }

    @get:Exclude
    val endDateTimeMillis: LocalDateTime?
        get() = endDateTime?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }

    // Your logic functions work correctly with the helpers
    private fun isOver(now: LocalDateTime = LocalDateTime.now()): Boolean {
        return endDateTimeMillis?.let { now > it } ?: true
    }

    fun isActive(now: LocalDateTime = LocalDateTime.now()): Boolean = !isOver(now)
}

