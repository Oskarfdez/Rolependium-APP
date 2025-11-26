package com.example.login.admin.news

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.login.Data.DataViewModel
import com.example.login.R
import com.example.login.admin.admincomponents.AdminTopBar
import com.example.login.presentation.components.BottomBar
import com.example.login.presentation.components.TopBar
import com.example.login.presentation.menu.MenuViewModel
import com.example.login.ui.theme.ROLERED
import java.nio.file.WatchEvent

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NewsScreen(onNavigateToNewsCreator: () -> Unit, onNavigateToSingOut: () -> Unit){
    val viewModel: DataViewModel = hiltViewModel()
    Scaffold(
        topBar = { AdminTopBar(onBack = { onNavigateToSingOut() }) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
        ){
            Spacer(modifier = Modifier.height(70.dp))
            Button(
                onClick = {onNavigateToNewsCreator()},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ROLERED)
            ) {
                Text("Añadir noticias", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            NewsList(viewModel)
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
    }
}
