package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccountEntity
import com.example.ui.components.AvatarView
import com.example.ui.screens.AuthTab
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantGreen
import kotlinx.coroutines.launch

data class SettingsItemData(
  val icon: ImageVector,
  val title: String,
  val subtitle: String
)

@Composable
fun SettingsScreen(
  onNavigateBack: () -> Unit,
  activeAccount: UserAccountEntity? = null,
  onOpenAccountSwitcher: () -> Unit = {},
  onOpenAuth: (AuthTab) -> Unit = {},
  onSignOut: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  val settingsItems = listOf(
    SettingsItemData(Icons.Default.Diamond, "Subscriptions", "Explore premium benefits"),
    SettingsItemData(Icons.Default.Devices, "Linked devices", "Use FAMILY on other devices"),
    SettingsItemData(Icons.Default.VpnKey, "Account", "Security notifications, change number"),
    SettingsItemData(Icons.Default.Lock, "Privacy", "Blocked accounts, disappearing messages"),
    SettingsItemData(Icons.Default.ViewList, "Lists", "Manage people and groups"),
    SettingsItemData(Icons.Default.ChatBubble, "Chats", "Chat history, backup"),
    SettingsItemData(Icons.Default.Notifications, "Notifications", "Message, group & call tones"),
    SettingsItemData(Icons.Default.DataUsage, "Storage and data", "Network usage, auto-download"),
    SettingsItemData(Icons.Default.Language, "App language", "English (device's language)"),
    SettingsItemData(Icons.Default.HelpOutline, "Help", "Help center, contact us, privacy policy"),
    SettingsItemData(Icons.Default.GroupAdd, "Invite a friend", "Spread the word about FAMILY")
  )

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = AmoledBackground,
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .statusBarsPadding()
    ) {
      // Top Navigation Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier.testTag("btn_settings_back")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimary
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { scope.launch { snackbarHostState.showSnackbar("Search settings") } }) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
          }
          IconButton(onClick = { scope.launch { snackbarHostState.showSnackbar("My QR Code: @boylysmfjjfj") } }) {
            Icon(Icons.Default.QrCode, contentDescription = "QR Code", tint = TextPrimary)
          }
          IconButton(onClick = { scope.launch { snackbarHostState.showSnackbar("Edit profile") } }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextPrimary)
          }
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("settings_list")
      ) {
        // Profile Header Section with stylish wallpaper doodle backing
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color(0xFF141F26),
                    AmoledBackground
                  )
                )
              )
              .padding(top = 16.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              // Status Bubble as shown in Screenshot 3: "Today in emoji..."
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .background(DarkSurfaceVariant.copy(alpha = 0.85f))
                  .clickable {
                    scope.launch { snackbarHostState.showSnackbar("Status: ${activeAccount?.status ?: "Today in emoji..."}") }
                  }
                  .padding(horizontal = 14.dp, vertical = 6.dp)
                  .testTag("status_emoji_bubble"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = activeAccount?.status ?: "Today in emoji...",
                  color = TextSecondary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Large Circular Avatar
              AvatarView(
                size = 96.dp,
                drawableName = activeAccount?.avatarDrawableName ?: "avatar_gamer",
                initials = activeAccount?.fullName ?: "BK",
                backgroundColorHex = activeAccount?.avatarColorHex ?: 0xFFFF7043,
                modifier = Modifier.clickable { onOpenAccountSwitcher() }
              )

              Spacer(modifier = Modifier.height(14.dp))

              // Name with chevron
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .clickable { onOpenAccountSwitcher() }
                  .testTag("settings_profile_row")
              ) {
                Text(
                  text = activeAccount?.fullName ?: "boekfjmdhn",
                  color = TextPrimary,
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.KeyboardArrowDown,
                  contentDescription = "Switch profile",
                  tint = TextSecondary,
                  modifier = Modifier.size(20.dp)
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = activeAccount?.username ?: "@boylysmfjjfj",
                color = TextSecondary,
                fontSize = 14.sp
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Account actions quick bar
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = onOpenAccountSwitcher,
                  modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .testTag("btn_settings_switch_account"),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = DarkSurfaceVariant,
                    contentColor = TextPrimary
                  ),
                  shape = RoundedCornerShape(19.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = VibrantGreen
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Switch", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                  onClick = { onOpenAuth(AuthTab.SIGN_UP) },
                  modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .testTag("btn_settings_signup"),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = DarkSurfaceVariant,
                    contentColor = TextPrimary
                  ),
                  shape = RoundedCornerShape(19.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = VibrantGreen
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Sign Up", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                  onClick = { onOpenAuth(AuthTab.SIGN_IN) },
                  modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .testTag("btn_settings_signin"),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = DarkSurfaceVariant,
                    contentColor = TextPrimary
                  ),
                  shape = RoundedCornerShape(19.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.VpnKey,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = VibrantGreen
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Sign In", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
              }
            }
          }

          HorizontalDivider(thickness = 0.6.dp, color = BorderDivider)
          Spacer(modifier = Modifier.height(8.dp))
        }

        // Settings Items List
        items(settingsItems) { item ->
          SettingsRow(
            item = item,
            onClick = {
              if (item.title == "Account") {
                onOpenAccountSwitcher()
              } else {
                scope.launch {
                  snackbarHostState.showSnackbar("${item.title} settings")
                }
              }
            }
          )
        }

        // Sign Out option item
        item {
          HorizontalDivider(thickness = 0.5.dp, color = BorderDivider, modifier = Modifier.padding(vertical = 8.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onSignOut() }
              .padding(horizontal = 20.dp, vertical = 14.dp)
              .testTag("settings_row_sign_out"),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Logout,
              contentDescription = "Sign out",
              tint = Color(0xFFFF8A80),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
              Text(
                text = "Sign out",
                color = Color(0xFFFF8A80),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "Log out from this FAMILY account",
                color = TextSecondary,
                fontSize = 13.sp
              )
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(24.dp))
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "from",
              color = TextMuted,
              fontSize = 12.sp
            )
            Text(
              text = "VELO LABS",
              color = TextSecondary,
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
          }
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }
}

@Composable
fun SettingsRow(
  item: SettingsItemData,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 20.dp, vertical = 14.dp)
      .testTag("settings_row_${item.title.lowercase().replace(" ", "_")}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = item.icon,
      contentDescription = item.title,
      tint = TextSecondary,
      modifier = Modifier.size(24.dp)
    )

    Spacer(modifier = Modifier.width(20.dp))

    Column(
      modifier = Modifier.weight(1f)
    ) {
      Text(
        text = item.title,
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
      )
      if (item.subtitle.isNotEmpty()) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = item.subtitle,
          color = TextSecondary,
          fontSize = 13.sp
        )
      }
    }
  }
}
