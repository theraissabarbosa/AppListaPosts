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
import androidx.compose.material3.Badge
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raissa.listaposts_raissa.ui.theme.ListaPosts_RaissaTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class PostDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val postId = intent.getStringExtra("post_id")
        enableEdgeToEdge()
        setContent {
            ListaPosts_RaissaTheme {
                PostDetailPage(postId ?: "")
            }
        }
    }
}


@Composable
fun PostDetailPage(postId: String){
    var state by remember { mutableStateOf<SuspendState<PostDetail>>(LoadingState()) }

    // esse treco usa como parametro postId pois toda vez que o ID DO POST mudar, ele roda de novo
    // é como se fosse efeito colateral
    LaunchedEffect(postId) {
        state = LoadingState()
        state = try {
            SuccessState(data = fetchData(postId))
        } catch (_: Exception){
            ErrorState(
                message = "Nao foi possivel carregar o post de ID: $postId"
            )
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(Modifier
            .fillMaxSize()
            .padding(innerPadding)) {

        when (val state = state){
            // estado de erro -> traz a mensagem de erro do suspend
            is ErrorState -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { // precisamos de message: String
                    Text(state.message) }
            }

            // estado de carregamento traz a bolinha girando
            is LoadingState -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            }

            is SuccessState -> {
                ElevatedCard(Modifier.padding(16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Badge{
                            // traz o post de acordo com suspend mas a partir de <PostDetail>
                            Text("Post #${state.data.id}")
                        } // titulo e corpo do post
                        Text(state.data.title)
                        Text(state.data.body)
                    }
                }
            //
                Text("Comments")
                LazyColumn {
                    items(2) {ElevatedCard(Modifier.padding(start = 16.dp, end = 8.dp)) {
                        Column(Modifier
                            .fillMaxWidth()
                            .padding(16.dp)) {
                            Text("titulo comentario")
                            Badge{
                                Text("usuario")
                            }
                            Text("textoooooo")
                        }
                    }
                    }
                }
            }
        }
            }
            }
        }






data class PostDetail(
    val id: String,
    val title: String,
    val body: String,
)

suspend fun fetchData(postId: String): PostDetail = withContext(Dispatchers.IO){
    require(postId.toIntOrNull() != null) {"Invalid postId"}
    val jsonText = URL("https://jsonplaceholder.typicode.com/posts/$postId").readText()
    val postJSON = JSONObject(jsonText)
    PostDetail(
        id = postJSON.getInt("id").toString(),
        title = postJSON.getString("title"),
        body = postJSON.getString("title")
    )

}