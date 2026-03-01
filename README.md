# COC Helper (Android / Kotlin)

这是一个可以直接在 **Android Studio** 打开的“部落冲突辅助流程”基础工程，使用 **Kotlin + XML** 实现。

## 已实现内容

- **状态机场景识别**（基础流程）：
  - 待机
  - 主城
  - 搜索打鱼目标
  - 战斗中
  - 回城结算
  - 升级建筑
  - 升级兵种
  - 流程完成
- **主页面（XML）**：支持开始流程、下一步、自动跑完、重置，并输出日志。
- **坐标记录功能**：可录入“按钮名 + X/Y”，支持持久化保存、清空与导出到剪贴板。
- **悬浮窗服务（Overlay）**：可启动悬浮窗按钮，一键回到流程页面。

> 说明：这是“基础流程骨架”，用于后续接入截图识别、ADB/无障碍点击、脚本调度等自动化能力。

## 项目结构

- `app/src/main/java/com/example/cochelper/AssistantStateMachine.kt`：状态机核心。
- `app/src/main/java/com/example/cochelper/MainActivity.kt`：流程控制 + 坐标录入页面。
- `app/src/main/java/com/example/cochelper/CoordinateStore.kt`：坐标数据结构与编解码。
- `app/src/main/java/com/example/cochelper/OverlayService.kt`：悬浮窗服务。
- `app/src/main/res/layout/activity_main.xml`：主页面布局。
- `app/src/main/res/layout/view_floating_window.xml`：悬浮窗布局。

## 使用方式

1. 使用 Android Studio 打开根目录。
2. 同步 Gradle。
3. 运行 `app` 到真机或模拟器。
4. 如需悬浮窗，先授予“在其他应用上层显示”权限。

## 测试

- 单元测试文件：
  - `app/src/test/java/com/example/cochelper/AssistantStateMachineTest.kt`
  - `app/src/test/java/com/example/cochelper/CoordinateCodecTest.kt`
