package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.MotionPhotosOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.ActiveTabIcon
import com.example.ui.theme.ActiveTabPill
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class NavTab(val title: String) {
  CHATS("Chats"),
  UPDATES("Updates"),
  COMMUNITIES("Communities"),
  CALLS("Calls")
}

@Composable
fun BottomNavBar(
  selectedTab: NavTab,
  onTabSelected: (NavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(AmoledBackground)
  ) {
    HorizontalDivider(thickness = 0.6.dp, color = BorderDivider)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .height(68.dp)
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      NavTabItem(
        tab = NavTab.CHATS,
        isSelected = selectedTab == NavTab.CHATS,
        selectedIcon = Icons.Filled.Chat,
        unselectedIcon = Icons.Outlined.ChatBubbleOutline,
        hasDot = false,
        onClick = { onTabSelected(NavTab.CHATS) }
      )
      NavTabItem(
        tab = NavTab.UPDATES,
        isSelected = selectedTab == NavTab.UPDATES,
        selectedIcon = Icons.Filled.MotionPhotosOn,
        unselectedIcon = Icons.Outlined.MotionPhotosOn,
        hasDot = true,
        onClick = { onTabSelected(NavTab.UPDATES) }
      )
      NavTabItem(
        tab = NavTab.COMMUNITIES,
        isSelected = selectedTab == NavTab.COMMUNITIES,
        selectedIcon = Icons.Filled.Groups,
        unselectedIcon = Icons.Outlined.Groups,
        hasDot = false,
        onClick = { onTabSelected(NavTab.COMMUNITIES) }
      )
      NavTabItem(
        tab = NavTab.CALLS,
        isSelected = selectedTab == NavTab.CALLS,
        selectedIcon = Icons.Filled.Call,
        unselectedIcon = Icons.Outlined.Call,
        hasDot = false,
        onClick = { onTabSelected(NavTab.CALLS) }
      )
    }
  }
}

@Composable
private fun NavTabItem(
  tab: NavTab,
  isSelected: Boolean,
  selectedIcon: ImageVector,
  unselectedIcon: ImageVector,
  hasDot: Boolean,
  onClick: () -> Unit
) {
  val pillBgColor by animateColorAsState(
    targetValue = if (isSelected) ActiveTabPill else Color.Transparent,
    animationSpec = tween(durationMillis = 200),
    label = "pillBgColor"
  )
  val iconColor by animateColorAsState(
    targetValue = if (isSelected) ActiveTabIcon else TextSecondary,
    animationSpec = tween(durationMillis = 200),
    label = "iconColor"
  )
  val labelColor by animateColorAsState(
    targetValue = if (isSelected) TextPrimary else TextSecondary,
    animationSpec = tween(durationMillis = 200),
    label = "labelColor"
  )

  Column(
    modifier = Modifier
      .minimumInteractiveComponentSize()
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
      )
      .testTag("nav_tab_${tab.name.lowercase()}"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .width(64.dp)
        .height(32.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(pillBgColor),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = if (isSelected) selectedIcon else unselectedIcon,
        contentDescription = tab.title,
        tint = iconColor,
        modifier = Modifier.size(22.dp)
      )
      if (hasDot) {
        Box(
          modifier = Modifier
            .size(7.dp)
            .align(Alignment.TopEnd)
            .padding(end = 2.dp, top = 2.dp)
            .clip(CircleShape)
            .background(AccentGreen)
        )
      }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = tab.title,
      color = labelColor,
      fontSize = 12.sp,
      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    )
  }
}
