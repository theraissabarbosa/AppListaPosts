package com.raissa.listaposts_raissa

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.raissa.listaposts_raissa.ui.theme.ListaPosts_RaissaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
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
                PostListPage {
                    val intent = Intent(this, PostDetailActivity::class.java)
                    intent.putExtra("post_id", it.postId)
                    startActivity(intent)
                }
            }
        }
    }
}

// ---------- Tela: carrega, mostra erro ou mostra a lista ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListPage(onPostClick: (PostListItem) -> Unit) {
    var posts by remember { mutableStateOf<List<PostListItem>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isLoading = posts == null && errorMessage == null
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            posts = downloadPosts()
        } catch (e: Exception) {
            errorMessage = "Algo deu errado"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Posts") },
                actions = {
                    TextButton(
                        // se isloading for falso, botão retry fica cinza, nao deixa carregar
                        enabled = !isLoading,
                        onClick = {
                            scope.launch {
                                posts = null
                                errorMessage = null
                                try {
                                    posts = downloadPosts()
                                } catch (e: Exception) {
                                    errorMessage = "Algo deu errado"
                                }
                            }
                        }
                    ){
                        Text("Retry")
                    }
                }
            )
        }

    ) { innerPadding ->
        val errorMessageSnapshot = errorMessage
        when {
            isLoading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            errorMessageSnapshot != null -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text(errorMessageSnapshot) }

            else -> LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .padding(paddingValues = innerPadding)
            ) {
                items(posts ?: emptyList(), key = { it.postId }) {
                    PostListTile(it){
                        onPostClick(it)
                    }

                }
            }
        }
    }
}

// ---------- Item da lista ----------

@Composable
fun PostListTile(post: PostListItem, onClick: ()-> Unit) {
    Column(Modifier.fillMaxWidth().clickable{ onClick()}) {
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
            ),
            onClick = {}
        )
    }
}

