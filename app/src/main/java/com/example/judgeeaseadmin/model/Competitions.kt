package com.example.judgeeaseadmin.model

data class Competition(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val startDateTime: Long = 0L, // millis
    val endDateTime: Long = 0L,  // millis
    val status: String = "",
    val venue: String = "",
    val organizer: String = "",
){
    //To check if the competition is over
    fun isOver(now: Long = System.currentTimeMillis()):  Boolean = now > endDateTime
    //To check if the competition is active
    fun isActive(now: Long = System.currentTimeMillis()): Boolean = now <= endDateTime
}

