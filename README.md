# LSP Identity Lab

这是一个**自有测试目标 + LSPosed/libxposed Hook 模块**的学习项目，用来验证 Android 身份状态机、方法 Hook、作用域和动态日志链路。

> 目标仅为 `com.example.identitylab`。不针对第三方应用，也不执行网络、账号或服务流量/配额操作。

## 当前版本

- Android target/compile SDK: 35
- Java: 17
- LSPosed/libxposed Modern API: **102**
- Module dependency: `io.github.libxposed:api:102.0.0` (`compileOnly`)
- Modern entry: `META-INF/xposed/java_init.list`
- Scope: `com.example.identitylab`
- Hook: `IdentityStore.getMachineId()`
- Hook 行为：只记录原返回值，随后原样返回，不修改结果

## 目录

- `DemoTarget/`：可安装的本地测试目标
- `DemoLsp/`：LSPosed/libxposed API 102 模块
- `local-test/`：不依赖 Android 的状态机测试
- `tools/verify_structure.py`：模块结构/作用域/安全边界检查

## 构建

需要 Android SDK 35、JDK 17，以及 Gradle 8.7/Android Gradle Plugin 环境。

```text
gradle :DemoTarget:assembleDebug :DemoLsp:assembleDebug
```

预期 APK：

```text
DemoTarget/build/outputs/apk/debug/DemoTarget-debug.apk
DemoLsp/build/outputs/apk/debug/DemoLsp-debug.apk
```

## 本地验证

```text
python3 tools/verify_structure.py
```

## 边界

原始过程文件仅作为行为分析参考。这里仅复刻其中可在自有 Demo 中安全验证的状态机和 Hook 学习部分，不提供针对第三方应用的可直接使用补丁、Hook 或流量/配额绕过实现。
