package github.mikaelzero.flugin

import io.flutter.embedding.engine.plugins.FlutterPlugin

/**
 * @Author:         MikaelZero
 * @CreateDate:     3/16/21 1:27 PM
 * @Description:
 */
class FluginPlugin : FlutterPlugin {
    lateinit var nativeChannel: NativeChannel
    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        nativeChannel = NativeChannel()
        nativeChannel.registerChannel()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        nativeChannel.unRegister()
    }
}