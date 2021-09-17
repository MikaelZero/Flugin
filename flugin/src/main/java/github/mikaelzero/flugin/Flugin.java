package github.mikaelzero.flugin;

import android.app.Application;

import com.idlefish.flutterboost.FlutterBoost;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.view.FlutterMain;

/**
 * @Author: MikaelZero
 * @CreateDate: 3/16/21 4:56 PM
 * @Description:
 */
public class Flugin {
    private static Flugin sInstance = null;
    private String engineName;

    public static Flugin instance() {
        if (sInstance == null) {
            sInstance = new Flugin();
        }
        return sInstance;
    }

    public void setup(FlutterEngine engine, String engineName) {
        this.engineName = engineName;
        engine.getPlugins().add(new FluginPlugin());
        initFluginPlugin();
    }

    public void setup(Application application) {
        setEngine(application);
        getEngine().getPlugins().add(new FluginPlugin());
        initFluginPlugin();
    }

    private void setEngine(Application application) {
        FlutterEngine engine = FlutterEngineCache.getInstance().get("flugin_default_engine");
        if (engine == null) {
            engine = new FlutterEngine(application);
            engine.getNavigationChannel().setInitialRoute("/");
            engine.getDartExecutor().executeDartEntrypoint(new DartExecutor.DartEntrypoint(FlutterMain.findAppBundlePath(), "main"));
            FlutterEngineCache.getInstance().put("flugin_default_engine", engine);
        }
    }

    private void initFluginPlugin() {
        try {
            Class<?> clazz = Class.forName("net.mikaelzero.flugin.FluginCompileClass");
            Method m1 = clazz.getDeclaredMethod("init");
            m1.invoke(clazz.newInstance());
        } catch (ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * Gets the FlutterEngine in use.
     *
     * @return the FlutterEngine
     */
    public FlutterEngine getEngine() {
        if (engineName != null && !engineName.isEmpty()) {
            return FlutterEngineCache.getInstance().get(engineName);
        }
        return FlutterBoost.instance().getEngine();
    }


}
