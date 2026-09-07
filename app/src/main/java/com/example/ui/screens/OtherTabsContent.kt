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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AvatarView
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantGreen

@Composable
fun UpdatesScreenContent(
  onOpenSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(AmoledBackground)
      .padding(horizontal = 16.dp)
      .testTag("updates_tab_content")
  ) {
    item {
      Text(
        text = "Status",
        color = TextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 12.dp)
      )

      // My Status Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { }
          .padding(vertical = 8.dp)
          .testTag("my_status_row"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box {
          AvatarView(
            size = 54.dp,
            drawableName = "avatar_gamer",
            initials = "BK"
          )
          Box(
            modifier = Modifier
              .size(22.dp)
              .align(Alignment.BottomEnd)
              .clip(CircleShape)
              .background(VibrantGreen),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add status",
              tint = Color(0xFF0B1014),
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = "My status",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "Tap to add status update",
            color = TextSecondary,
            fontSize = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Recent updates",
        color = TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
      )
    }

    // Status items from contacts
    val statusUpdates = listOf(
      Triple("F Rahman", "25 minutes ago", "avatar_person1"),
      Triple("Masud Vai", "Today, 1:45 PM", "avatar_city"),
      Triple("Raju Ahmed", "Today, 10:12 AM", "avatar_gamer"),
      Triple("Nishoo 🌻", "Yesterday, 11:30 PM", null)
    )

    items(statusUpdates.size) { index ->
      val (name, time, avatar) = statusUpdates[index]
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { }
          .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AvatarView(
          size = 54.dp,
          drawableName = avatar,
          initials = name,
          hasStatusStory = true
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = name,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = time,
            color = TextSecondary,
            fontSize = 14.sp
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
      HorizontalDivider(thickness = 0.5.dp, color = BorderDivider)
      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Channels",
        color = TextPrimary,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
      )
      Text(
        text = "Stay updated on topics you care about. Find channels to follow below.",
        color = TextSecondary,
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 12.dp)
      )
      Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
          containerColor = DarkSurfaceVariant,
          contentColor = VibrantGreen
        ),
        shape = RoundedCornerShape(20.dp)
      ) {
        Text("Explore Channels", fontWeight = FontWeight.SemiBold)
      }
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
fun CommunitiesScreenContent(
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(AmoledBackground)
      .padding(horizontal = 16.dp)
      .testTag("communities_tab_content")
  ) {
    item {
      Spacer(modifier = Modifier.height(12.dp))

      // New Community Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { }
          .padding(vertical = 12.dp)
          .testTag("new_community_action"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.GroupAdd,
            contentDescription = "New community",
            tint = VibrantGreen,
            modifier = Modifier.size(28.dp)
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
          Text(
            text = "New community",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Bring members together in topic-based groups",
            color = TextSecondary,
            fontSize = 13.sp
          )
        }
      }

      HorizontalDivider(thickness = 0.5.dp, color = BorderDivider, modifier = Modifier.padding(vertical = 12.dp))

      Text(
        text = "Your Communities",
        color = TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 12.dp)
      )
    }

    val communityList = listOf(
      "FAMILY Circle & Community" to "14 groups • Announcements, Family Chat",
      "Tech Enthusiasts Network" to "8 groups • Kotlin, Android, Design",
      "Dhaka Creators Hub" to "5 groups • Photography, Media"
    )

    items(communityList.size) { index ->
      val (name, desc) = communityList[index]
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { }
          .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Groups,
            contentDescription = null,
            tint = VibrantGreen
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(
            text = name,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = desc,
            color = TextSecondary,
            fontSize = 13.sp
          )
        }
      }
    }
  }
}

@Composable
fun CallsScreenContent(
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(AmoledBackground)
      .padding(horizontal = 16.dp)
      .testTag("calls_tab_content")
  ) {
    item {
      Spacer(modifier = Modifier.height(12.dp))

      // Create call link
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { }
          .padding(vertical = 10.dp)
          .testTag("create_call_link"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(VibrantGreen),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Link,
            contentDescription = "Create call link",
            tint = Color(0xFF0B1014),
            modifier = Modifier.size(26.dp)
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = "Create call link",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
          )
          Text(
            text = "Share a link for your FAMILY call",
            color = TextSecondary,
            fontSize = 13.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Recent",
        color = TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
      )
    }

    val callLogs = listOf(
      Triple("F Rahman", "Yesterday, 7:12 PM", true),
      Triple("Masud Vai", "9/4/26, 11:30 AM", false),
      Triple("Raju Ahmed", "8/29/26, 9:15 PM", true),
      Triple("+91 84211 24017", "8/26/26, 4:20 PM", false)
    )

    items(callLogs.size) { index ->
      val (name, time, isVideo) = callLogs[index]
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { }
          .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          AvatarView(
            size = 50.dp,
            initials = name
          )
          Spacer(modifier = Modifier.width(14.dp))
          Column {
            Text(
              text = name,
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.SemiBold
            )
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(top = 2.dp)
            ) {
              Icon(
                imageVector = if (index % 2 == 0) Icons.Default.CallReceived else Icons.Default.CallMade,
                contentDescription = null,
                tint = if (index == 0) Color(0xFFFF5252) else AccentGreen,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = time,
                color = TextSecondary,
                fontSize = 13.sp
              )
            }
          }
        }

        IconButton(onClick = { }) {
          Icon(
            imageVector = if (isVideo) Icons.Default.Videocam else Icons.Default.Call,
            contentDescription = "Call",
            tint = VibrantGreen
          )
        }
      }
    }
  }
}
