# 🎯 GitHub Actions 构建指南

## 使用方法

### 方法1：自动触发（推荐给新手）
1. **推送代码到main分支** - 每次推送都会自动构建
```bash
git add .
git commit -m "增加老年用户功能"
git push origin main
```

### 方法2：手动触发（推荐）
1. **进入GitHub仓库** > **Actions标签**
2. **选择"Elderly-Friendly Single APK Build"工作流**
3. **点击"Run workflow"按钮** > **选择release**

### 方法3：基于标签触发
```bash
git tag v1.0.0-elderly
git push origin v1.0.0-elderly
```

## 下载位置

构建完成后，在以下位置获取APK：
- **Actions页面** > 最近的工作流 > **Artifacts** > `elderly-rustdesk-apk`
- 或直接下载到本地文件夹：`elderly-rustdesk.apk`

## 测试步骤

### 本地测试（可选）
```bash
# 本地快速构建
cd flutter
flutter build apk --release
```

### 使用Action构建
工作流已包含所有依赖项，无需本地环境！

## 构建标识
生成的APK包含以下定制标识：
- 文件名: `elderly-rustdesk.apk`
- 服务器: `223.26.59.132`
- 版本: V${{ github.run_number }}

## 注意事项
- ✅ 自动包含所有您的定制（服务器配置、权限优化）
- ✅ 支持Android 5.0-14
- ✅ 已优化UI界面适合老年用户