# 解决 ZipException: Archive is not a ZIP archive 错误

## 错误原因
这个错误通常是因为 Gradle 下载的 ZIP 文件损坏或不完整导致的。可能的原因：
1. 网络中断导致下载不完整
2. 下载过程中文件被损坏
3. Gradle 缓存中的文件损坏

## 解决方案（按顺序尝试）

### 方法1：在 Android Studio 中清理（最简单）

1. **关闭 Android Studio**
   - 确保完全关闭，包括所有项目窗口

2. **重新打开 Android Studio**

3. **清理缓存**
   - File → Invalidate Caches / Restart
   - 选择 "Invalidate and Restart"
   - 等待 Android Studio 重启

4. **重新同步项目**
   - File → Sync Project with Gradle Files
   - 等待 Gradle 重新下载所有文件

### 方法2：手动清理 Gradle 缓存（如果方法1不行）

**重要：必须先关闭 Android Studio 和所有 Gradle 进程！**

1. **关闭 Android Studio**

2. **结束所有 Java/Gradle 进程**
   - 打开任务管理器（Ctrl+Shift+Esc）
   - 结束所有 `java.exe` 和 `gradle.exe` 进程

3. **删除项目中的缓存**
   ```
   删除文件夹：android/.gradle
   删除文件夹：android/build（如果存在）
   删除文件夹：android/app/build（如果存在）
   ```

4. **删除用户目录的 Gradle 缓存**
   ```
   删除文件夹：C:\Users\你的用户名\.gradle\wrapper\dists\gradle-8.5-bin
   ```
   或者删除整个 wrapper 目录：
   ```
   删除文件夹：C:\Users\你的用户名\.gradle\wrapper\dists
   ```

5. **重新打开 Android Studio 并同步项目**

### 方法3：使用命令行清理（PowerShell）

**注意：必须先关闭 Android Studio！**

```powershell
# 切换到项目目录
cd F:\make-money2\400\android

# 删除项目缓存
Remove-Item -Recurse -Force .gradle -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force build -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force app\build -ErrorAction SilentlyContinue

# 删除用户目录的 Gradle wrapper 缓存
$gradleCache = "$env:USERPROFILE\.gradle\wrapper\dists\gradle-8.5-bin"
if (Test-Path $gradleCache) {
    # 先尝试结束所有 Java 进程
    Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
    Remove-Item -Recurse -Force $gradleCache -ErrorAction SilentlyContinue
}
```

### 方法4：检查网络和代理设置

如果上述方法都不行，可能是网络问题：

1. **检查代理设置**
   - File → Settings → Appearance & Behavior → System Settings → HTTP Proxy
   - 如果有代理，尝试禁用或配置正确的代理

2. **使用国内镜像（如果在中国）**
   
   在 `android/build.gradle` 的 `allprojects` 块中添加：
   ```gradle
   allprojects {
       repositories {
           // 阿里云镜像（可选，如果下载慢）
           maven { url 'https://maven.aliyun.com/repository/google' }
           maven { url 'https://maven.aliyun.com/repository/central' }
           maven { url 'https://maven.aliyun.com/repository/gradle-plugin' }
           google()
           mavenCentral()
       }
   }
   ```

### 方法5：重新下载 Gradle（最后手段）

如果所有方法都不行，可以手动下载 Gradle：

1. 访问：https://services.gradle.org/distributions/gradle-8.5-bin.zip
2. 下载 ZIP 文件
3. 解压到：`C:\Users\你的用户名\.gradle\wrapper\dists\gradle-8.5-bin\5t9huq95ubn472n8rpzujfbqh\`
   （注意：哈希目录名可能不同，需要查看实际目录名）

## 验证修复

清理完成后：
1. 重新打开 Android Studio
2. File → Sync Project with Gradle Files
3. 等待 Gradle 重新下载所有依赖
4. 查看 Build 输出窗口，确认没有错误

## 预防措施

1. 保持网络连接稳定
2. 如果经常遇到此问题，考虑使用 Gradle 离线模式（不推荐）
3. 定期清理 Gradle 缓存
