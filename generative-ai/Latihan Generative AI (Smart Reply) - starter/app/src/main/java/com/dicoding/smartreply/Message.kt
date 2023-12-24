package com.dicoding.smartreply

data class Message(
    val text: String,
    val isLocalUser: Boolean,
    val timestamp: Long
)
