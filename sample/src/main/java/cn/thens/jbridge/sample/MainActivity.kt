package cn.thens.jbridge.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cn.thens.jbridge.JBridge
import cn.thens.jbridge.JBridgeUtils
import cn.thens.jbridge.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val v by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        JBridgeUtils.init(v.webView)

        val jBridge = JBridge(v.webView)
        jBridge.register("toast") { arg, callback ->
            Toast.makeText(this, arg, Toast.LENGTH_SHORT).show()
            callback("hello, android")
        }
        v.webView.loadUrl("file:///android_asset/jbridge.html")
    }

    override fun onDestroy() {
        super.onDestroy()
        JBridgeUtils.dispose(v.webView)
    }
}