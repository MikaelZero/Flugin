package github.mikaelzero.flugin;

/**
 * @Author: MikaelZero
 * @CreateDate: 3/16/21 2:37 PM
 * @Description:
 */

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel.Result;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class FluginBridgeProxy {
    private final Map<String, Object> mPluginCacheMap;
    private final Map<String, String> mMethodCacheMap;

    private FluginBridgeProxy() {
        this.mPluginCacheMap = new HashMap<>();
        this.mMethodCacheMap = new HashMap<>();
    }

    public static FluginBridgeProxy getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static FluginBridgeProxy instance = new FluginBridgeProxy();

        private Holder() {
        }
    }

    public void putClass(String name, Object clazz) {
        mPluginCacheMap.put(name, clazz);
    }

    public void putMethod(String moduleName, String methodName, String realMethodName) {
        mMethodCacheMap.put(moduleName + "/" + methodName, realMethodName);
    }

    public void invoke(MethodCall call, Result result) {
        String callMethod = call.method;
        int index = callMethod.indexOf("/");
        String moduleName = callMethod.substring(0, index);
        String methodName = callMethod.substring(index + 1);
        Object obj = this.mPluginCacheMap.get(moduleName);
        if (obj == null) {
            System.out.println("can not find " + moduleName + "'s plugin class，please check plugin name");
        } else {
            try {
                String realMethod = (String) this.mMethodCacheMap.get(moduleName + "/" + methodName);
                if (realMethod != null) {
                    Method method = obj.getClass().getMethod(realMethod, MethodCall.class, Result.class);
                    method.invoke(obj, call, result);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var10) {
                var10.printStackTrace();
            }
        }
    }
}