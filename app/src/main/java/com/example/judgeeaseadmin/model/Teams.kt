package com.example.judgeeaseadmin.model

data class Teams(
    val id: String = "",
    val name: String = "",
    val members: List<String> = emptyList(),
    val leader: String = "",
    val eventId:String = "",
    val adminId: String = "",
    val problemStatement: String = "",
)
