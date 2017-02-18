package com.empatica;

import android.widget.Toast;

import com.empatica.empalink.config.EmpaStatus;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.Map;

public class EmpaticaModule extends ReactContextBaseJavaModule {

    private static final String EMPATICA_STATUS_ENUM = "Status";

    public ToastModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "Empatica";
    }

    @Override
    private Map<String, Object> getStatusConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("CONNECTED", EmpaStatus.CONNECTED);
        constants.put("DISCONNECTED", EmpaStatus.DISCONNECTED);
        constants.put("READY", EmpaStatus.READY);
        return constants;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(EMPATICA_STATUS_ENUM, getStatusConstants());
        return constants;
    }

}