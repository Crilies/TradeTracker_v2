# Trade Tracker Android App

一款简洁高效的交易跟踪应用，帮助您记录和管理投资交易。

## 功能特点

- 📊 三个主界面：发现、持仓、我的
- 💹 实时盈亏计算
- 📱 小米超级岛支持（预留）
- 🤖 MCP服务接口（AI可调用）
- 📦 Room本地数据库存储

## 技术栈

- Kotlin + Jetpack Compose
- Room数据库
- Navigation导航
- Material Design 3

## 自动构建

本项目已配置GitHub Actions，推送到main/master分支后会自动构建APK。

### 获取APK

1. 推送代码到GitHub
2. 等待Actions完成（约5分钟）
3. 在Actions页面的Artifacts下载`app-debug.apk`

## 手动构建

### 前置条件
- Android Studio Hedgehog或更高版本
- JDK 17

### 步骤
1. 克隆项目
2. 用Android Studio打开
3. 等待Gradle同步完成
4. 点击运行或构建APK

## 项目结构

```
app/
├── src/main/java/com/example/tradetracker/
│   ├── data/          # 数据层（Room数据库）
│   ├── ui/           # UI层（Compose界面）
│   ├── service/      # MCP服务
│   └── MainActivity.kt
└── build.gradle.kts
```

## 模拟数据

应用内置5个示例产品：
1. 小米股票 (1810.HK)
2. 茅台基金 (519018)
3. 黄金期货 (AU2406)
4. 比特币 (BTC)
5. 国债逆回购 (204001)

## 许可证

MIT License