package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.AddComment
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatEntity
import com.example.ui.components.AvatarView
import com.example.ui.components.BottomNavBar
import com.example.ui.components.MainOverflowMenu
import com.example.ui.components.NavTab
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ReadCheckmarkBlue
import com.example.ui.theme.SearchBarBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UnreadBadgeGreen
import com.example.ui.theme.VibrantGreen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
  chats: List<ChatEntity>,
  selectedTab: NavTab,
  searchQuery: String,
  isOverflowExpanded: Boolean,
  onTabSelected: (NavTab) -> Unit,
  onSearchChanged: (String) -> Unit,
  onToggleOverflow: (Boolean) -> Unit,
  onChatClicked: (String) -> Unit,
  onOpenSettings: () -> Unit,
  onOpenSelectContact: () -> Unit,
  onReadAll: () -> Unit,
  onSwitchAccount: () -> Unit,
  modifier: Modifier = Modifier
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = AmoledBackground,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    bottomBar = {
      BottomNavBar(
        selectedTab = selectedTab,
        onTabSelected = onTabSelected
      )
    },
    floatingActionButton = {
      if (selectedTab == NavTab.CHATS) {
        Column(
          horizontalAlignment = Alignment.End,
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Secondary AI quick action button as in screenshot 1
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(
                Brush.sweepGradient(
                  listOf(
                    Color(0xFF8A2BE2),
                    Color(0xFF00E676),
                    Color(0xFF00B0FF),
                    Color(0xFF8A2BE2)
                  )
                )
              )
              .clickable {
                scope.launch {
                  snackbarHostState.showSnackbar("FAMILY AI Assistant is ready to summarize or assist.")
                }
              }
              .testTag("fab_ai_assistant"),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "AI Assistant",
                tint = Color(0xFFB388FF),
                modifier = Modifier.size(22.dp)
              )
            }
          }

          // Main Green FAB (New Chat) as in screenshot 1 & 2
          FloatingActionButton(
            onClick = onOpenSelectContact,
            containerColor = VibrantGreen,
            contentColor = Color(0xFF0B1014),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .size(56.dp)
              .testTag("fab_new_chat")
          ) {
            Icon(
              imageVector = Icons.Rounded.AddComment,
              contentDescription = "New chat",
              modifier = Modifier.size(26.dp)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = innerPadding.calculateBottomPadding())
        .statusBarsPadding()
    ) {
      // Top Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "FAMILY",
          color = TextPrimary,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )

        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = {
              scope.launch {
                snackbarHostState.showSnackbar("Camera opened")
              }
            },
            modifier = Modifier.testTag("btn_camera")
          ) {
            Icon(
              imageVector = Icons.Default.PhotoCamera,
              contentDescription = "Camera",
              tint = TextPrimary
            )
          }

          Box {
            IconButton(
              onClick = { onToggleOverflow(true) },
              modifier = Modifier.testTag("btn_overflow_menu")
            ) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = TextPrimary
              )
            }

            MainOverflowMenu(
              expanded = isOverflowExpanded,
              onDismissRequest = { onToggleOverflow(false) },
              onNewGroup = {
                onToggleOverflow(false)
                onOpenSelectContact()
              },
              onBroadcastLists = {
                onToggleOverflow(false)
                scope.launch { snackbarHostState.showSnackbar("Broadcast lists") }
              },
              onLinkedDevices = {
                onToggleOverflow(false)
                scope.launch { snackbarHostState.showSnackbar("Linked devices: 1 active desktop") }
              },
              onStarred = {
                onToggleOverflow(false)
                scope.launch { snackbarHostState.showSnackbar("No starred messages yet") }
              },
              onReadAll = {
                onToggleOverflow(false)
                onReadAll()
                scope.launch { snackbarHostState.showSnackbar("All chats marked as read") }
              },
              onSettings = {
                onToggleOverflow(false)
                onOpenSettings()
              },
              onSwitchAccount = {
                onToggleOverflow(false)
                onSwitchAccount()
              }
            )
          }
        }
      }

      // Rounded Search Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
      ) {
        TextField(
          value = searchQuery,
          onValueChange = onSearchChanged,
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(CircleShape)
            .testTag("search_input_field"),
          placeholder = {
            Text(
              text = "Ask AI or Search",
              color = TextSecondary,
              fontSize = 15.sp
            )
          },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = TextSecondary,
              modifier = Modifier.size(22.dp)
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchChanged("") }) {
                Icon(
                  imageVector = Icons.Default.Clear,
                  contentDescription = "Clear",
                  tint = TextSecondary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          },
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SearchBarBackground,
            unfocusedContainerColor = SearchBarBackground,
            disabledContainerColor = SearchBarBackground,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = VibrantGreen
          ),
          singleLine = true
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      when (selectedTab) {
        NavTab.CHATS -> {
          if (chats.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(VibrantGreen.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "No messages",
                    tint = VibrantGreen,
                    modifier = Modifier.size(38.dp)
                  )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                  text = "No messages yet",
                  color = TextPrimary,
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "Your messages list is empty. Add people registered on FAMILY to start chatting! Anyone you add must have an account in the app.",
                  color = TextSecondary,
                  fontSize = 14.sp,
                  textAlign = TextAlign.Center,
                  lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                  onClick = onOpenSelectContact,
                  colors = ButtonDefaults.buttonColors(
                    containerColor = VibrantGreen,
                    contentColor = Color(0xFF0B1014)
                  ),
                  shape = RoundedCornerShape(24.dp),
                  modifier = Modifier.testTag("btn_empty_add_person")
                ) {
                  Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Add Person to Chat",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                  )
                }
              }
            }
          } else {
            // Compact Chat Rows List
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .testTag("chats_list")
            ) {
              items(chats, key = { it.id }) { chat ->
                CompactChatRow(
                  chat = chat,
                  onClick = { onChatClicked(chat.id) }
                )
              }
            }
          }
        }
        NavTab.UPDATES -> {
          UpdatesScreenContent(onOpenSettings = onOpenSettings)
        }
        NavTab.COMMUNITIES -> {
          CommunitiesScreenContent()
        }
        NavTab.CALLS -> {
          CallsScreenContent()
        }
      }
    }
  }
}

@Composable
fun CompactChatRow(
  chat: ChatEntity,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp, vertical = 10.dp)
      .testTag("chat_row_${chat.id}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    AvatarView(
      size = 52.dp,
      drawableName = chat.avatarDrawableName,
      initials = chat.title,
      backgroundColorHex = chat.avatarColorHex,
      showOnlineDot = chat.isOnline
    )

    Spacer(modifier = Modifier.width(14.dp))

    Column(
      modifier = Modifier.weight(1f)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = chat.title,
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f, fill = false)
        )
        Text(
          text = chat.lastMessageFormattedTime,
          color = if (chat.unreadCount > 0) VibrantGreen else TextSecondary,
          fontSize = 12.sp,
          fontWeight = if (chat.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (chat.isOutgoing) {
            Icon(
              imageVector = Icons.Default.DoneAll,
              contentDescription = "Read receipt",
              tint = if (chat.status == "READ") ReadCheckmarkBlue else TextMuted,
              modifier = Modifier
                .size(16.dp)
                .padding(end = 4.dp)
            )
          }

          Text(
            text = chat.lastMessage,
            color = TextSecondary,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        if (chat.unreadCount > 0) {
          Box(
            modifier = Modifier
              .padding(start = 8.dp)
              .clip(CircleShape)
              .background(UnreadBadgeGreen)
              .padding(horizontal = 7.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = chat.unreadCount.toString(),
              color = Color(0xFF0B1014),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
