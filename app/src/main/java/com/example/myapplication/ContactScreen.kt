package com.example.myapplication

// ===== IMPORTS - CÁC THƯ VIỆN CẦN THIẾT =====
// Android core imports - Các thư viện cơ bản của Android
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

// Compose Activity imports - Thư viện để tích hợp Compose với Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

// Compose UI imports - Thư viện giao diện người dùng
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.io.Serializable

// ===== XIAOMI COLOR SCHEME - BỘ MÀU SẮC THEO PHONG CÁCH XIAOMI =====
/**
 * Object chứa các màu sắc được thiết kế theo phong cách Xiaomi
 * Sử dụng object để có thể truy cập từ bất kỳ đâu trong ứng dụng
 */
object XiaomiColors {
    val Primary = Color(0xFF007AFF)        // Màu xanh chính (giống iOS)
    val Background = Color(0xFFF5F5F5)     // Màu nền chính
    val Surface = Color.White              // Màu nền của các card/container
    val OnSurface = Color(0xFF1C1C1E)     // Màu chữ trên nền Surface
    val OnSurfaceVariant = Color(0xFF8E8E93) // Màu chữ phụ
    val Divider = Color(0xFFE5E5EA)       // Màu đường phân cách
    val Success = Color(0xFF34C759)       // Màu xanh lá (thành công)
    val Warning = Color(0xFFFF9500)       // Màu cam (cảnh báo)
    val Error = Color(0xFFFF3B30)         // Màu đỏ (lỗi)
}

// ===== XIAOMI TOP BAR - THANH ĐIỀU HƯỚNG TRÊN CÙNG =====
/**
 * Composable function tạo thanh điều hướng phía trên màn hình
 * @param title: Tiêu đề hiển thị trên thanh
 * @param onSearchClick: Hàm được gọi khi nhấn nút tìm kiếm
 * @param onAddClick: Hàm được gọi khi nhấn nút thêm
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XiaomiTopBar(
    title: String = "Danh bạ",
    onSearchClick: () -> Unit,
    onAddClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = XiaomiColors.OnSurface
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tìm kiếm",
                    tint = XiaomiColors.OnSurface
                )
            }
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm liên hệ",
                    tint = XiaomiColors.OnSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = XiaomiColors.Surface
        )
    )
}

// ===== DATA CLASSES VÀ DỮ LIỆU MẪU =====
/**
 * Data class định nghĩa cấu trúc dữ liệu cho một liên hệ
 * @param id: ID duy nhất của liên hệ
 * @param name: Tên liên hệ
 * @param phoneNumber: Số điện thoại
 */
data class Contact(val id: Int, val name: String, val phoneNumber: String)

/**
 * Danh sách liên hệ mẫu để test ứng dụng
 * Sử dụng mutableStateListOf để có thể thay đổi và tự động cập nhật UI
 */
val sampleContacts = mutableStateListOf(
    Contact(1, "Nguyen Van A", "0123456789"),
    Contact(2, "Le Thi B", "0987654321"),
    Contact(3, "Tran Van C", "0121987654"),
    Contact(4, "Pham Thi D", "0456789123"),
    Contact(5, "Hoang Van E", "0789123456")
)

// ===== CONTACT AVATAR - AVATAR HIỂN THỊ CHỮ CÁI ĐẦU =====
/**
 * Composable tạo avatar tròn hiển thị chữ cái đầu của tên liên hệ
 * @param name: Tên liên hệ để lấy chữ cái đầu
 * @param modifier: Modifier để tùy chỉnh kích thước và vị trí
 */
@Composable
fun ContactAvatar(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(XiaomiColors.Primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ContactItem(
    contact: Contact,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(name = contact.name)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = XiaomiColors.OnSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = contact.phoneNumber,
                fontSize = 14.sp,
                color = XiaomiColors.OnSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    color: Color = XiaomiColors.Primary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = XiaomiColors.OnSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun XiaomiSearchBar(
    searchQuery: TextFieldValue,
    onSearchQueryChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = {
            Text(
                "Tìm kiếm liên hệ",
                color = XiaomiColors.OnSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = XiaomiColors.OnSurfaceVariant
            )
        },
        trailingIcon = {
            if (searchQuery.text.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange(TextFieldValue("")) }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = XiaomiColors.OnSurfaceVariant
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = XiaomiColors.Primary,
            unfocusedBorderColor = XiaomiColors.Divider,
            focusedContainerColor = XiaomiColors.Surface,
            unfocusedContainerColor = XiaomiColors.Surface
        ),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    activity: ComponentActivity,
    navController: androidx.navigation.NavController? = null
) {
    val context = LocalContext.current

    // State variables
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var pendingPhoneNumber by remember { mutableStateOf<String?>(null) }

    // Add contact form states
    var addName by remember { mutableStateOf(TextFieldValue()) }
    var addPhoneNumber by remember { mutableStateOf(TextFieldValue()) }
    var addNameError by remember { mutableStateOf<String?>(null) }
    var addPhoneError by remember { mutableStateOf<String?>(null) }

    // Edit contact form states
    var editName by remember { mutableStateOf(TextFieldValue()) }
    var editPhoneNumber by remember { mutableStateOf(TextFieldValue()) }
    var editNameError by remember { mutableStateOf<String?>(null) }
    var editPhoneError by remember { mutableStateOf<String?>(null) }

    // Permission launcher
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

    // Filtered contacts
    val displayedContacts by remember(searchQuery.text, sampleContacts.toList()) {
        derivedStateOf {
            searchContacts(searchQuery.text, sampleContacts.toList())
        }
    }

    // Call handling functions
    fun handleCall(phoneNumber: String) {
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

    fun openDialer(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể mở ứng dụng gọi điện: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(XiaomiColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            XiaomiTopBar(
                onSearchClick = { /* Focus search */ },
                onAddClick = { 
                    // Nếu có navigation, chuyển đến màn hình thêm liên hệ
                    if (navController != null) {
                        navController.navigate(Screen.AddContact.route)
                    } else {
                        // Nếu không có navigation, hiển thị dialog như cũ
                        showAddDialog = true
                    }
                }
            )

            XiaomiSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )

            // Contact List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(XiaomiColors.Surface)
            ) {
                items(displayedContacts.size) { index ->
                    ContactItem(
                        contact = displayedContacts[index],
                        onClick = { 
                            // Nếu có navigation, chuyển đến màn hình chi tiết
                            if (navController != null) {
                                navController.navigate(Screen.ContactDetail.createRoute(displayedContacts[index].id))
                            } else {
                                // Nếu không có navigation, hiển thị dialog như cũ
                                selectedContact = displayedContacts[index]
                            }
                        }
                    )

                    if (index < displayedContacts.size - 1) {
                        Divider(
                            color = XiaomiColors.Divider,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(start = 80.dp)
                        )
                    }
                }
            }
        }
    }

    // Contact Detail Dialog
    selectedContact?.let { contact ->
        AlertDialog(
            onDismissRequest = { selectedContact = null },
            containerColor = XiaomiColors.Surface,
            title = null,
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    ContactAvatar(
                        name = contact.name,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = contact.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = XiaomiColors.OnSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = contact.phoneNumber,
                        fontSize = 18.sp,
                        color = XiaomiColors.OnSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ActionButton(
                            icon = Icons.Default.Call,
                            text = "Gọi",
                            onClick = { handleCall(contact.phoneNumber) },
                            color = XiaomiColors.Success
                        )

                        ActionButton(
                            icon = Icons.Default.Phone,
                            text = "Quay số",
                            onClick = { openDialer(contact.phoneNumber) },
                            color = XiaomiColors.Primary
                        )

                        ActionButton(
                            icon = Icons.Default.Edit,
                            text = "Sửa",
                            onClick = {
                                editName = TextFieldValue(contact.name)
                                editPhoneNumber = TextFieldValue(contact.phoneNumber)
                                editNameError = null
                                editPhoneError = null
                                showEditDialog = true
                            },
                            color = XiaomiColors.Warning
                        )

                        ActionButton(
                            icon = Icons.Default.Delete,
                            text = "Xóa",
                            onClick = {
                                sampleContacts.removeIf { it.id == contact.id }
                                selectedContact = null
                            },
                            color = XiaomiColors.Error
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { selectedContact = null },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = XiaomiColors.Primary
                    )
                ) {
                    Text("Đóng")
                }
            }
        )
    }

    // Add Contact Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                addName = TextFieldValue("")
                addPhoneNumber = TextFieldValue("")
                addNameError = null
                addPhoneError = null
            },
            containerColor = XiaomiColors.Surface,
            title = {
                Text(
                    "Thêm liên hệ mới",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = XiaomiColors.OnSurface
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = addName,
                        onValueChange = {
                            addName = it
                            addNameError = null
                        },
                        label = { Text("Tên liên hệ") },
                        isError = addNameError != null,
                        supportingText = {
                            addNameError?.let {
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

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = addPhoneNumber,
                        onValueChange = {
                            addPhoneNumber = it
                            addPhoneError = null
                        },
                        label = { Text("Số điện thoại") },
                        isError = addPhoneError != null,
                        supportingText = {
                            addPhoneError?.let {
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
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        addNameError = validateName(addName.text)
                        addPhoneError = validatePhoneNumber(addPhoneNumber.text)

                        if (addNameError == null && addPhoneError == null) {
                            val newContact = Contact(
                                id = (sampleContacts.maxOfOrNull { it.id } ?: 0) + 1,
                                name = addName.text,
                                phoneNumber = addPhoneNumber.text
                            )
                            sampleContacts.add(newContact)
                            showAddDialog = false
                            addName = TextFieldValue("")
                            addPhoneNumber = TextFieldValue("")
                            addNameError = null
                            addPhoneError = null
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = XiaomiColors.Primary
                    )
                ) {
                    Text("Thêm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        addName = TextFieldValue("")
                        addPhoneNumber = TextFieldValue("")
                        addNameError = null
                        addPhoneError = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = XiaomiColors.OnSurfaceVariant
                    )
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    // Edit Contact Dialog
    if (showEditDialog && selectedContact != null) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
                editNameError = null
                editPhoneError = null
            },
            containerColor = XiaomiColors.Surface,
            title = {
                Text(
                    "Sửa liên hệ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = XiaomiColors.OnSurface
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = {
                            editName = it
                            editNameError = null
                        },
                        label = { Text("Tên liên hệ") },
                        isError = editNameError != null,
                        supportingText = {
                            editNameError?.let {
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

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editPhoneNumber,
                        onValueChange = {
                            editPhoneNumber = it
                            editPhoneError = null
                        },
                        label = { Text("Số điện thoại") },
                        isError = editPhoneError != null,
                        supportingText = {
                            editPhoneError?.let {
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
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        editNameError = validateName(editName.text)
                        editPhoneError = validatePhoneNumber(editPhoneNumber.text)

                        if (editNameError == null && editPhoneError == null) {
                            val updatedContact = selectedContact!!.copy(
                                name = editName.text,
                                phoneNumber = editPhoneNumber.text
                            )
                            val index = sampleContacts.indexOfFirst { it.id == updatedContact.id }
                            if (index != -1) {
                                sampleContacts[index] = updatedContact
                            }
                            selectedContact = updatedContact
                            showEditDialog = false
                            editNameError = null
                            editPhoneError = null
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = XiaomiColors.Primary
                    )
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        editNameError = null
                        editPhoneError = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = XiaomiColors.OnSurfaceVariant
                    )
                ) {
                    Text("Hủy")
                }
            }
        )
    }
}

// Utility functions remain the same
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