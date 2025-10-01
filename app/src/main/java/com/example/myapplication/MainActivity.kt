package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

/**
 * MainActivity.kt - Activity chính của ứng dụng
 * 
 * Đây là điểm khởi đầu của ứng dụng Android. MainActivity:
 * 1. Là Activity đầu tiên được khởi chạy khi mở ứng dụng
 * 2. Thiết lập giao diện chính của ứng dụng
 * 3. Quản lý navigation giữa các màn hình
 * 4. Áp dụng theme và style cho toàn bộ ứng dụng
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bật EdgeToEdge để ứng dụng có thể sử dụng toàn bộ màn hình
        enableEdgeToEdge()
        
        // Thiết lập giao diện Compose
        setContent {
            MyApplicationTheme {
                // Tạo NavController để quản lý navigation
                val navController = rememberNavController()
                
                // Khởi tạo navigation với MainActivity
                AppNavigation(
                    activity = this,
                    navController = navController
                )
            }
        }
    }
}


