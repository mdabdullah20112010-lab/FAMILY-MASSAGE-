package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.ContactEntity
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SearchBarBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantGreen

data class QuickSamplePhoto(
  val id: String,
  val title: String,
  val drawableRes: Int
)

fun getSampleFamilyPhotos(): List<QuickSamplePhoto> {
  return listOf(
    QuickSamplePhoto("family_gathering", "Family Gathering 🌳", R.drawable.family_gathering),
    QuickSamplePhoto("family_meal", "Family Dinner 🍲", R.drawable.family_meal),
    QuickSamplePhoto("avatar_city", "Weekend Tour 🏙️", R.drawable.avatar_city),
    QuickSamplePhoto("avatar_gamer", "Gaming Room 🎮", R.drawable.avatar_gamer),
    QuickSamplePhoto("avatar_person1", "Portrait 📸", R.drawable.avatar_person1)
  )
}

/**
 * Image view inside message bubble supporting local drawables and URIs
 */
@Composable
fun ChatImageView(
  imageUri: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val resId = remember(imageUri) {
    when (imageUri) {
      "family_gathering" -> R.drawable.family_gathering
      "family_meal" -> R.drawable.family_meal
      "avatar_city" -> R.drawable.avatar_city
      "avatar_gamer" -> R.drawable.avatar_gamer
      "avatar_person1" -> R.drawable.avatar_person1
      else -> {
        try {
          context.resources.getIdentifier(imageUri, "drawable", context.packageName).takeIf { it != 0 }
        } catch (_: Exception) {
          null
        }
      }
    }
  }

  Box(
    modifier = modifier
      .widthIn(min = 180.dp, max = 280.dp)
      .height(200.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(DarkSurfaceVariant)
      .clickable { onClick() }
      .testTag("chat_image_view"),
    contentAlignment = Alignment.Center
  ) {
    if (resId != null) {
      Image(
        painter = painterResource(id = resId),
        contentDescription = "Shared photo",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    } else {
      AsyncImage(
        model = ImageRequest.Builder(context)
          .data(imageUri)
          .crossfade(true)
          .build(),
        contentDescription = "Shared photo",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    }
  }
}

/**
 * Full screen image preview overlay
 */
@Composable
fun FullScreenImageViewer(
  imageUri: String,
  caption: String,
  senderTitle: String,
  formattedTime: String,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val resId = remember(imageUri) {
    when (imageUri) {
      "family_gathering" -> R.drawable.family_gathering
      "family_meal" -> R.drawable.family_meal
      "avatar_city" -> R.drawable.avatar_city
      "avatar_gamer" -> R.drawable.avatar_gamer
      "avatar_person1" -> R.drawable.avatar_person1
      else -> {
        try {
          context.resources.getIdentifier(imageUri, "drawable", context.packageName).takeIf { it != 0 }
        } catch (_: Exception) {
          null
        }
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFA000000))
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      // Top Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Brush.verticalGradient(listOf(Color(0xCC000000), Color.Transparent)))
          .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_full_image")) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = senderTitle,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = formattedTime,
            color = Color.LightGray,
            fontSize = 12.sp
          )
        }
      }

      // Center Image
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
      ) {
        if (resId != null) {
          Image(
            painter = painterResource(id = resId),
            contentDescription = "Full preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
          )
        } else {
          AsyncImage(
            model = ImageRequest.Builder(context).data(imageUri).build(),
            contentDescription = "Full preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
          )
        }
      }

      // Bottom Caption
      if (caption.isNotBlank()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xE6000000))))
            .padding(16.dp)
        ) {
          Text(
            text = caption,
            color = Color.White,
            fontSize = 15.sp,
            lineHeight = 20.sp
          )
        }
      }
    }
  }
}

/**
 * Material 3 Contact Card for phone number sharing in messages
 */
@Composable
fun ContactMessageCard(
  contactName: String,
  phoneNumber: String,
  note: String,
  isOutgoing: Boolean,
  onCopyPhone: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Column(
    modifier = modifier
      .widthIn(min = 220.dp, max = 280.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(if (isOutgoing) Color(0xFF0F362D) else DarkSurfaceVariant)
      .border(0.5.dp, BorderDivider, RoundedCornerShape(12.dp))
      .padding(12.dp)
      .testTag("contact_message_card")
  ) {
    if (note.isNotBlank()) {
      Text(
        text = note,
        color = TextPrimary,
        fontSize = 14.sp,
        modifier = Modifier.padding(bottom = 8.dp)
      )
    }

    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(VibrantGreen),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = "Contact",
          tint = Color(0xFF0B1014),
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = contactName,
          color = TextPrimary,
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = phoneNumber,
          color = VibrantGreen,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          fontFamily = FontFamily.Monospace
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))
    HorizontalDivider(thickness = 0.5.dp, color = BorderDivider)
    Spacer(modifier = Modifier.height(8.dp))

    // Action buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Button(
        onClick = {
          val cleanNumber = phoneNumber.replace(" ", "").replace("-", "")
          try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
              data = Uri.parse("tel:$cleanNumber")
            }
            context.startActivity(intent)
          } catch (_: Exception) {
            Toast.makeText(context, "Dial: $phoneNumber", Toast.LENGTH_SHORT).show()
          }
        },
        modifier = Modifier
          .weight(1f)
          .height(36.dp)
          .testTag("btn_call_contact"),
        colors = ButtonDefaults.buttonColors(
          containerColor = VibrantGreen,
          contentColor = Color(0xFF0B1014)
        ),
        contentPadding = PaddingValues(horizontal = 8.dp),
        shape = RoundedCornerShape(18.dp)
      ) {
        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Call", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }

      OutlinedButton(
        onClick = {
          val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
          val clip = ClipData.newPlainText("Phone Number", phoneNumber)
          clipboard.setPrimaryClip(clip)
          onCopyPhone(phoneNumber)
        },
        modifier = Modifier
          .weight(1f)
          .height(36.dp)
          .testTag("btn_copy_phone"),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = TextPrimary
        ),
        contentPadding = PaddingValues(horizontal = 8.dp),
        shape = RoundedCornerShape(18.dp)
      ) {
        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp), tint = TextSecondary)
        Spacer(modifier = Modifier.width(4.dp))
        Text("Copy", fontSize = 12.sp, color = TextPrimary)
      }
    }
  }
}

/**
 * Bottom sheet with rich options: Pick photo, send sample family pictures, or share contact number
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentSheet(
  onDismiss: () -> Unit,
  onLaunchPhotoPicker: () -> Unit,
  onSelectSamplePhoto: (String, String) -> Unit,
  onOpenAddContactNumber: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val samplePhotos = remember { getSampleFamilyPhotos() }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DarkSurface,
    contentColor = TextPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      Text(
        text = "Share to Chat",
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
      )

      // Top action buttons row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        // Gallery Button
        AttachmentOptionItem(
          icon = Icons.Default.Collections,
          title = "Gallery Pic",
          subtitle = "Device Photos",
          badgeColor = Color(0xFF9C27B0),
          onClick = {
            onDismiss()
            onLaunchPhotoPicker()
          },
          testTag = "opt_attach_gallery"
        )

        // Contact Number Button
        AttachmentOptionItem(
          icon = Icons.Default.ContactPhone,
          title = "Add Number",
          subtitle = "Share Phone",
          badgeColor = Color(0xFF00897B),
          onClick = {
            onDismiss()
            onOpenAddContactNumber()
          },
          testTag = "opt_attach_contact_number"
        )
      }

      Spacer(modifier = Modifier.height(20.dp))
      HorizontalDivider(thickness = 0.5.dp, color = BorderDivider)
      Spacer(modifier = Modifier.height(14.dp))

      // Family Quick Photos Section
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
      ) {
        Icon(Icons.Default.Image, contentDescription = null, tint = VibrantGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Family Photos (Instant Share)",
          color = TextSecondary,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        samplePhotos.forEach { photo ->
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clickable {
                onDismiss()
                onSelectSamplePhoto(photo.id, photo.title)
              }
              .testTag("sample_photo_${photo.id}")
          ) {
            Image(
              painter = painterResource(id = photo.drawableRes),
              contentDescription = photo.title,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BorderDivider, RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = photo.title.split(" ").firstOrNull() ?: photo.title,
              color = TextPrimary,
              fontSize = 11.sp,
              maxLines = 1
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

@Composable
private fun AttachmentOptionItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  badgeColor: Color,
  onClick: () -> Unit,
  testTag: String
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable { onClick() }
      .padding(8.dp)
      .testTag(testTag)
  ) {
    Box(
      modifier = Modifier
        .size(56.dp)
        .clip(CircleShape)
        .background(badgeColor.copy(alpha = 0.2f))
        .border(1.5.dp, badgeColor, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = badgeColor,
        modifier = Modifier.size(28.dp)
      )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      text = title,
      color = TextPrimary,
      fontSize = 13.sp,
      fontWeight = FontWeight.SemiBold
    )
    Text(
      text = subtitle,
      color = TextSecondary,
      fontSize = 11.sp
    )
  }
}

/**
 * Dialog / Sheet for adding/sharing contact numbers in messages
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactNumberDialog(
  contacts: List<ContactEntity>,
  onDismiss: () -> Unit,
  onSendContactNumber: (name: String, phone: String, note: String) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Pick saved contact, 1: Custom number

  var customName by remember { mutableStateOf("") }
  var customPhone by remember { mutableStateOf("") }
  var customNote by remember { mutableStateOf("") }
  var selectedSavedContact by remember { mutableStateOf<ContactEntity?>(null) }
  var savedContactNote by remember { mutableStateOf("") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = DarkSurface,
    contentColor = TextPrimary
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Share Contact Number",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = DarkSurfaceVariant,
        contentColor = TextPrimary,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = VibrantGreen
          )
        },
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Saved Contacts", fontWeight = FontWeight.SemiBold) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("New Number", fontWeight = FontWeight.SemiBold) }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (selectedTab == 0) {
        // Saved contacts list
        Text(
          text = "Select a contact number to send:",
          color = TextSecondary,
          fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .padding(4.dp)
        ) {
          LazyColumn(
            modifier = Modifier.fillMaxSize()
          ) {
            items(contacts.filter { !it.isSelf }, key = { it.id }) { c ->
              val isSelected = selectedSavedContact?.id == c.id
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) VibrantGreen.copy(alpha = 0.2f) else Color.Transparent)
                  .clickable { selectedSavedContact = c }
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(c.avatarColorHex)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = c.name.take(2).uppercase(),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(text = c.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                  Text(text = c.phone, color = VibrantGreen, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                }

                if (isSelected) {
                  Icon(Icons.Default.Call, contentDescription = null, tint = VibrantGreen, modifier = Modifier.size(18.dp))
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = savedContactNote,
          onValueChange = { savedContactNote = it },
          placeholder = { Text("Add optional note (e.g. Call this number)", color = TextMuted) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VibrantGreen,
            unfocusedBorderColor = BorderDivider,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_saved_contact_note"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            selectedSavedContact?.let {
              onSendContactNumber(it.name, it.phone, savedContactNote)
              onDismiss()
            }
          },
          enabled = selectedSavedContact != null,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_confirm_send_saved_contact"),
          colors = ButtonDefaults.buttonColors(
            containerColor = VibrantGreen,
            contentColor = Color(0xFF0B1014)
          ),
          shape = RoundedCornerShape(24.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Send Contact Card", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

      } else {
        // Custom Number inputs
        OutlinedTextField(
          value = customName,
          onValueChange = { customName = it },
          label = { Text("Contact Name") },
          placeholder = { Text("e.g. Uncle Rahim, Dr. Kabir", color = TextMuted) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VibrantGreen,
            unfocusedBorderColor = BorderDivider,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_custom_contact_name"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = customPhone,
          onValueChange = { customPhone = it },
          label = { Text("Phone Number") },
          placeholder = { Text("e.g. +880 1712-345678", color = TextMuted) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VibrantGreen,
            unfocusedBorderColor = BorderDivider,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_custom_contact_phone"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = customNote,
          onValueChange = { customNote = it },
          label = { Text("Optional Note") },
          placeholder = { Text("e.g. Here is the doctor's phone number", color = TextMuted) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VibrantGreen,
            unfocusedBorderColor = BorderDivider,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_custom_contact_note"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            if (customPhone.isNotBlank()) {
              val name = customName.ifBlank { "Contact Number" }
              onSendContactNumber(name, customPhone, customNote)
              onDismiss()
            }
          },
          enabled = customPhone.isNotBlank(),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("btn_confirm_send_custom_contact"),
          colors = ButtonDefaults.buttonColors(
            containerColor = VibrantGreen,
            contentColor = Color(0xFF0B1014)
          ),
          shape = RoundedCornerShape(24.dp)
        ) {
          Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Send Contact Number", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
