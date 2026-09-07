package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccountEntity
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.DarkPopupSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSwitcherSheet(
  accounts: List<UserAccountEntity>,
  onDismiss: () -> Unit,
  onAccountSelected: (String) -> Unit,
  onAddAccount: () -> Unit,
  onSignOut: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sheetState = rememberModalBottomSheetState()

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DarkPopupSurface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    modifier = modifier.testTag("account_switcher_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .navigationBarsPadding()
    ) {
      Text(
        text = "Switch account",
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 14.dp)
      )

      if (accounts.isEmpty()) {
        // Fallback default row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          AvatarView(
            size = 46.dp,
            drawableName = "avatar_gamer",
            initials = "BK"
          )
          Spacer(modifier = Modifier.width(14.dp))
          Column {
            Text(
              text = "boekfjmdhn",
              color = TextPrimary,
              fontSize = 16.sp,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "@boylysmfjjfj • Active",
              color = TextSecondary,
              fontSize = 13.sp
            )
          }
        }
      } else {
        accounts.forEach { account ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onAccountSelected(account.id)
                onDismiss()
              }
              .padding(vertical = 10.dp)
              .testTag("account_item_${account.id}"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              AvatarView(
                size = 46.dp,
                drawableName = account.avatarDrawableName,
                initials = account.fullName,
                backgroundColorHex = account.avatarColorHex
              )
              Spacer(modifier = Modifier.width(14.dp))
              Column {
                Text(
                  text = account.fullName,
                  color = TextPrimary,
                  fontSize = 16.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "${account.username}${if (account.isActive) " • Active" else ""}",
                  color = if (account.isActive) VibrantGreen else TextSecondary,
                  fontSize = 13.sp
                )
              }
            }

            if (account.isActive) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(VibrantGreen),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Active account",
                  tint = Color(0xFF0B1014),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }

      HorizontalDivider(
        thickness = 0.5.dp,
        color = BorderDivider,
        modifier = Modifier.padding(vertical = 8.dp)
      )

      // Add account / Sign Up option
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            onAddAccount()
            onDismiss()
          }
          .padding(vertical = 12.dp)
          .testTag("btn_add_account"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(DarkSurfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add account",
            tint = VibrantGreen,
            modifier = Modifier.size(24.dp)
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(
            text = "Add account",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Sign in or create a new FAMILY account",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }
      }

      // Sign out option
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            onSignOut()
            onDismiss()
          }
          .padding(vertical = 12.dp)
          .testTag("btn_sheet_sign_out"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(DarkSurfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = "Sign out",
            tint = Color(0xFFFF8A80),
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(
            text = "Sign out",
            color = Color(0xFFFF8A80),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Log out from the current account",
            color = TextSecondary,
            fontSize = 12.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
