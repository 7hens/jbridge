# JBridge

jBridge 是一个十分轻量且安全的 Javascript Bridge，并在原生和 Web 端提供了一致的API和开发体验。

## Setting up the dependency

```groovy
// maven { url "https://jitpack.io" }
implementation("com.github.7hens:jbridge:0.1")
```

## Sample Usage

On the native side (Kotlin)

```kotlin
val jBridge = JBridge(webView)

// 注册一个原生方法，供JS调用
jBridge.register("nativeMethod") { arg, callback ->
    callback("jsCallback")
}

// 调用JS的方法
jBridge.invoke("jsMethod", "jsArg") { arg -> }
```

On the web side (javascript)

```javascript
// 注册一个JS方法，供原生调用
jBridge.register("jsMethod", function (arg, callback) {
    callback("nativeCallback")
})

// 调用原生方法
jBridge.invoke("nativeMethod", "nativeArg", function (arg) { })
```
