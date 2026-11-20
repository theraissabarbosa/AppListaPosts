package com.raissa.listaposts_raissa.models

// modelos do app
data class PostDetail(
    val id: String,
    val title: String,
    val body: String,
    val comments: List<Comment>
) {

    data class Comment(
        val id: String,
        val title: String,
        val commenterEmail: String,
        val body: String,
    )
}
