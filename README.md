# 打地鼠游戏 (Whack-A-Mole)

一个基于 Android 平台开发的打地鼠游戏，具有实时定位功能和数据同步功能。

## 项目截图
![项目截图1.png](assets%2F%CF%EE%C4%BF%BD%D8%CD%BC1.png)
![项目截图2.png](assets%2F%CF%EE%C4%BF%BD%D8%CD%BC2.png)
![项目截图3.png](assets%2F%CF%EE%C4%BF%BD%D8%CD%BC3.png)

## 功能特点

- 多难度等级的打地鼠游戏
  - 初级 (beginner)
  - 简单 (easy)
  - 中等 (medium)
  - 困难 (hard)
  - 专家 (expert)
  - 噩梦 (nightmare)
- 用户系统
  - 用户名注册和修改
  - 游戏数据与用户关联
- 实时定位功能
  - 使用高德地图 SDK
  - 显示当前位置信息
  - 地理位置可视化
- 数据同步系统
  - 本地 SQLite 数据存储
  - 远程服务器数据同步
  - 支持游戏记录和位置信息同步
- 历史记录查看
  - 查看历史游戏记录
  - 查看历史位置信息
  - 支持记录导航和跳转

## 技术栈

- Android SDK
- 高德地图 SDK
- SQLite 数据库
- Retrofit2 网络请求
- SharedPreferences 数据存储

## 系统要求

- Android 设备
- Android API Level >= 21
- 需要网络连接
- 需要位置权限

## 安装说明

1. 克隆项目到本地

```bash
git clone https://github.com/xiaohan2004/Whackamole
```

2. 在 Android Studio 中打开项目

3. 配置高德地图 API Key
   - 在 `LocationHelper.java` 中替换为你的 API Key
   ```java
   AMapLocationClient.setApiKey("你的API Key");
   ```

4. 配置服务器地址
   - 在 `RetrofitClient.java` 中设置你的服务器地址
   ```java
   private static final String BASE_URL = "你的服务器地址";
   ```

5. 构建并运行项目

## 使用说明

1. 首次启动时，输入用户名
2. 在主界面可以：
   - 开始新游戏
   - 查看历史记录
   - 访问更多功能
3. 游戏过程中：
   - 点击出现的地鼠获得分数
   - 不同难度模式下地鼠出现速度和数量不同
4. 实时定位功能：
   - 在更多功能中可以开启实时定位
   - 查看当前位置信息和地图显示
5. 数据同步：
   - 在更多功能中可以手动同步数据
   - 同步游戏记录和位置信息

## 项目结构

主要类文件说明：
- `MainActivity.java`: 应用主入口
- `GameActivity.java`: 游戏主逻辑
- `LocationHelper.java`: 位置服务相关功能
- `RetrofitClient.java`: 网络请求客户端
- `GameRecordDBHelper.java`: 游戏记录数据库操作
- `LocationDBHelper.java`: 位置信息数据库操作
- `ActInfo.java`: 数据模型类
- `MyApplication.java`: 应用程序类

## 许可证

MIT License

## 注意事项
项目中的高德地图 API Key 已失效，无泄露风险。
