import { NativeModules, DeviceEventEmitter }  from 'react-native';
const { Empatica } = NativeModules;
const { Events } = Empatica;

Empatica.setOnDiscoverDevice = (handler) => {
    if (handler) DeviceEventEmitter.addListener(Events.DISCOVER_DEVICE, handler);
};
Empatica.setOnUpdateStatus = (handler) => {
    if (handler) DeviceEventEmitter.addListener(Events.UPDATE_STATUS, handler);
};
Empatica.setOnUpdateAcceleration = (handler) => {
    if (handler) DeviceEventEmitter.addListener(Events.UPDATE_ACCELERATION, handler);
};
Empatica.setOnUpdateBVP = (handler) => {
    if (handler) DeviceEventEmitter.addListener(Events.UPDATE_BVP, handler);
};
Empatica.setOnUpdateBatteryLevel = (handler) => {
    if (handler) DeviceEventEmitter.addListener(Events.UPDATE_BATTERY_LEVEL, handler);
};
Empatica.setOnUpdateGSR = (handler) => {
    if (handler) DeviceEventEmitter.addListener(Events.UPDATE_GSR, handler);
};
Empatica.setOnUpdateIBI = (handler) => {
    if (handler) DeviceEventEmitter.addListener(Events.UPDATE_IBI, handler);
};
Empatica.setOnUpdateTemperature = (handler) => {
    if (handler) DeviceEventEmitter.addListener(Events.UPDATE_TEMPERATURE, handler);
};
Empatica.setOnError = (handler) => {
    if (handler) DeviceEventEmitter.addListener(Events.UPDATE_ERROR_BLUETOOTH, handler);
};

module.exports = Empatica;