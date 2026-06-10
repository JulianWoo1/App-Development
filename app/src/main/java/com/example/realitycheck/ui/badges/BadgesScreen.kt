package com.example.realitycheck.ui.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.realitycheck.data.di.SupabaseModule

private val Background = Color(0xFF050505)
private val CardBg = Color(0xFF0D0D0D)
private val Purple = Color(0xFF5B2EFF)
private val SubtitleGray = Color(0xFF9A9A9A)

@Composable
fun BadgesScreen() {
    val viewModel: BadgesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BadgesViewModel(SupabaseModule.badgeRepository) as T
            }
        }
    )

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Badges",
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        val earnedCount = state.badges.count { it.isUnlocked }
        Text(
            text = "$earnedCount / ${state.badges.size} earned",
            color = SubtitleGray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Purple)
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "An error occurred",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = { viewModel.loadBadges() }) {
                            Text("Try again", color = Purple)
                        }
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = state.badges,
                        key = { it.id }
                    ) { badge ->
                        BadgeGridItem(badge = badge)
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeGridItem(badge: BadgeUiItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (!badge.isUnlocked) Modifier.alpha(0.35f) else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = if (badge.isUnlocked) Purple.copy(alpha = 0.2f) else CardBg,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (badge.isUnlocked) {
                AsyncImage(
                    model = badge.iconUrl,
                    contentDescription = badge.name,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text(
                    text = "?",
                    color = SubtitleGray,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (badge.isUnlocked && badge.name != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = badge.name,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
