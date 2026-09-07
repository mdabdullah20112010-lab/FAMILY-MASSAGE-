package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.DarkPopupSurface
import com.example.ui.theme.TextPrimary

@Composable
fun MainOverflowMenu(
  expanded: Boolean,
  onDismissRequest: () -> Unit,
  onNewGroup: () -> Unit,
  onBroadcastLists: () -> Unit,
  onLinkedDevices: () -> Unit,
  onStarred: () -> Unit,
  onReadAll: () -> Unit,
  onSettings: () -> Unit,
  onSwitchAccount: () -> Unit,
  modifier: Modifier = Modifier
) {
  DropdownMenu(
    expanded = expanded,
    onDismissRequest = onDismissRequest,
    offset = DpOffset(x = (-8).dp, y = 8.dp),
    modifier = modifier
      .width(200.dp)
      .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp))
      .clip(RoundedCornerShape(16.dp))
      .background(DarkPopupSurface)
      .testTag("overflow_popup_menu")
  ) {
    val items = listOf(
      "New group" to onNewGroup,
      "Broadcast lists" to onBroadcastLists,
      "Linked devices" to onLinkedDevices,
      "Starred" to onStarred,
      "Read all" to onReadAll,
      "Settings" to onSettings,
      "Switch account" to onSwitchAccount
    )

    items.forEachIndexed { index, (label, action) ->
      DropdownMenuItem(
        text = {
          Text(
            text = label,
            color = TextPrimary,
            fontSize = 15.sp
          )
        },
        onClick = {
          onDismissRequest()
          action()
        },
        modifier = Modifier.testTag("menu_item_${label.lowercase().replace(" ", "_")}")
      )
      if (index == 4) { // small divider before Settings
        HorizontalDivider(thickness = 0.5.dp, color = BorderDivider)
      }
    }
  }
}
