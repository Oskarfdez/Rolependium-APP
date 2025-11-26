package com.example.login.presentation.menu



import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.login.presentation.components.BottomBar
import com.example.login.presentation.components.TopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.login.Data.DataViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ViewModelConstructorInComposable")
@Composable
fun MenuScreen(
    onNavigateToDice: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSpell: () -> Unit,
    onBack: () -> Unit,
    email: String
) {
    Scaffold(
        topBar = { TopBar(onNavigateToSpell,onBack) },
        bottomBar = {BottomBar(modifier = Modifier, onNavigateToDice, onNavigateToList,onNavigateToHome)}
    ) {
        Column {
            Spacer(modifier = Modifier.height(55.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Noticias",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(30.dp))
                NewsList(viewModel = DataViewModel())

            }

        }

    }
}

@Composable
fun NewsList(viewModel: DataViewModel) {
    val newsState = viewModel.newsList.observeAsState(emptyList())
    val news = newsState.value

    LaunchedEffect(Unit) {
        viewModel.listarNoticias()
    }

    LazyColumn {
        items(news.size) { index ->
            val item = news[index]
            Column(modifier = Modifier.padding(16.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(item.imagen),
                            contentDescription = "titulo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.titulo,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.subtitulo,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}





@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MenuScreen(onNavigateToDice = {}, onNavigateToList = {}, onNavigateToUser = {}, onNavigateToHome = {}, onBack = {}, onNavigateToSpell = {}, email = "")
}
