package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import java.io.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = { Text("Danh Bạ Điện Thoại") },
        navigationIcon = {
            IconButton(onClick = { /* Xử lý nút quay lại */ }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* Xử lý tìm kiếm */ }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tìm kiếm"
                )
            }
        },
        modifier = Modifier.background(Color.Blue)
    )
}

data class Contact(val id: Int, val name: String, val phoneNumber: String)

val sampleContacts = mutableStateListOf(
    Contact(1, "Nguyen Van A", "0123456789"),
    Contact(2, "Le Thi B", "0987654321"),
    Contact(3, "Tran Van C", "0121987654")
)

@Composable
fun ContactListScreen(context: Context) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue()) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var editingContact by remember { mutableStateOf<Contact?>(null) }
    var newName by remember { mutableStateOf(TextFieldValue()) }
    var newPhoneNumber by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopBar()
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(sampleContacts.size) { index ->
                val contact = sampleContacts[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { selectedContact = contact },
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = contact.name, fontSize = 18.sp)
                    Text(text = contact.phoneNumber, fontSize = 16.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên liên hệ") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        Button(
            onClick = {
                // Kiểm tra ràng buộc thuộc tính
                if (name.text.isNotBlank() && name.text.length >= 2) {
                    if (phoneNumber.text.isNotBlank() && phoneNumber.text.all { it.isDigit() } && phoneNumber.text.length in 9..11) {
                        val newContact = Contact(
                            id = (sampleContacts.maxOfOrNull { it.id } ?: 0) + 1,
                            name = name.text,
                            phoneNumber = phoneNumber.text
                        )
                        sampleContacts.add(newContact)
                        name = TextFieldValue("")
                        phoneNumber = TextFieldValue("")
                    } else {
                        // Hiển thị thông báo lỗi cho người dùng
                        // Ví dụ: Toast, SnackBar
                    }
                } else {
                    // Hiển thị thông báo lỗi cho người dùng
                    // Ví dụ: Toast, SnackBar
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Thêm liên hệ")
        }
    }

    // Contact Detail & Edit Dialog
    if (selectedContact != null) {
        AlertDialog(
            onDismissRequest = {
                selectedContact = null
                editingContact = null
            },
            confirmButton = {
                if (editingContact != null) {
                    TextButton(
                        onClick = {
                            val contactToUpdate = editingContact!!
                            val updatedContact = contactToUpdate.copy(
                                name = newName.text,
                                phoneNumber = newPhoneNumber.text
                            )
                            val index = sampleContacts.indexOfFirst { it.id == updatedContact.id }
                            if (index != -1) {
                                sampleContacts[index] = updatedContact
                            }
                            selectedContact = null
                            editingContact = null
                        }
                    ) {
                        Text("Lưu")
                    }
                }
                TextButton(onClick = {
                    selectedContact = null
                    editingContact = null
                }) {
                    Text("Đóng")
                }
            },
            title = {
                Text(if (editingContact != null) "Sửa Liên Hệ" else "Chi tiết Liên Hệ")
            },
            text = {
                if (editingContact != null) {
                    Column {
                        TextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Tên liên hệ") }
                        )
                        TextField(
                            value = newPhoneNumber,
                            onValueChange = { newPhoneNumber = it },
                            label = { Text("Số điện thoại") }
                        )
                    }
                } else {
                    Column {
                        Text("Tên: ${selectedContact?.name}")
                        Text("Số điện thoại: ${selectedContact?.phoneNumber}")
                    }
                }
            },
            dismissButton = {
                if (editingContact == null) {
                    TextButton(onClick = { makeCall(context, selectedContact?.phoneNumber.toString()) }) {
                        Text("Gọi")
                    }
                    TextButton(
                        onClick = {
                            editingContact = selectedContact
                            newName = TextFieldValue(selectedContact!!.name)
                            newPhoneNumber = TextFieldValue(selectedContact!!.phoneNumber)
                        }
                    ) {
                        Text("Sửa")
                    }
                }
            }
        )
    }
}

fun makeCall(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_CALL)
    intent.data = Uri.parse("tel:$phoneNumber")
    context.startActivity(intent)
}