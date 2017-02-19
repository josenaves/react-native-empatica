package com.empatica;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.empatica.empalink.ConnectionNotAllowedException;
import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

class EmpaticaModule extends ReactContextBaseJavaModule implements ActivityEventListener, EmpaDataDelegate, EmpaStatusDelegate {

    private static final String TAG = EmpaticaModule.class.getName();

    private static final int REQUEST_ENABLE_BT = 1;

    private ReactApplicationContext mReactContext;

    private static final boolean DEBUG = true;
    private static final String EMPATICA_KEY_STATUS = "Status";
    private static final String EMPATICA_KEY_EVENTS = "Events";

    private static final String EMPATICA_EVENT_ERROR_BLUETOOTH = "errorbt";
    private static final String EMPATICA_EVENT_ERROR_DEVICE_NOT_FOUND = "errordnf";

    private static final String EMPATICA_EVENT_DISCOVER_DEVICE = "discoverdevice";
    private static final String EMPATICA_EVENT_UPDATE_STATUS = "updatestatus";
    private static final String EMPATICA_EVENT_UPDATE_ACCELERATION = "updateacceleration";
    private static final String EMPATICA_EVENT_UPDATE_BVP = "updatebvp";
    private static final String EMPATICA_EVENT_UPDATE_BATTERY_LEVEL = "updatebatterylevel";
    private static final String EMPATICA_EVENT_UPDATE_GSR = "updategsr";
    private static final String EMPATICA_EVENT_UPDATE_IBI = "updateibi";
    private static final String EMPATICA_EVENT_UPDATE_TEMPERATURE = "updatetemperature";

    private EmpaDeviceManager deviceManager;

    private Map<String, BluetoothDevice> devices = new HashMap<>();

    EmpaticaModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        mReactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "Empatica";
    }

    private Map<String, Object> getStatusConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("CONNECTED", EmpaStatus.CONNECTED);
        constants.put("DISCONNECTED", EmpaStatus.DISCONNECTED);
        constants.put("READY", EmpaStatus.READY);
        return constants;
    }

    private Map<String, Object> getEventsConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("ERROR_BLUETOOTH", EMPATICA_EVENT_ERROR_BLUETOOTH);
        constants.put("UPDATE_STATUS", EMPATICA_EVENT_UPDATE_STATUS);
        constants.put("UPDATE_ACCELERATION", EMPATICA_EVENT_UPDATE_ACCELERATION);
        constants.put("UPDATE_BVP", EMPATICA_EVENT_UPDATE_BVP);
        constants.put("UPDATE_BATTERY_LEVEL", EMPATICA_EVENT_UPDATE_BATTERY_LEVEL);
        constants.put("UPDATE_GSR", EMPATICA_EVENT_UPDATE_GSR);
        constants.put("UPDATE_IBI", EMPATICA_EVENT_UPDATE_IBI);
        constants.put("UPDATE_TEMPERATURE", EMPATICA_EVENT_UPDATE_TEMPERATURE);
        return constants;
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(EMPATICA_KEY_STATUS, getStatusConstants());
        constants.put(EMPATICA_KEY_EVENTS, getEventsConstants());
        return constants;
    }

    @ReactMethod
    public void authenticateWithAPIKey(String apiKey) {
        deviceManager = new EmpaDeviceManager(getReactApplicationContext(), this, this);
        deviceManager.authenticateWithAPIKey(apiKey);
    }

    @ReactMethod
    public void startScanning() {
        deviceManager.startScanning();
    }

    @ReactMethod
    public void stopScanning() {
        deviceManager.stopScanning();
    }

    @ReactMethod
    public void connectDevice(String deviceName) {
        try {
            BluetoothDevice device = devices.get(deviceName);
            deviceManager.connectDevice(device);
        } catch (ConnectionNotAllowedException e) {
            sendEvent(EMPATICA_EVENT_ERROR_DEVICE_NOT_FOUND, null);
            e.printStackTrace();
        }
    }

    /**
     * Send event to javascript
     * @param eventName Name of the event
     * @param params Additional params
     */
    private void sendEvent(String eventName, @Nullable WritableMap params) {
        if (mReactContext.hasActiveCatalystInstance()) {
            if (DEBUG) Log.d(TAG, "Sending event: " + eventName);
            mReactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
        }
    }

    @Override
    public void didDiscoverDevice(BluetoothDevice bluetoothDevice, String deviceName, int rssi, boolean allowed) {
        // Check if the discovered device can be used with your API key. If allowed is always false,
        // the device is not linked with your API key. Please check your developer area at
        // https://www.empatica.com/connect/developer.php
        if (allowed) {
            devices.put(bluetoothDevice.getName(), bluetoothDevice);

            WritableMap device = Arguments.createMap();
            device.putString("name", bluetoothDevice.getName());
            device.putString("address", bluetoothDevice.getAddress());
            device.putInt("hashCode", bluetoothDevice.hashCode());

            WritableMap params = Arguments.createMap();
            params.putMap("device", device);
            params.putInt("rssi", rssi);
            sendEvent(EMPATICA_EVENT_DISCOVER_DEVICE, params);
        }
    }

    @Override
    public void didRequestEnableBluetooth() {
        // Request the user to enable Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        try {
            getCurrentActivity().startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } catch (NullPointerException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void didUpdateStatus(EmpaStatus status) {
        WritableMap params = Arguments.createMap();
        params.putString("status", status.name());
        sendEvent(EMPATICA_EVENT_UPDATE_STATUS, params);
    }

    @Override
    public void didUpdateSensorStatus(EmpaSensorStatus empaSensorStatus, EmpaSensorType empaSensorType) {

    }

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double timestamp) {
        WritableMap params = Arguments.createMap();
        params.putInt("x", x);
        params.putInt("y", y);
        params.putInt("z", z);
        sendEvent(EMPATICA_EVENT_UPDATE_ACCELERATION, params);
    }

    @Override
    public void didReceiveBVP(float bvp, double timestamp) {
        WritableMap params = Arguments.createMap();
        params.putDouble("bvp", bvp);
        sendEvent(EMPATICA_EVENT_UPDATE_BVP, params);
    }

    @Override
    public void didReceiveBatteryLevel(float battery, double timestamp) {
        WritableMap params = Arguments.createMap();
        params.putDouble("batteryLevel", battery);
        sendEvent(EMPATICA_EVENT_UPDATE_BATTERY_LEVEL, params);
    }

    @Override
    public void didReceiveGSR(float gsr, double timestamp) {
        WritableMap params = Arguments.createMap();
        params.putDouble("gsr", gsr);
        sendEvent(EMPATICA_EVENT_UPDATE_GSR, params);
    }

    @Override
    public void didReceiveIBI(float ibi, double timestamp) {
        WritableMap params = Arguments.createMap();
        params.putDouble("ibi", ibi);
        sendEvent(EMPATICA_EVENT_UPDATE_IBI, params);
    }

    @Override
    public void didReceiveTemperature(float temp, double timestamp) {
        WritableMap params = Arguments.createMap();
        params.putDouble("temperature", temp);
        sendEvent(EMPATICA_EVENT_UPDATE_TEMPERATURE, params);
    }

    /**
     * Called when host (activity/service) receives an {@link Activity#onActivityResult} call.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        // The user chose not to enable Bluetooth
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            sendEvent(EMPATICA_EVENT_ERROR_BLUETOOTH, null);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (DEBUG) Log.d(TAG, "On new intent");
    }
}