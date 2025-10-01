package com.example.myapplication

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Navigation.kt - Quản lý điều hướng trong ứng dụng
 * 
 * Đây là file chứa logic điều hướng giữa các màn hình trong ứng dụng.
 * Navigation Component giúp chúng ta:
 * 1. Định nghĩa các màn hình (screens/routes)
 * 2. Quản lý việc chuyển đổi giữa các màn hình
 * 3. Truyền dữ liệu giữa các màn hình
 * 4. Quản lý stack navigation (back button)
 */

// Định nghĩa các màn hình trong ứng dụng
sealed class Screen(val route: String) {
    object ContactList : Screen("contact_list")
    object ContactDetail : Screen("contact_detail/{contactId}") {
        fun createRoute(contactId: Int) = "contact_detail/$contactId"
    }
    object AddContact : Screen("add_contact")
    object EditContact : Screen("edit_contact/{contactId}") {
        fun createRoute(contactId: Int) = "edit_contact/$contactId"
    }
}

/**
 * Hàm chính để thiết lập navigation
 * @param activity: ComponentActivity - Activity chứa navigation
 * @param navController: NavHostController - Controller quản lý navigation
 */
@Composable
fun AppNavigation(activity: ComponentActivity, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.ContactList.route // Màn hình khởi động
    ) {
        // Màn hình danh sách liên hệ
        composable(Screen.ContactList.route) {
            ContactListScreen(
                activity = activity,
                navController = navController
            )
        }
        
        // Màn hình chi tiết liên hệ
        composable(Screen.ContactDetail.route) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId")?.toIntOrNull()
            if (contactId != null) {
                ContactDetailScreen(
                    contactId = contactId,
                    navController = navController
                )
            }
        }
        
        // Màn hình thêm liên hệ mới
        composable(Screen.AddContact.route) {
            AddContactScreen(
                navController = navController
            )
        }
        
        // Màn hình sửa liên hệ
        composable(Screen.EditContact.route) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId")?.toIntOrNull()
            if (contactId != null) {
                EditContactScreen(
                    contactId = contactId,
                    navController = navController
                )
            }
        }
    }
}

/**
 * Hàm tiện ích để tạo NavController
 * Thường được gọi trong MainActivity
 */
@Composable
fun rememberAppNavController(): NavHostController = rememberNavController()
