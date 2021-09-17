package github.mikaelzero.flugin

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.lang.Exception

/**
 * @Author:         MikaelZero
 * @CreateDate:     3/15/21 5:24 PM
 * @Description:
 */
class NativeChannel : MethodChannel.MethodCallHandler {
    lateinit var methodChannel: MethodChannel

    fun registerChannel() {
        methodChannel =
            MethodChannel(Flugin.instance()?.engine?.dartExecutor, "NativeChannel")
        methodChannel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        try {
            FluginBridgeProxy.getInstance()
                .invoke(call, result)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unRegister() {
        methodChannel.setMethodCallHandler(null)
    }

}