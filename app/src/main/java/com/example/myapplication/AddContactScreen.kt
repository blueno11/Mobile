package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * AddContactScreen.kt - Màn hình thêm liên hệ mới
 * 
 * Màn hình này cho phép người dùng:
 * - Nhập tên liên hệ
 * - Nhập số điện thoại
 * - Validate dữ liệu đầu vào
 * - Lưu liên hệ mới vào danh sách
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactScreen(navController: NavController) {
    // State cho form nhập liệu
    var name by remember { mutableStateOf(TextFieldValue()) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue()) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    
    // Hàm xử lý lưu liên hệ
    fun saveContact() {
        nameError = validateName(name.text)
        phoneError = validatePhoneNumber(phoneNumber.text)
        
        if (nameError == null && phoneError == null) {
            val newContact = Contact(
                id = (sampleContacts.maxOfOrNull { it.id } ?: 0) + 1,
                name = name.text,
                phoneNumber = phoneNumber.text
            )
            sampleContacts.add(newContact)
            navController.popBackStack() // Quay lại màn hình trước
        }
    }
    
    // Giao diện màn hình
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Thêm liên hệ mới",
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
                // Form nhập tên
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

                // Form nhập số điện thoại
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

                // Nút lưu
                Button(
                    onClick = { saveContact() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = XiaomiColors.Primary
                    )
                ) {
                    Text(
                        text = "Lưu liên hệ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}