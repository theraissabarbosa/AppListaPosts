package com.raissa.listaposts_raissa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.raissa.listaposts_raissa.models.PostListItem
import com.raissa.listaposts_raissa.ui.composables.PostDetailPage
import com.raissa.listaposts_raissa.ui.composables.PostListPage
import com.raissa.listaposts_raissa.ui.theme.ListaPosts_RaissaTheme
import kotlinx.serialization.Serializable



// ---------- Activity ----------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        enableEdgeToEdge()
        setContent {
            ListaPosts_RaissaTheme {
                // pilha
                val backStack = remember { mutableStateListOf<Any>(AppRoute.PostList) }
                NavDisplay(
                    backStack = backStack,
                    onBack = {backStack.removeLastOrNull()},
                    entryProvider = { key ->
                        when (key) {
                            is AppRoute.PostList -> NavEntry(key){
                                PostListPage { post ->
                                    backStack.add(AppRoute.PostDetail(post.postId))
                                }
                            }
                            is AppRoute.PostDetail -> NavEntry(key){
                                PostDetailPage(key.id)
                            }
                            else -> NavEntry(key){
                                Text("404 : Página não encontrada")
                            }
                        }

                    }
                )
            }
        }
    }
}

sealed class AppRoute{
    data object PostList
    data class PostDetail(val id: String)
}
