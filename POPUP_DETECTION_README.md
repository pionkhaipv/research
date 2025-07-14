# Hệ thống phát hiện Popup Quảng cáo bằng AccessibilityService

## Tổng quan
Hệ thống này sử dụng AccessibilityService để phát hiện popup quảng cáo từ các ứng dụng khác trên thiết bị Android.

## Các thành phần chính

### 1. AccessibilityService
- **File**: `PopupDetectionService.kt`
- **Chức năng**: Phát hiện popup quảng cáo dựa trên:
  - Từ khóa trong text content
  - Class name của view
  - Layout characteristics (close button, skip button, ad label)
  - View ID patterns

### 2. Configuration
- **File**: `accessibility_service_config.xml`
- **Mô tả**: Cấu hình service trong XML resource

### 3. Helper Utilities
- **File**: `AccessibilityServiceHelper.kt`
- **Chức năng**: 
  - Kiểm tra trạng thái service
  - Mở settings để enable service

### 4. Data Models
- **File**: `PopupDetection.kt`
- **Models**: 
  - `PopupDetection`: Thông tin popup đã phát hiện
  - `PopupStatistics`: Thống kê popup

### 5. UI Components
- **Fragment**: `PopupStatisticsFragment.kt`
- **ViewModel**: `PopupStatisticsViewModel.kt`
- **Adapter**: `PopupDetectionAdapter.kt`
- **Layouts**: 
  - `fragment_popup_statistics.xml`
  - `item_popup_detection.xml`

### 6. BroadcastReceiver
- **File**: `PopupDetectionReceiver.kt`
- **Chức năng**: Nhận thông báo khi phát hiện popup

## Cách sử dụng

### 1. Enable AccessibilityService
```kotlin
// Kiểm tra trạng thái service
val status = AccessibilityServiceHelper.getServiceStatus(context)
if (!status.canDetectPopups) {
    AccessibilityServiceHelper.openAccessibilitySettings(context)
}
```

### 2. Nhận thông báo popup
```kotlin
// Đăng ký BroadcastReceiver
val filter = IntentFilter(PopupDetectionService.ACTION_POPUP_DETECTED)
LocalBroadcastManager.getInstance(context)
    .registerReceiver(popupReceiver, filter)
```

### 3. Hiển thị thống kê
```kotlin
// Sử dụng PopupStatisticsFragment để hiển thị thống kê
// Fragment tự động cập nhật khi có popup mới được phát hiện
```

## Quyền cần thiết
```xml
<uses-permission android:name="android.permission.BIND_ACCESSIBILITY_SERVICE" />
```

## Đăng ký trong AndroidManifest.xml
```xml
<service
    android:name=".service.PopupDetectionService"
    android:exported="false"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
    <meta-data
        android:name="android.accessibilityservice"
        android:resource="@xml/accessibility_service_config" />
</service>
```

## Tính năng phát hiện

### Phương pháp phát hiện
1. **Text Analysis**: Tìm từ khóa quảng cáo trong text content
2. **Layout Analysis**: Phát hiện pattern layout của popup quảng cáo
3. **Class Name Analysis**: Kiểm tra tên class của view
4. **View ID Analysis**: Phân tích ID của view

### Từ khóa phát hiện
- Tiếng Việt: "quảng cáo"
- Tiếng Anh: "advertisement", "ads", "ad", "promotion", "sponsored"
- Action words: "download", "install", "claim", "win", "free"

### Confidence Score
Hệ thống tính toán độ tin cậy dựa trên:
- Text content match: +30%
- Content description match: +20% 
- View ID match: +40%
- Class name match: +30%
- Layout characteristics: +20%

## Lưu ý quan trọng

### 1. Privacy & Security
- Service chỉ phân tích UI elements, không đọc dữ liệu nhạy cảm
- Không lưu trữ thông tin cá nhân
- Chỉ thu thập metadata về popup

### 2. Performance
- Sử dụng Handler để tránh block UI thread
- Giới hạn số lượng detection lưu trữ (max 1000)
- Recycle AccessibilityNodeInfo để tránh memory leak

### 3. Compatibility
- Yêu cầu Android API 24+ (Android 7.0)
- Hoạt động với tất cả ứng dụng có popup overlay

## Troubleshooting

### Service không hoạt động
1. Kiểm tra quyền AccessibilityService đã được bật
2. Restart service nếu cần thiết
3. Kiểm tra log để debug

### Không phát hiện popup
1. Kiểm tra từ khóa có phù hợp không
2. Thêm từ khóa mới vào danh sách
3. Điều chỉnh confidence threshold

### Performance issues
1. Giảm frequency của analysis
2. Tối ưu hóa node traversal
3. Sử dụng background thread cho heavy operations

## Mở rộng tương lai
1. Machine Learning để cải thiện độ chính xác
2. Cloud-based detection patterns
3. User feedback system
4. Automatic popup blocking
5. Integration với antivirus/security apps
