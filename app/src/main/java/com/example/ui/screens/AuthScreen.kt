package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccountEntity
import com.example.ui.components.AvatarView
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AmoledBackground
import com.example.ui.theme.BorderDivider
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SearchBarBackground
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantGreen
import kotlinx.coroutines.launch

enum class AuthTab {
  SIGN_IN,
  SIGN_UP
}

@Composable
fun AuthScreen(
  currentAccounts: List<UserAccountEntity>,
  initialTab: AuthTab = AuthTab.SIGN_IN,
  onSignIn: (identifier: String, password: String, onResult: (String?) -> Unit) -> Unit,
  onSignUp: (
    fullName: String,
    username: String,
    phone: String,
    email: String,
    password: String,
    status: String,
    avatarDrawable: String?,
    avatarColorHex: Long,
    onResult: (String?) -> Unit
  ) -> Unit,
  onSwitchToAccount: (String) -> Unit,
  onCancelOrBack: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(initialTab) }
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val focusManager = LocalFocusManager.current

  // Sign In form state
  var signInIdentifier by remember { mutableStateOf("") }
  var signInPassword by remember { mutableStateOf("") }
  var isSignInPasswordVisible by remember { mutableStateOf(false) }
  var isSignInLoading by remember { mutableStateOf(false) }
  var signInError by remember { mutableStateOf<String?>(null) }

  // Sign Up form state
  var signUpName by remember { mutableStateOf("") }
  var signUpUsername by remember { mutableStateOf("") }
  var signUpPhone by remember { mutableStateOf("") }
  var signUpEmail by remember { mutableStateOf("") }
  var signUpPassword by remember { mutableStateOf("") }
  var signUpConfirmPassword by remember { mutableStateOf("") }
  var signUpStatus by remember { mutableStateOf("Hey there! I am using FAMILY.") }
  var selectedAvatarDrawable by remember { mutableStateOf<String?>("avatar_gamer") }
  var selectedAvatarColor by remember { mutableStateOf(0xFFFF7043) }
  var isSignUpPasswordVisible by remember { mutableStateOf(false) }
  var isSignUpLoading by remember { mutableStateOf(false) }
  var signUpError by remember { mutableStateOf<String?>(null) }

  val avatarOptions = listOf(
    Pair("avatar_gamer", 0xFFFF7043),
    Pair("avatar_city", 0xFF2E7D32),
    Pair("avatar_person1", 0xFF1565C0),
    Pair(null, 0xFF795548),
    Pair(null, 0xFF00897B)
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
        .imePadding()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top header with Back option if available
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (onCancelOrBack != null) {
          IconButton(
            onClick = onCancelOrBack,
            modifier = Modifier.testTag("btn_auth_back")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = TextPrimary
            )
          }
        } else {
          Spacer(modifier = Modifier.width(48.dp))
        }

        Text(
          text = "FAMILY Account",
          color = TextSecondary,
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.width(48.dp))
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Logo & Brand Header
      Box(
        modifier = Modifier
          .size(68.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              listOf(
                Color(0xFF00E676),
                Color(0xFF00A884)
              )
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Chat,
          contentDescription = "FAMILY Logo",
          tint = Color(0xFF0B1014),
          modifier = Modifier.size(36.dp)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Welcome to FAMILY",
        color = TextPrimary,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.4.sp
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Simple. Secure. Reliable messaging.",
        color = TextSecondary,
        fontSize = 14.sp
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Segmented Tabs: Sign In / Sign Up
      Row(
        modifier = Modifier
          .padding(horizontal = 24.dp)
          .fillMaxWidth()
          .height(48.dp)
          .clip(RoundedCornerShape(24.dp))
          .background(DarkSurfaceVariant)
          .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(if (selectedTab == AuthTab.SIGN_IN) VibrantGreen else Color.Transparent)
            .clickable {
              selectedTab = AuthTab.SIGN_IN
              signInError = null
            }
            .testTag("auth_tab_signin"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Sign In",
            color = if (selectedTab == AuthTab.SIGN_IN) Color(0xFF0B1014) else TextSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(if (selectedTab == AuthTab.SIGN_UP) VibrantGreen else Color.Transparent)
            .clickable {
              selectedTab = AuthTab.SIGN_UP
              signUpError = null
            }
            .testTag("auth_tab_signup"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "Sign Up",
            color = if (selectedTab == AuthTab.SIGN_UP) Color(0xFF0B1014) else TextSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      AnimatedContent(
        targetState = selectedTab,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "AuthTabTransition",
        modifier = Modifier.fillMaxWidth()
      ) { tab ->
        when (tab) {
          AuthTab.SIGN_IN -> {
            // SIGN IN FORM
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              if (signInError != null) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFB00020).copy(alpha = 0.2f))
                    .border(1.dp, Color(0xFFCF6679), RoundedCornerShape(12.dp))
                    .padding(12.dp)
                ) {
                  Text(
                    text = signInError ?: "",
                    color = Color(0xFFFF8A80),
                    fontSize = 13.sp
                  )
                }
                Spacer(modifier = Modifier.height(14.dp))
              }

              OutlinedTextField(
                value = signInIdentifier,
                onValueChange = {
                  signInIdentifier = it
                  signInError = null
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_input_identifier"),
                label = { Text("Username or Phone", color = TextSecondary) },
                placeholder = { Text("@boylysmfjjfj or +1 (555) 019-2834", color = TextMuted) },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = VibrantGreen
                  )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Text,
                  imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                  onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = VibrantGreen,
                  unfocusedBorderColor = BorderDivider,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedContainerColor = SearchBarBackground,
                  unfocusedContainerColor = SearchBarBackground,
                  cursorColor = VibrantGreen
                ),
                shape = RoundedCornerShape(16.dp)
              )

              Spacer(modifier = Modifier.height(14.dp))

              OutlinedTextField(
                value = signInPassword,
                onValueChange = {
                  signInPassword = it
                  signInError = null
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_input_password"),
                label = { Text("Password", color = TextSecondary) },
                placeholder = { Text("Enter your password", color = TextMuted) },
                leadingIcon = {
                  Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = VibrantGreen
                  )
                },
                trailingIcon = {
                  IconButton(onClick = { isSignInPasswordVisible = !isSignInPasswordVisible }) {
                    Icon(
                      imageVector = if (isSignInPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                      contentDescription = if (isSignInPasswordVisible) "Hide password" else "Show password",
                      tint = TextSecondary
                    )
                  }
                },
                visualTransformation = if (isSignInPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Password,
                  imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                  onDone = { focusManager.clearFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = VibrantGreen,
                  unfocusedBorderColor = BorderDivider,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedContainerColor = SearchBarBackground,
                  unfocusedContainerColor = SearchBarBackground,
                  cursorColor = VibrantGreen
                ),
                shape = RoundedCornerShape(16.dp)
              )

              Spacer(modifier = Modifier.height(20.dp))

              Button(
                onClick = {
                  if (signInIdentifier.isBlank()) {
                    signInError = "Please enter your username or phone number."
                    return@Button
                  }
                  isSignInLoading = true
                  onSignIn(signInIdentifier.trim(), signInPassword) { error ->
                    isSignInLoading = false
                    signInError = error
                  }
                },
                enabled = !isSignInLoading,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("auth_btn_signin"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = VibrantGreen,
                  contentColor = Color(0xFF0B1014)
                ),
                shape = RoundedCornerShape(25.dp)
              ) {
                if (isSignInLoading) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color(0xFF0B1014),
                    strokeWidth = 2.5.dp
                  )
                } else {
                  Text(
                    text = "Sign In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Quick Test / Demo autofill helper
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(DarkSurfaceVariant)
                  .clickable {
                    signInIdentifier = "@boylysmfjjfj"
                    signInPassword = "password123"
                    signInError = null
                    scope.launch {
                      snackbarHostState.showSnackbar("Autofilled demo account @boylysmfjjfj")
                    }
                  }
                  .padding(12.dp)
                  .testTag("auth_btn_demo_signin"),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AccentGreen.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = VibrantGreen,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Quick Demo Auto-fill",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = "Tap to populate @boylysmfjjfj credentials",
                    color = TextSecondary,
                    fontSize = 12.sp
                  )
                }
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                  contentDescription = null,
                  tint = TextSecondary,
                  modifier = Modifier.size(18.dp)
                )
              }

              Spacer(modifier = Modifier.height(16.dp))

              Row(
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Don't have an account?",
                  color = TextSecondary,
                  fontSize = 14.sp
                )
                TextButton(
                  onClick = {
                    selectedTab = AuthTab.SIGN_UP
                    signUpError = null
                  }
                ) {
                  Text(
                    text = "Sign Up",
                    color = VibrantGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }

          AuthTab.SIGN_UP -> {
            // SIGN UP FORM
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              if (signUpError != null) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFB00020).copy(alpha = 0.2f))
                    .border(1.dp, Color(0xFFCF6679), RoundedCornerShape(12.dp))
                    .padding(12.dp)
                ) {
                  Text(
                    text = signUpError ?: "",
                    color = Color(0xFFFF8A80),
                    fontSize = 13.sp
                  )
                }
                Spacer(modifier = Modifier.height(14.dp))
              }

              // Avatar Selection Row
              Text(
                text = "Choose Profile Picture",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
              )
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
              ) {
                avatarOptions.forEach { (drawable, color) ->
                  val isSelected = selectedAvatarDrawable == drawable && selectedAvatarColor == color
                  Box(
                    modifier = Modifier
                      .size(52.dp)
                      .clip(CircleShape)
                      .border(
                        width = if (isSelected) 2.5.dp else 0.dp,
                        color = if (isSelected) VibrantGreen else Color.Transparent,
                        shape = CircleShape
                      )
                      .padding(3.dp)
                      .clickable {
                        selectedAvatarDrawable = drawable
                        selectedAvatarColor = color
                      },
                    contentAlignment = Alignment.Center
                  ) {
                    AvatarView(
                      size = 46.dp,
                      drawableName = drawable,
                      initials = signUpName.ifBlank { "Me" },
                      backgroundColorHex = color
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              // Full Name
              OutlinedTextField(
                value = signUpName,
                onValueChange = {
                  signUpName = it
                  signUpError = null
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_input_name"),
                label = { Text("Full Name *", color = TextSecondary) },
                placeholder = { Text("e.g. Abdullah Joy", color = TextMuted) },
                leadingIcon = {
                  Icon(Icons.Default.Person, contentDescription = null, tint = VibrantGreen)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Text,
                  imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = VibrantGreen,
                  unfocusedBorderColor = BorderDivider,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedContainerColor = SearchBarBackground,
                  unfocusedContainerColor = SearchBarBackground,
                  cursorColor = VibrantGreen
                ),
                shape = RoundedCornerShape(16.dp)
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Username
              OutlinedTextField(
                value = signUpUsername,
                onValueChange = {
                  signUpUsername = it
                  signUpError = null
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_input_username"),
                label = { Text("Username *", color = TextSecondary) },
                placeholder = { Text("@username", color = TextMuted) },
                leadingIcon = {
                  Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = VibrantGreen)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Text,
                  imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = VibrantGreen,
                  unfocusedBorderColor = BorderDivider,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedContainerColor = SearchBarBackground,
                  unfocusedContainerColor = SearchBarBackground,
                  cursorColor = VibrantGreen
                ),
                shape = RoundedCornerShape(16.dp)
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Phone Number
              OutlinedTextField(
                value = signUpPhone,
                onValueChange = {
                  signUpPhone = it
                  signUpError = null
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_input_phone"),
                label = { Text("Phone Number", color = TextSecondary) },
                placeholder = { Text("+1 (555) 000-0000", color = TextMuted) },
                leadingIcon = {
                  Icon(Icons.Default.Phone, contentDescription = null, tint = VibrantGreen)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Phone,
                  imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = VibrantGreen,
                  unfocusedBorderColor = BorderDivider,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedContainerColor = SearchBarBackground,
                  unfocusedContainerColor = SearchBarBackground,
                  cursorColor = VibrantGreen
                ),
                shape = RoundedCornerShape(16.dp)
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Email (Optional)
              OutlinedTextField(
                value = signUpEmail,
                onValueChange = {
                  signUpEmail = it
                  signUpError = null
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_input_email"),
                label = { Text("Email (Optional)", color = TextSecondary) },
                placeholder = { Text("user@example.com", color = TextMuted) },
                leadingIcon = {
                  Icon(Icons.Default.Email, contentDescription = null, tint = VibrantGreen)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Email,
                  imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = VibrantGreen,
                  unfocusedBorderColor = BorderDivider,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedContainerColor = SearchBarBackground,
                  unfocusedContainerColor = SearchBarBackground,
                  cursorColor = VibrantGreen
                ),
                shape = RoundedCornerShape(16.dp)
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Password
              OutlinedTextField(
                value = signUpPassword,
                onValueChange = {
                  signUpPassword = it
                  signUpError = null
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_input_signup_password"),
                label = { Text("Create Password *", color = TextSecondary) },
                placeholder = { Text("At least 6 characters", color = TextMuted) },
                leadingIcon = {
                  Icon(Icons.Default.Lock, contentDescription = null, tint = VibrantGreen)
                },
                trailingIcon = {
                  IconButton(onClick = { isSignUpPasswordVisible = !isSignUpPasswordVisible }) {
                    Icon(
                      imageVector = if (isSignUpPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                      contentDescription = null,
                      tint = TextSecondary
                    )
                  }
                },
                visualTransformation = if (isSignUpPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Password,
                  imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = VibrantGreen,
                  unfocusedBorderColor = BorderDivider,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedContainerColor = SearchBarBackground,
                  unfocusedContainerColor = SearchBarBackground,
                  cursorColor = VibrantGreen
                ),
                shape = RoundedCornerShape(16.dp)
              )

              Spacer(modifier = Modifier.height(12.dp))

              // Confirm Password
              OutlinedTextField(
                value = signUpConfirmPassword,
                onValueChange = {
                  signUpConfirmPassword = it
                  signUpError = null
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("auth_input_confirm_password"),
                label = { Text("Confirm Password *", color = TextSecondary) },
                leadingIcon = {
                  Icon(Icons.Default.Lock, contentDescription = null, tint = VibrantGreen)
                },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                  keyboardType = KeyboardType.Password,
                  imeAction = ImeAction.Done
                ),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = VibrantGreen,
                  unfocusedBorderColor = BorderDivider,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedContainerColor = SearchBarBackground,
                  unfocusedContainerColor = SearchBarBackground,
                  cursorColor = VibrantGreen
                ),
                shape = RoundedCornerShape(16.dp)
              )

              Spacer(modifier = Modifier.height(20.dp))

              Button(
                onClick = {
                  if (signUpName.isBlank()) {
                    signUpError = "Please enter your full name."
                    return@Button
                  }
                  if (signUpUsername.isBlank()) {
                    signUpError = "Please choose a username."
                    return@Button
                  }
                  if (signUpPassword.length < 4) {
                    signUpError = "Password must be at least 4 characters."
                    return@Button
                  }
                  if (signUpPassword != signUpConfirmPassword) {
                    signUpError = "Passwords do not match."
                    return@Button
                  }

                  isSignUpLoading = true
                  onSignUp(
                    signUpName.trim(),
                    signUpUsername.trim(),
                    signUpPhone.trim(),
                    signUpEmail.trim(),
                    signUpPassword,
                    signUpStatus.trim(),
                    selectedAvatarDrawable,
                    selectedAvatarColor
                  ) { error ->
                    isSignUpLoading = false
                    signUpError = error
                  }
                },
                enabled = !isSignUpLoading,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("auth_btn_signup"),
                colors = ButtonDefaults.buttonColors(
                  containerColor = VibrantGreen,
                  contentColor = Color(0xFF0B1014)
                ),
                shape = RoundedCornerShape(25.dp)
              ) {
                if (isSignUpLoading) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color(0xFF0B1014),
                    strokeWidth = 2.5.dp
                  )
                } else {
                  Text(
                    text = "Create Account & Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              Row(
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Already have an account?",
                  color = TextSecondary,
                  fontSize = 14.sp
                )
                TextButton(
                  onClick = {
                    selectedTab = AuthTab.SIGN_IN
                    signInError = null
                  }
                ) {
                  Text(
                    text = "Sign In",
                    color = VibrantGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      // Existing accounts list option
      if (currentAccounts.isNotEmpty()) {
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(
          thickness = 0.5.dp,
          color = BorderDivider,
          modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Saved Accounts on this Device",
          color = TextSecondary,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          currentAccounts.forEach { acc ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurfaceVariant)
                .clickable { onSwitchToAccount(acc.id) }
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("auth_saved_account_item_${acc.id}"),
              verticalAlignment = Alignment.CenterVertically
            ) {
              AvatarView(
                size = 40.dp,
                drawableName = acc.avatarDrawableName,
                initials = acc.fullName,
                backgroundColorHex = acc.avatarColorHex
              )

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = acc.fullName,
                  color = TextPrimary,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "${acc.username} • Tap to switch",
                  color = TextSecondary,
                  fontSize = 12.sp
                )
              }

              if (acc.isActive) {
                Box(
                  modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(VibrantGreen),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Active",
                    tint = Color(0xFF0B1014),
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(36.dp))
    }
  }
}
