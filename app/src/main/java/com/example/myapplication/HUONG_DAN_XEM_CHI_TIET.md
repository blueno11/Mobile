# Hướng Dẫn Xem Chi Tiết Danh Bạ

## 🎯 Mục Tiêu
Để xem được màn hình chi tiết danh bạ, bạn cần đảm bảo ứng dụng có thể chạy được và navigation hoạt động đúng.

## 📱 Cách Xem Chi Tiết Danh Bạ

### Bước 1: Chạy Ứng Dụng
1. Mở Android Studio
2. Chọn thiết bị hoặc emulator
3. Nhấn nút **Run** (▶️) hoặc `Shift + F10`

### Bước 2: Điều Hướng Đến Chi Tiết
1. **Mở danh sách liên hệ** - Màn hình chính sẽ hiển thị danh sách liên hệ
2. **Nhấn vào một liên hệ** - Tap vào bất kỳ liên hệ nào trong danh sách
3. **Xem chi tiết** - Màn hình chi tiết sẽ mở ra với:
   - Avatar lớn của liên hệ
   - Tên liên hệ
   - Số điện thoại
   - 4 nút hành động: Gọi, Quay số, Sửa, Xóa

## 🔧 Nếu Không Thấy Chi Tiết

### Kiểm Tra Lỗi Compile
```bash
# Chạy lệnh này trong terminal của Android Studio
./gradlew clean build
```

### Kiểm Tra Import
Đảm bảo các file có import đầy đủ:
- `Navigation.kt` ✅
- `ContactDetailScreen.kt` ✅  
- `AddContactScreen.kt` ✅
- `EditContactScreen.kt` ✅
- `MainActivity.kt` ✅

### Kiểm Tra Dependencies
Trong `app/build.gradle.kts` phải có:
```kotlin
implementation("androidx.navigation:navigation-compose:2.7.6")
```

## 🎮 Các Tính Năng Có Thể Test

### 1. Xem Chi Tiết Liên Hệ
- Tap vào liên hệ → Màn hình chi tiết mở ra
- Thấy avatar, tên, số điện thoại
- Có nút "Quay lại" ở góc trên trái

### 2. Gọi Điện
- Nhấn nút "Gọi" → Hệ thống sẽ xin quyền gọi điện
- Nhấn nút "Quay số" → Mở ứng dụng quay số

### 3. Chỉnh Sửa Liên Hệ
- Nhấn nút "Sửa" → Màn hình chỉnh sửa mở ra
- Thay đổi tên/số điện thoại
- Nhấn "Lưu thay đổi" → Quay lại chi tiết

### 4. Xóa Liên Hệ
- Nhấn nút "Xóa" → Liên hệ bị xóa và quay lại danh sách

### 5. Thêm Liên Hệ Mới
- Nhấn nút "+" ở màn hình chính
- Nhập tên và số điện thoại
- Nhấn "Lưu liên hệ" → Quay lại danh sách

## 🐛 Troubleshooting

### Lỗi "Unresolved reference"
- **Nguyên nhân**: Thiếu import hoặc dependency
- **Giải pháp**: Sync project và rebuild

### Lỗi "Cannot find symbol"
- **Nguyên nhân**: Code chưa được compile
- **Giải pháp**: Clean và rebuild project

### Ứng dụng crash khi nhấn vào liên hệ
- **Nguyên nhân**: Navigation chưa được setup đúng
- **Giải pháp**: Kiểm tra MainActivity có gọi AppNavigation không

## 📋 Checklist

- [ ] Ứng dụng compile thành công
- [ ] Màn hình danh sách hiển thị liên hệ
- [ ] Tap vào liên hệ mở màn hình chi tiết
- [ ] Có nút "Quay lại" hoạt động
- [ ] Các nút hành động hiển thị đúng
- [ ] Navigation giữa các màn hình mượt mà

## 🎉 Kết Quả Mong Đợi

Khi hoạt động đúng, bạn sẽ thấy:
1. **Màn hình chính**: Danh sách liên hệ với 5 liên hệ mẫu
2. **Màn hình chi tiết**: Avatar lớn, thông tin chi tiết, 4 nút hành động
3. **Navigation**: Chuyển đổi mượt mà giữa các màn hình
4. **Tính năng**: Gọi điện, chỉnh sửa, xóa, thêm mới

Chúc bạn thành công! 🚀
