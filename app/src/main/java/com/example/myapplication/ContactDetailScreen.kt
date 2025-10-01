package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

/**
 * ContactDetailScreen.kt - Màn hình chi tiết liên hệ
 * 
 * Màn hình này hiển thị thông tin chi tiết của một liên hệ và các hành động có thể thực hiện:
 * - Gọi điện trực tiếp
 * - Mở ứng dụng quay số
 * - Chỉnh sửa thông tin
 * - Xóa liên hệ
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contactId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    
    // Tìm liên hệ theo ID
    val contact = sampleContacts.find { it.id == contactId }
    
    // State để lưu số điện thoại đang chờ gọi (khi chưa có quyền)
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
    
    // Nếu không tìm thấy liên hệ, hiển thị thông báo lỗi
    if (contact == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Không tìm thấy liên hệ")
        }
        return
    }
    
    // Hàm xử lý gọi điện
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
    
    // Hàm mở ứng dụng quay số
    fun openDialer(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phoneNumber")
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể mở ứng dụng gọi điện: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    // Giao diện màn hình
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chi tiết liên hệ",
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
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Avatar liên hệ
                ContactAvatar(
                    name = contact.name,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tên liên hệ
                Text(
                    text = contact.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = XiaomiColors.OnSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Số điện thoại
                Text(
                    text = contact.phoneNumber,
                    fontSize = 20.sp,
                    color = XiaomiColors.OnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Các nút hành động
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
                            navController.navigate(Screen.EditContact.createRoute(contact.id))
                        },
                        color = XiaomiColors.Warning
                    )

                    ActionButton(
                        icon = Icons.Default.Delete,
                        text = "Xóa",
                        onClick = {
                            sampleContacts.removeIf { it.id == contact.id }
                            navController.popBackStack()
                        },
                        color = XiaomiColors.Error
                    )
                }
            }
        }
    }
}