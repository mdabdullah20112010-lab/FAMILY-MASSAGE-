package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatEntity
import com.example.data.ContactEntity
import com.example.data.MessageEntity
import com.example.ui.components.AddContactNumberDialog
import com.example.ui.components.AttachmentSheet
import com.example.ui.components.AvatarView
import com.example.ui.components.ChatImageView
import com.example.ui.components.ContactMessageCard
import com.example.ui.components.FullScreenImageViewer
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.BubbleIncoming
import com.example.ui.theme.BubbleOutgoing
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ReadCheckmarkBlue
import com.example.ui.theme.SearchBarBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantGreen
import kotlinx.coroutines.launch

@Composable
fun ChatDetailScreen(
  chat: ChatEntity?,
  messages: List<MessageEntity>,
  contacts: List<ContactEntity> = emptyList(),
  onNavigateBack: () -> Unit,
  onSendMessage: (String) -> Unit,
  onSendImageMessage: (imageUri: String, caption: String) -> Unit = { _, _ -> },
  onSendContactNumber: (name: String, phone: String, note: String) -> Unit = { _, _, _ -> },
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  var inputText by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  // Modals & Viewer states
  var showAttachmentSheet by remember { mutableStateOf(false) }
  var showAddContactDialog by remember { mutableStateOf(false) }
  var activePreviewImageUri by remember { mutableStateOf<String?>(null) }
  var activePreviewCaption by remember { mutableStateOf("") }
  var activePreviewFormattedTime by remember { mutableStateOf("") }

  // Android Photo Picker Launcher (Google Play Policy Compliant)
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      onSendImageMessage(uri.toString(), inputText)
      inputText = ""
      scope.launch {
        snackbarHostState.showSnackbar("Picture sent 📸")
      }
    }
  }

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

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
        .imePadding()
    ) {
      // Top Header Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSurface)
          .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier.testTag("btn_chat_back")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimary
          )
        }

        AvatarView(
          size = 40.dp,
          drawableName = chat?.avatarDrawableName,
          initials = chat?.title ?: "Chat",
          backgroundColorHex = chat?.avatarColorHex ?: 0xFF2A3942
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
          modifier = Modifier
            .weight(1f)
            .clickable {
              scope.launch { snackbarHostState.showSnackbar("Contact info: ${chat?.title}") }
            }
        ) {
          Text(
            text = chat?.title ?: "Chat",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
          )
          Text(
            text = if (chat?.isOnline == true) "online" else "last seen recently",
            color = if (chat?.isOnline == true) VibrantGreen else TextSecondary,
            fontSize = 12.sp
          )
        }

        IconButton(onClick = { scope.launch { snackbarHostState.showSnackbar("Video call") } }) {
          Icon(Icons.Default.Videocam, contentDescription = "Video call", tint = TextPrimary)
        }
        IconButton(onClick = {
          val callNumber = chat?.title?.filter { it.isDigit() || it == '+' }
          if (!callNumber.isNullOrBlank()) {
            try {
              context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$callNumber")))
            } catch (_: Exception) {
              scope.launch { snackbarHostState.showSnackbar("Calling ${chat.title}") }
            }
          } else {
            scope.launch { snackbarHostState.showSnackbar("Voice call: ${chat?.title}") }
          }
        }) {
          Icon(Icons.Default.Call, contentDescription = "Call", tint = TextPrimary)
        }
        IconButton(onClick = { scope.launch { snackbarHostState.showSnackbar("More options") } }) {
          Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextPrimary)
        }
      }

      // Conversation Messages Area
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(
                Color(0xFF090D10),
                AmoledBackground
              )
            )
          )
      ) {
        LazyColumn(
          state = listState,
          modifier = Modifier
            .fillMaxSize()
            .testTag("messages_list"),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
              contentAlignment = Alignment.Center
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(DarkSurfaceVariant.copy(alpha = 0.8f))
                  .padding(horizontal = 12.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "🔒 Messages & calls are end-to-end encrypted",
                  color = TextMuted,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }

          items(messages, key = { it.id }) { msg ->
            MessageBubbleRow(
              message = msg,
              onImageClick = {
                activePreviewImageUri = msg.imageUri
                activePreviewCaption = msg.text
                activePreviewFormattedTime = msg.formattedTime
              },
              onCopyPhone = { copiedNumber ->
                scope.launch { snackbarHostState.showSnackbar("Number copied: $copiedNumber") }
              }
            )
          }
        }
      }

      // Bottom Message Input Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
          .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Rounded Pill Input Box
        Row(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(26.dp))
            .background(SearchBarBackground)
            .padding(horizontal = 6.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = { scope.launch { snackbarHostState.showSnackbar("Emoji picker") } }) {
            Icon(
              imageVector = Icons.Default.Mood,
              contentDescription = "Emojis",
              tint = TextSecondary,
              modifier = Modifier.size(24.dp)
            )
          }

          TextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = { Text("Message", color = TextSecondary, fontSize = 15.sp) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = Color.Transparent,
              unfocusedContainerColor = Color.Transparent,
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent,
              focusedTextColor = TextPrimary,
              unfocusedTextColor = TextPrimary,
              cursorColor = VibrantGreen
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("chat_input_field"),
            maxLines = 4
          )

          // Add Phone Number Quick Shortcut
          IconButton(
            onClick = { showAddContactDialog = true },
            modifier = Modifier.testTag("btn_quick_add_number")
          ) {
            Icon(
              imageVector = Icons.Default.ContactPhone,
              contentDescription = "Share Phone Number",
              tint = VibrantGreen,
              modifier = Modifier.size(22.dp)
            )
          }

          // Attachment (Pic / Number options)
          IconButton(
            onClick = { showAttachmentSheet = true },
            modifier = Modifier.testTag("btn_attach_media")
          ) {
            Icon(
              imageVector = Icons.Default.AttachFile,
              contentDescription = "Attach",
              tint = TextSecondary,
              modifier = Modifier.size(22.dp)
            )
          }

          // Camera (Launch Photo Picker directly)
          IconButton(
            onClick = {
              photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            },
            modifier = Modifier.testTag("btn_camera_pic")
          ) {
            Icon(
              imageVector = Icons.Default.PhotoCamera,
              contentDescription = "Send Photo",
              tint = TextSecondary,
              modifier = Modifier.size(22.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Circular Green Send / Mic Action Button
        Box(
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(VibrantGreen)
            .clickable {
              if (inputText.isNotBlank()) {
                onSendMessage(inputText)
                inputText = ""
              } else {
                scope.launch { snackbarHostState.showSnackbar("Hold to record voice message") }
              }
            }
            .testTag("btn_send_message"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (inputText.isNotBlank()) Icons.AutoMirrored.Filled.Send else Icons.Default.Mic,
            contentDescription = "Send",
            tint = Color(0xFF0B1014),
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }
  }

  // Attachment Sheet (Photos & Contact Numbers)
  if (showAttachmentSheet) {
    AttachmentSheet(
      onDismiss = { showAttachmentSheet = false },
      onLaunchPhotoPicker = {
        photoPickerLauncher.launch(
          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
      },
      onSelectSamplePhoto = { photoId, title ->
        onSendImageMessage(photoId, inputText)
        inputText = ""
        scope.launch { snackbarHostState.showSnackbar("Sent $title 📷") }
      },
      onOpenAddContactNumber = {
        showAddContactDialog = true
      }
    )
  }

  // Add Contact Number Dialog
  if (showAddContactDialog) {
    AddContactNumberDialog(
      contacts = contacts,
      onDismiss = { showAddContactDialog = false },
      onSendContactNumber = { name, phone, note ->
        onSendContactNumber(name, phone, note)
        scope.launch { snackbarHostState.showSnackbar("Contact number sent: $phone") }
      }
    )
  }

  // Full Screen Image Preview
  if (activePreviewImageUri != null) {
    FullScreenImageViewer(
      imageUri = activePreviewImageUri!!,
      caption = activePreviewCaption,
      senderTitle = chat?.title ?: "Photo",
      formattedTime = activePreviewFormattedTime,
      onDismiss = { activePreviewImageUri = null }
    )
  }
}

@Composable
fun MessageBubbleRow(
  message: MessageEntity,
  onImageClick: () -> Unit = {},
  onCopyPhone: (String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isOutgoing = message.isOutgoing
  val hasImage = message.messageType == "IMAGE" || !message.imageUri.isNullOrBlank()
  val hasContact = message.messageType == "CONTACT" || !message.sharedContactNumber.isNullOrBlank()

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = if (isOutgoing) Arrangement.End else Arrangement.Start
  ) {
    Box(
      modifier = Modifier
        .widthIn(max = if (hasImage || hasContact) 300.dp else 290.dp)
        .clip(
          RoundedCornerShape(
            topStart = 14.dp,
            topEnd = 14.dp,
            bottomStart = if (isOutgoing) 14.dp else 2.dp,
            bottomEnd = if (isOutgoing) 2.dp else 14.dp
          )
        )
        .background(if (isOutgoing) BubbleOutgoing else BubbleIncoming)
        .padding(
          horizontal = if (hasImage) 6.dp else 12.dp,
          vertical = if (hasImage) 6.dp else 8.dp
        )
    ) {
      Column {
        // Render Image if present (Pic deowa neowa)
        if (hasImage && message.imageUri != null) {
          ChatImageView(
            imageUri = message.imageUri,
            onClick = onImageClick,
            modifier = Modifier.padding(bottom = 4.dp)
          )
        }

        // Render Contact Card if present (Massage a number add)
        if (hasContact && message.sharedContactNumber != null) {
          ContactMessageCard(
            contactName = message.sharedContactName ?: "Contact",
            phoneNumber = message.sharedContactNumber,
            note = message.text,
            isOutgoing = isOutgoing,
            onCopyPhone = onCopyPhone,
            modifier = Modifier.padding(bottom = 4.dp)
          )
        } else if (message.text.isNotBlank()) {
          // Render text with phone number detection
          FormattedMessageText(
            text = message.text,
            onPhoneClick = { phone ->
              val cleanPhone = phone.replace(" ", "")
              try {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone")))
              } catch (_: Exception) {
                Toast.makeText(context, "Dial: $phone", Toast.LENGTH_SHORT).show()
              }
            }
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Time and Receipts
        Row(
          modifier = Modifier
            .align(Alignment.End)
            .padding(end = 4.dp, bottom = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = message.formattedTime,
            color = if (isOutgoing) Color(0xFF90C2B8) else TextSecondary,
            fontSize = 11.sp
          )

          if (isOutgoing) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.DoneAll,
              contentDescription = "Read receipt",
              tint = if (message.status == "READ") ReadCheckmarkBlue else Color(0xFF90C2B8),
              modifier = Modifier.size(15.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * Text component that detects phone numbers and makes them clickable
 */
@Composable
private fun FormattedMessageText(
  text: String,
  onPhoneClick: (String) -> Unit
) {
  val phoneRegex = Regex("(\\+?\\d[\\d\\s\\-()]{6,}\\d)")
  val matches = phoneRegex.findAll(text).toList()

  if (matches.isEmpty()) {
    Text(
      text = text,
      color = TextPrimary,
      fontSize = 15.sp,
      lineHeight = 20.sp
    )
  } else {
    val annotatedString = buildAnnotatedString {
      var currentIndex = 0
      for (match in matches) {
        if (match.range.first > currentIndex) {
          append(text.substring(currentIndex, match.range.first))
        }
        pushStringAnnotation(tag = "PHONE", annotation = match.value)
        withStyle(
          style = SpanStyle(
            color = VibrantGreen,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline
          )
        ) {
          append(match.value)
        }
        pop()
        currentIndex = match.range.last + 1
      }
      if (currentIndex < text.length) {
        append(text.substring(currentIndex))
      }
    }

    ClickableText(
      text = annotatedString,
      style = androidx.compose.ui.text.TextStyle(
        color = TextPrimary,
        fontSize = 15.sp,
        lineHeight = 20.sp
      ),
      onClick = { offset ->
        annotatedString.getStringAnnotations(tag = "PHONE", start = offset, end = offset)
          .firstOrNull()?.let { annotation ->
            onPhoneClick(annotation.item)
          }
      }
    )
  }
}
