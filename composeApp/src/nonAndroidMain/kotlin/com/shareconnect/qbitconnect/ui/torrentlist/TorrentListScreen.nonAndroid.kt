package com.shareconnect.qbitconnect.ui.torrentlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import qbitconnect.composeapp.generated.resources.Res
import qbitconnect.composeapp.generated.resources.ic_launcher

@Composable
actual fun AppIcon(modifier: Modifier) {
    Image(
        painter = painterResource(Res.drawable.ic_launcher),
        contentDescription = null,
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(16.dp)),
    )
}
