import React from 'react';
import { View, Text } from 'react-native';
import Empatica from 'react-native-empatica';

const API_KEY = '<api-key-goes-here>';

export default class App extends React.Component {

    state = {
        status: '',
        acceleration: { x: null, y: null, z: null },
        bvp: null,
        batteryLevel: null,
        gsr: null,
        ibi: null,
        temperature: null
    };

    componentWillMount() {
        Empatica.setOnDiscoverDevice(this.onDiscoverDevice.bind(this));
        Empatica.setOnUpdateStatus(this.onUpdateStatus.bind(this));
        Empatica.setOnUpdateAcceleration(this.onUpdateAcceleration.bind(this));
        Empatica.setOnUpdateBVP(this.onUpdateBVP.bind(this));
        Empatica.setOnUpdateBatteryLevel(this.onUpdateBatteryLevel.bind(this));
        Empatica.setOnUpdateGSR(this.onUpdateGSR.bind(this));
        Empatica.setOnUpdateIBI(this.onUpdateIBI.bind(this));
        Empatica.setOnUpdateTemperature(this.onUpdateTemperature.bind(this));

        Empatica.authenticateWithAPIKey(API_KEY);
    }

    // Only triggers when the device is allowed (linked to your API key)
    onDiscoverDevice(bluetoothDevice, deviceName, rssi) {
        Empatica.connectDevice(bluetoothDevice);
        Empatica.stopScanning();
    }

    onUpdateStatus(status) {
        this.setState({ status });
        switch (status) {
            
            case Empatica.Status.READY:
                Empatica.startScanning();
                break;
            
            case Empatica.Status.CONNECTED:
                break;

            case Empatica.Status.DISCONNECTED:
                break;
        }
    }

    onUpdateAcceleration(x, y, z, timestamp) {
        const acceleration = { x, y, z };
        this.setState({ acceleration });
    }

    onUpdateBVP(bvp, timestamp) {
        this.setState({ bvp });
    }

    onUpdateBatteryLevel(batteryLevel, timestamp) {
        this.setState({ batteryLevel });
    }

    onUpdateGSR(gsr, timestamp) {
        this.setState({ gsr });
    }

    onUpdateIBI(ibi, timestamp) {
        this.setState({ ibi });
    }

    onUpdateTemperature(temperature, timestamp) {
        this.setState({ temperature });
    }

    renderContent() {
        const { status, acceleration, bvp, batteryLevel, gsr, ibi, temperature } = this.state;
        if (!status) return;
        return (
            <View>
                <Text>Status: {status}</Text>
                <Text>Acceleration: {JSON.stringify(acceleration)}</Text>
                <Text>BVP: {bvp}</Text>
                <Text>GSR: {gsr}</Text>
                <Text>IBI: {ibi}</Text>
                <Text>Temperature: {temperature}</Text>
                <Text>Battery level: {batteryLevel}</Text>
            </View>
        );
    }

    render() {
        return (
            <View>
                <Text>Hello Empatica!</Text>
            </View>
        );
    }
}