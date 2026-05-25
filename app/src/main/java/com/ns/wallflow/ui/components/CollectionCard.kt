package com.ns.wallflow.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.ns.wallflow.R
import com.ns.wallflow.model.Collection

@Composable
fun CollectionCard(
    collection: Collection,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(237.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color(0x3F000000),
                spotColor = Color(0x3F000000)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        if (collection.coverImagePath.startsWith("file:///android_asset/") || collection.coverImagePath.isEmpty()) {
            Image(
                painterResource(R.drawable.collection_placeholder), // Fallback/default image
                contentDescription = "Collection Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(collection.coverImagePath)
                    .build(),
                contentDescription = "Collection Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(44.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        Text(
            text = collection.name,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 22.dp, bottom = 12.dp),
            style = TextStyle(
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.25f),
                    offset = Offset(0f, 4f),
                    blurRadius = 4f
                )
            )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 13.dp, bottom = 6.dp)
                .size(42.dp, 19.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(45.dp),
                    ambientColor = Color(0x3F000000),
                    spotColor = Color(0x3F000000)
                )
                .background(Color.White.copy(alpha = 0.10f), RoundedCornerShape(45.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${collection.totalWallpapers}",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}
