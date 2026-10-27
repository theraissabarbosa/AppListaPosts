package com.raissa.listaposts_raissa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.raissa.listaposts_raissa.ui.theme.ListaPosts_RaissaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

// ---------- Modelo de dados ----------

data class PostListItem(
    val title: String,
    val description: String,
    val userId: String,
    val postId: String
)

// ---------- Activity ----------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListaPosts_RaissaTheme {
                PostListPage()
            }
        }
    }
}

// ---------- Tela: carrega, mostra erro ou mostra a lista ----------

@Composable
fun PostListPage() {
    var posts by remember { mutableStateOf<List<PostListItem>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isLoading = posts == null && errorMessage == null

    LaunchedEffect(Unit) {
        try {
            posts = downloadPosts()
        } catch (e: Exception) {
            errorMessage = "Algo deu errado"
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when {
            isLoading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            errorMessage != null -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text(errorMessage!!) }

            else -> LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = innerPadding)
            ) {
                items(posts ?: emptyList(), key = { it.postId }) {
                    PostListTile(it)
                }
            }
        }
    }
}

// ---------- Item da lista ----------

@Composable
fun PostListTile(post: PostListItem) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            "User ID: ${post.userId} • Post #${post.postId}",
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            post.title,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            post.description,
            maxLines = 3
        )

        HorizontalDivider(Modifier)
    }
}

// ---------- Download dos posts (JSON) ----------

suspend fun downloadPosts(): List<PostListItem> = withContext(Dispatchers.IO) {
    val jsonResponse = URL("https://jsonplaceholder.typicode.com/posts").readText()
    val jsonList = JSONArray(jsonResponse)
    List(jsonList.length()) { index ->
        val jsonItem = jsonList.getJSONObject(index)
        PostListItem(
            title = jsonItem.get("title") as String,
            description = jsonItem.getString("body"),
            userId = jsonItem.getInt("userId").toString(),
            postId = jsonItem.getInt("id").toString()
        )
    }
}

// ---------- Preview ----------

@Preview(showBackground = true)
@Composable
fun PostListTilePreview() {
    ListaPosts_RaissaTheme {
        PostListTile(
            PostListItem(
                title = "Título de exemplo",
                description = "Texto de exemplo do post, só para visualizar o item da lista.",
                userId = "1",
                postId = "1"
            )
        )
    }
}