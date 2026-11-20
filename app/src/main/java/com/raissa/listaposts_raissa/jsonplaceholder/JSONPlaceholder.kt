package com.raissa.listaposts_raissa.jsonplaceholder

import com.raissa.listaposts_raissa.models.PostListItem
import kotlinx.serialization.Serializable
import com.raissa.listaposts_raissa.models.PostDetail as AppPostDetail
import com.raissa.listaposts_raissa.models.PostListItem as AppPostListItem


object JSONPlaceholder {
    // modelos da api (dto)
    @Serializable
    data class PostDetail(
        val id: Int,
        val userId: Int,
        val title: String,
        val body: String,
    ){
        fun toPostDetail(comment: List<AppPostDetail.Comment>) : AppPostDetail{
            return AppPostDetail(
                id = id.toString(),
                title = title,
                body = body,
                comments = comment
            )
        }
    }
    @Serializable
    data class PostDetailComment(
        val id: Int,
        val postId: Int,
        val name: String,
        val email: String,
        val body: String,
    ) {
        fun toComment(): AppPostDetail.Comment{
            return AppPostDetail.Comment(
                id = id.toString(),
                title = name,
                commenterEmail = email,
                body = body
            )
        }
    }

    // ---------- Modelo de dados ----------
    @Serializable
    data class PostLstItem(
        val id: Int,
        val userId: Int,
        val title: String,
        val body: String,
    ){
        fun toPostListItem(): PostListItem {
            return PostListItem(
                title = title,
                description = body,
                userId = userId.toString(),
                postId = id.toString()
            )
        }
    }
}