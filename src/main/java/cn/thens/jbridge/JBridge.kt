package cn.thens.jbridge

import android.annotation.SuppressLint
import android.os.Build
import android.webkit.*

@SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
class JBridge(private val webView: WebView) {
    private val callMap = HashMap<String, Call>()
    private val callbackMap = HashMap<String, Callback>()
    private val javascriptInterface = JsInterface()
    private val nativeInterface = NativeInterface()

    init {
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = JsPromptClient(webView, nativeInterface)
    }

    fun register(func: String, handler: Call) {
        callMap[func] = handler
    }

    fun unregister(func: String) {
        callMap.remove(func)
    }

    operator fun invoke(func: String, arg: String, callback: Callback) {
        callbackMap[func] = callback
        javascriptInterface.call(func, arg)
    }

    fun interface Call {
        operator fun invoke(arg: String, callback: Callback)
    }

    fun interface Callback {
        operator fun invoke(arg: String)
    }

    interface Interface {
        fun call(func: String, arg: String)
        fun callback(func: String, arg: String)
    }

    private inner class NativeInterface : Interface {
        @JavascriptInterface
        override fun call(func: String, arg: String) {
            callMap[func]?.invoke(arg) { data ->
                javascriptInterface.callback(func, data)
            }
        }

        @JavascriptInterface
        override fun callback(func: String, arg: String) {
            callbackMap[func]?.invoke(arg)
        }
    }

    private inner class JsInterface : Interface {
        override fun call(func: String, arg: String) {
            JBridgeUtils.eval(webView, "jBridge.__call('${func.quoted()}', '${arg.quoted()}');")
        }

        override fun callback(func: String, arg: String) {
            JBridgeUtils.eval(webView, "jBridge.__callback('${func.quoted()}', '${arg.quoted()}');")
        }

        private fun String.quoted(): String = replace("'", "\\'")
    }
}