package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.io.Serializable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onSearchClick: () -> Unit
) {
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
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tìm kiếm"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

data class Contact(val id: Int, val name: String, val phoneNumber: String)

val sampleContacts = mutableStateListOf(
    Contact(1, "Nguyen Van A", "0123456789"),
    Contact(2, "Le Thi B", "0987654321"),
    Contact(3, "Tran Van C", "0121987654")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(activity: ComponentActivity) {
    val context = LocalContext.current

    // State variables
    var name by remember { mutableStateOf(TextFieldValue()) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue()) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var editNameError by remember { mutableStateOf<String?>(null) }
    var editPhoneError by remember { mutableStateOf<String?>(null) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var editingContact by remember { mutableStateOf<Contact?>(null) }
    var newName by remember { mutableStateOf(TextFieldValue()) }
    var newPhoneNumber by remember { mutableStateOf(TextFieldValue()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var pendingPhoneNumber by remember { mutableStateOf<String?>(null) }

    // Launcher để xin quyền gọi điện
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted && pendingPhoneNumber != null) {
            makeDirectCall(context, pendingPhoneNumber!!)
            pendingPhoneNumber = null
        } else {
            Toast.makeText(context, "Cần quyền gọi điện để sử dụng tính năng này", Toast.LENGTH_LONG).show()
            pendingPhoneNumber = null
        }
    }

    // Filtered contacts based on search query
    val displayedContacts by remember(searchQuery.text, sampleContacts.toList()) {
        derivedStateOf {
            searchContacts(searchQuery.text, sampleContacts.toList())
        }
    }

    // Function to handle call permission and make call
    fun handleCall(phoneNumber: String) {
        // Kiểm tra xem thiết bị có hỗ trợ telephony không
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            Toast.makeText(context, "Thiết bị này không hỗ trợ gọi điện", Toast.LENGTH_LONG).show()
            return
        }

        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED -> {
                makeDirectCall(context, phoneNumber)
            }
            else -> {
                pendingPhoneNumber = phoneNumber
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    // Function to open dialer
    fun openDialer(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể mở ứng dụng gọi điện: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopBar(onSearchClick = {
            // Logic focus vào TextField tìm kiếm nếu cần
        })

        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Tìm kiếm liên hệ") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search Icon")
            }
        )

        // Contact List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(displayedContacts.size) { index ->
                val contact = displayedContacts[index]
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

        // Add Contact Form
        TextField(
            value = name,
            onValueChange = {
                name = it
                nameError = null
            },
            label = { Text("Tên liên hệ") },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            isError = nameError != null,
            supportingText = {
                if (nameError != null) {
                    Text(
                        text = nameError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )

        TextField(
            value = phoneNumber,
            onValueChange = {
                phoneNumber = it
                phoneError = null
            },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            isError = phoneError != null,
            supportingText = {
                if (phoneError != null) {
                    Text(
                        text = phoneError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )

        Button(
            onClick = {
                nameError = validateName(name.text)
                phoneError = validatePhoneNumber(phoneNumber.text)

                if (nameError == null && phoneError == null) {
                    val newContact = Contact(
                        id = (sampleContacts.maxOfOrNull { it.id } ?: 0) + 1,
                        name = name.text,
                        phoneNumber = phoneNumber.text
                    )
                    sampleContacts.add(newContact)
                    name = TextFieldValue("")
                    phoneNumber = TextFieldValue("")
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
                editNameError = null
                editPhoneError = null
            },
            confirmButton = {
                if (editingContact != null) {
                    TextButton(
                        onClick = {
                            editNameError = validateName(newName.text)
                            editPhoneError = validatePhoneNumber(newPhoneNumber.text)

                            if (editNameError == null && editPhoneError == null) {
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
                                editNameError = null
                                editPhoneError = null
                            }
                        }
                    ) {
                        Text("Lưu")
                    }
                }
                TextButton(onClick = {
                    selectedContact = null
                    editingContact = null
                    editNameError = null
                    editPhoneError = null
                }) {
                    Text(if (editingContact != null) "Hủy" else "Đóng")
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
                            onValueChange = {
                                newName = it
                                editNameError = null
                            },
                            label = { Text("Tên liên hệ") },
                            isError = editNameError != null,
                            supportingText = {
                                if (editNameError != null) {
                                    Text(
                                        text = editNameError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        )
                        TextField(
                            value = newPhoneNumber,
                            onValueChange = {
                                newPhoneNumber = it
                                editPhoneError = null
                            },
                            label = { Text("Số điện thoại") },
                            isError = editPhoneError != null,
                            supportingText = {
                                if (editPhoneError != null) {
                                    Text(
                                        text = editPhoneError!!,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
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
                if (editingContact == null && selectedContact != null) {
                    // Gọi trực tiếp (yêu cầu quyền)
                    TextButton(
                        onClick = {
                            handleCall(selectedContact?.phoneNumber ?: "")
                        }
                    ) {
                        Text("Gọi")
                    }

                    // Mở ứng dụng gọi điện (không cần quyền)
                    TextButton(
                        onClick = {
                            openDialer(selectedContact?.phoneNumber ?: "")
                        }
                    ) {
                        Text("Quay số")
                    }

                    TextButton(
                        onClick = {
                            editingContact = selectedContact
                            newName = TextFieldValue(selectedContact!!.name)
                            newPhoneNumber = TextFieldValue(selectedContact!!.phoneNumber)
                            editNameError = null
                            editPhoneError = null
                        }
                    ) {
                        Text("Sửa")
                    }
                }
            }
        )
    }
}

// Utility functions
fun searchContacts(query: String, allContacts: List<Contact>): List<Contact> {
    if (query.isBlank()) {
        return allContacts
    }
    val lowerCaseQuery = query.lowercase()
    return allContacts.filter { contact ->
        contact.name.lowercase().contains(lowerCaseQuery) ||
                contact.phoneNumber.contains(lowerCaseQuery)
    }
}

fun validateName(name: String): String? {
    if (name.isBlank()) {
        return "Tên liên hệ không được để trống"
    }
    if (name.length < 2) {
        return "Tên liên hệ phải có ít nhất 2 ký tự"
    }
    return null
}

fun validatePhoneNumber(phone: String): String? {
    if (phone.isBlank()) {
        return "Số điện thoại không được để trống."
    }
    if (!phone.all { it.isDigit() }) {
        return "Số điện thoại chỉ được chứa chữ số."
    }
    if (phone.length !in 9..11) {
        return "Số điện thoại phải có từ 9 đến 11 số."
    }
    return null
}

// Function to make direct call (requires CALL_PHONE permission)
fun makeDirectCall(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        context.startActivity(intent)
    } catch (e: SecurityException) {
        Toast.makeText(context, "Lỗi bảo mật: ${e.message}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Không thể gọi điện: ${e.message}", Toast.LENGTH_LONG).show()
    }
}