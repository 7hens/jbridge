package cn.thens.jbridge

import android.webkit.JsPromptResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import java.util.regex.Pattern

internal class JsPromptClient(
    private val webView: WebView,
    private val nativeInterface: JBridge.Interface
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        if (newProgress >= 10) injectInitJs()
    }

    override fun onJsPrompt(
        view: WebView?, url: String?, message: String?,
        defaultValue: String?, result: JsPromptResult?
    ): Boolean {
        if (result == null) return false
        if (callNative(message ?: "")) {
            result.cancel()
            return true
        }
        return false
    }

    private fun eval(js: String) {
        JBridgeUtils.eval(webView, js)
    }

    private fun injectInitJs() {
        eval(initJsCode)
    }

    private val host = "jbridge://app"
    private val initCommand = "$host/init"
    private val funcPattern = Pattern.compile("^$host/call/([^/]+)/(.*)$")
    private val callbackPattern = Pattern.compile("^$host/callback/([^/]+)/(.*)$")

    private val initJsCode: String by lazy {
        """window.jBridge||function(){var n={},i={};function o(n,i,o){prompt("$host/"+n+"/"+i+"/"+o)}window.jBridge={register:function(n,o){i[n]=o},invoke:function(i,c,l){l&&(n[i]=l),o("call",i,c)},__call:function(n,c){var l=i[n];l&&l(c,function(i){o("callback",n,c)})},__callback:function(i,o){var c=n[i];c&&c(o)}},console.log("hello, jBridge")}();"""
    }

    private fun callNative(message: String): Boolean {
        if (message == initCommand) {
            injectInitJs()
            return true
        }
        return call(message, funcPattern, nativeInterface::call)
                || call(message, callbackPattern, nativeInterface::callback)
    }

    private fun call(message: String, pattern: Pattern, func: (String, String) -> Unit): Boolean {
        val matcher = pattern.matcher(message)
        if (!matcher.matches()) return false
        val action = matcher.group(1) ?: ""
        val param = matcher.group(2) ?: ""
        func(action, param)
        return true
    }
}