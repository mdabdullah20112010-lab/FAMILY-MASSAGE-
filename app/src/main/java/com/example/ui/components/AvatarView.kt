package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.TextPrimary

@Composable
fun AvatarView(
  modifier: Modifier = Modifier,
  size: Dp = 50.dp,
  drawableName: String? = null,
  initials: String = "",
  backgroundColorHex: Long = 0xFF2A3942,
  showOnlineDot: Boolean = false,
  hasStatusStory: Boolean = false
) {
  val context = LocalContext.current
  val resId = drawableName?.let {
    when (it) {
      "avatar_gamer" -> R.drawable.avatar_gamer
      "avatar_city" -> R.drawable.avatar_city
      "avatar_person1" -> R.drawable.avatar_person1
      else -> {
        val id = context.resources.getIdentifier(it, "drawable", context.packageName)
        if (id != 0) id else null
      }
    }
  }

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    val storyModifier = if (hasStatusStory) {
      Modifier
        .size(size)
        .border(2.dp, AccentGreen, CircleShape)
        .padding(2.dp)
    } else Modifier.size(size)

    if (resId != null) {
      Image(
        painter = painterResource(id = resId),
        contentDescription = "Profile Avatar",
        modifier = storyModifier
          .clip(CircleShape),
        contentScale = ContentScale.Crop
      )
    } else if (initials == "Payoneer" || initials == "P") {
      // Sleek Payoneer stylized colorful ring avatar as in screenshot
      Box(
        modifier = storyModifier
          .clip(CircleShape)
          .background(
            Brush.sweepGradient(
              listOf(
                Color(0xFFFF5722),
                Color(0xFFFFEB3B),
                Color(0xFF00E676),
                Color(0xFF2979FF),
                Color(0xFFE040FB),
                Color(0xFFFF5722)
              )
            )
          )
          .padding(3.dp)
          .clip(CircleShape)
          .background(Color.White),
        contentAlignment = Alignment.Center
      ) {}
    } else {
      // Monogram circle
      val displayInitials = if (initials.contains(" ")) {
        val parts = initials.trim().split(" ")
        "${parts[0].take(1)}${parts.getOrNull(1)?.take(1) ?: ""}".uppercase()
      } else {
        initials.take(2).uppercase()
      }

      Box(
        modifier = storyModifier
          .clip(CircleShape)
          .background(Color(backgroundColorHex)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = displayInitials.ifEmpty { "•" },
          color = TextPrimary,
          fontSize = (size.value * 0.36f).sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    if (showOnlineDot) {
      Box(
        modifier = Modifier
          .size(size * 0.28f)
          .align(Alignment.BottomEnd)
          .clip(CircleShape)
          .background(AccentGreen)
          .border(2.dp, Color(0xFF0B1014), CircleShape)
      )
    }
  }
}
