package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * EditContactScreen.kt - Màn hình chỉnh sửa liên hệ
 * 
 * Màn hình này cho phép người dùng:
 * - Chỉnh sửa tên liên hệ hiện có
 * - Chỉnh sửa số điện thoại hiện có
 * - Validate dữ liệu đầu vào
 * - Lưu thay đổi vào danh sách
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContactScreen(
    contactId: Int,
    navController: NavController
) {
    // Tìm liên hệ cần chỉnh sửa
    val contact = sampleContacts.find { it.id == contactId }
    
    // State cho form chỉnh sửa
    var name by remember { mutableStateOf(TextFieldValue(contact?.name ?: "")) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue(contact?.phoneNumber ?: "")) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    
    // Nếu không tìm thấy liên hệ, quay lại màn hình trước
    if (contact == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }
    
    // Hàm xử lý lưu thay đổi
    fun saveChanges() {
        nameError = validateName(name.text)
        phoneError = validatePhoneNumber(phoneNumber.text)
        
        if (nameError == null && phoneError == null) {
            val updatedContact = contact.copy(
                name = name.text,
                phoneNumber = phoneNumber.text
            )
            val index = sampleContacts.indexOfFirst { it.id == contactId }
            if (index != -1) {
                sampleContacts[index] = updatedContact
            }
            navController.popBackStack() // Quay lại màn hình trước
        }
    }
    
    // Giao diện màn hình
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chỉnh sửa liên hệ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = XiaomiColors.OnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = XiaomiColors.OnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = XiaomiColors.Surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(XiaomiColors.Background)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Form chỉnh sửa tên
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null // Xóa lỗi khi người dùng nhập
                    },
                    label = { Text("Tên liên hệ") },
                    isError = nameError != null,
                    supportingText = {
                        nameError?.let {
                            Text(
                                text = it,
                                color = XiaomiColors.Error,
                                fontSize = 12.sp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = XiaomiColors.Primary,
                        unfocusedBorderColor = XiaomiColors.Divider
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Form chỉnh sửa số điện thoại
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        phoneError = null // Xóa lỗi khi người dùng nhập
                    },
                    label = { Text("Số điện thoại") },
                    isError = phoneError != null,
                    supportingText = {
                        phoneError?.let {
                            Text(
                                text = it,
                                color = XiaomiColors.Error,
                                fontSize = 12.sp
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = XiaomiColors.Primary,
                        unfocusedBorderColor = XiaomiColors.Divider
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Nút lưu thay đổi
                Button(
                    onClick = { saveChanges() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = XiaomiColors.Primary
                    )
                ) {
                    Text(
                        text = "Lưu thay đổi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}