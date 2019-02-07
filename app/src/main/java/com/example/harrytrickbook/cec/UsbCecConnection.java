package com.example.harrytrickbook.cec;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class allows to send HDMI CEC commands through Pulse-Eight's USB-CEC adapter.
 * Command syntax is inspired by libCEC (https://github.com/Pulse-Eight/libcec).
 *
 * It requires UsbSerial library (https://github.com/felHR85/UsbSerial)
 *
 * IMPORTANT: the provided {@link UsbDevice} must have been granted permission.
 *
 */
public class UsbCecConnection implements UsbSerialInterface.UsbReadCallback {

    private static final String LOG_TAG = UsbCecConnection.class.getSimpleName();

    private static final byte MSG_START = (byte)255, MSG_END = (byte)254;

    private enum MsgCode {
        MSGCODE_NOTHING,
        MSGCODE_PING,
        MSGCODE_TIMEOUT_ERROR,
        MSGCODE_HIGH_ERROR,
        MSGCODE_LOW_ERROR,
        MSGCODE_FRAME_START,
        MSGCODE_FRAME_DATA,
        MSGCODE_RECEIVE_FAILED,
        MSGCODE_COMMAND_ACCEPTED,
        MSGCODE_COMMAND_REJECTED,
        MSGCODE_SET_ACK_MASK,
        MSGCODE_TRANSMIT,
        MSGCODE_TRANSMIT_EOM,
        MSGCODE_TRANSMIT_IDLETIME,
        MSGCODE_TRANSMIT_ACK_POLARITY,
        MSGCODE_TRANSMIT_LINE_TIMEOUT,
        MSGCODE_TRANSMIT_SUCCEEDED,
        MSGCODE_TRANSMIT_FAILED_LINE,
        MSGCODE_TRANSMIT_FAILED_ACK,
        MSGCODE_TRANSMIT_FAILED_TIMEOUT_DATA,
        MSGCODE_TRANSMIT_FAILED_TIMEOUT_LINE,
        MSGCODE_FIRMWARE_VERSION,
        MSGCODE_START_BOOTLOADER,
        MSGCODE_GET_BUILDDATE,
        MSGCODE_SET_CONTROLLED,
        MSGCODE_GET_AUTO_ENABLED,
        MSGCODE_SET_AUTO_ENABLED,
        MSGCODE_GET_DEFAULT_LOGICAL_ADDRESS,
        MSGCODE_SET_DEFAULT_LOGICAL_ADDRESS,
        MSGCODE_GET_LOGICAL_ADDRESS_MASK,
        MSGCODE_SET_LOGICAL_ADDRESS_MASK,
        MSGCODE_GET_PHYSICAL_ADDRESS,
        MSGCODE_SET_PHYSICAL_ADDRESS,
        MSGCODE_GET_DEVICE_TYPE,
        MSGCODE_SET_DEVICE_TYPE,
        MSGCODE_GET_HDMI_VERSION,
        MSGCODE_SET_HDMI_VERSION,
        MSGCODE_GET_OSD_NAME,
        MSGCODE_SET_OSD_NAME,
        MSGCODE_WRITE_EEPROM,
        MSGCODE_GET_ADAPTER_TYPE,
        MSGCODE_SET_ACTIVE_SOURCE,
        A,
        B,
        C,
        D,
        E,
        F,
        G,
        H,
        I,
        J,
        K,
        L,
        M,
        N,
        O,
        P,
        Q,
        R,
        S,
        T,
        U,
        V,
        X,
        Y,
        Z,
        ZZ,
        ZZZ,
        ZZZZ,
        MSGCODE_UNKNOWN
    }

    private final UsbSerialDevice usbSerialDevice;

    private final List<Byte> currentPacket = new ArrayList<>();

    public UsbCecConnection(UsbDevice device, UsbDeviceConnection usbConnection) {
        usbSerialDevice = UsbSerialDevice.createUsbSerialDevice(device, usbConnection);
        usbSerialDevice.open();
        usbSerialDevice.read(this);
        String port = usbSerialDevice.getPortName();
        Log.d(LOG_TAG, "port: " + port);

        // Optional, just attempt to get some information
        sendCommand(MsgCode.MSGCODE_FIRMWARE_VERSION);
        sendCommand(MsgCode.MSGCODE_SET_CONTROLLED, 1);
        sendCommand(MsgCode.MSGCODE_GET_OSD_NAME);
    }

    private int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if(digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: "+ hexChar);
        }
        return digit;
    }

    public byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }


    public void switchTvOn() {

        sendCommand(MsgCode.MSGCODE_SET_CONTROLLED, 1);
        sendCommand(MsgCode.MSGCODE_TRANSMIT_ACK_POLARITY, 0);
        sendCommand(MsgCode.MSGCODE_TRANSMIT, 16);
        sendCommand(MsgCode.MSGCODE_TRANSMIT_EOM, 4);

        if(true == true)return;


        byte[] data1 = new byte[1];
        data1[0] = (byte)240;
        sendData(data1);

        byte[] data2 = new byte[2];
        data2[0] = (byte)240;
        data2[1] = (byte)140;
        sendData(data2);

//        byte[] data3 = new byte[6];
//        data3[0] = hexToByte("0f");
//        data3[1] = hexToByte("87");
//        data3[3] = hexToByte("00");
//        data3[4] = hexToByte("80");
//        data3[5] = hexToByte("45");
//        data3[data3.length-1] = (byte)254;
//        sendData(data3);

        byte[] data4 = new byte[1];
        data4[0] = (byte)68;
        sendData(data4);
        sendData(data4);

        byte[] data5 = new byte[5];
        data5[0] = (byte)79;
        data5[1] = (byte)132;
        data5[2] = (byte)16;
        data5[3] = (byte)0;
        data5[4] = (byte)4;
        sendData(data5);

        byte[] data6 = new byte[11];
        data6[0] = (byte)64;
        data6[1] = (byte)71;
        data6[2] = (byte)67;
        data6[3] = (byte)69;
        data6[4] = (byte)67;
        data6[5] = (byte)84;
        data6[6] = (byte)101;
        data6[7] = (byte)115;
        data6[8] = (byte)116;
        data6[9] = (byte)101;
        data6[10] = (byte)114;
        sendData(data6);

        byte[] data7 = new byte[2];
        data7[0] = (byte)64;
        data7[1] = (byte)143;
        sendData(data7);

        byte[] data8 = new byte[5];
        data8[0] = (byte)79;
        data8[1] = (byte)135;
        data8[2] = (byte)0;
        data8[3] = (byte)128;
        data8[4] = (byte)69;
        sendData(data8);

        byte[] data9 = new byte[4];
        data9[0] = (byte)79;
        data9[1] = (byte)130;
        data9[2] = (byte)48;
        data9[3] = (byte)0;
        sendData(data9);




//        sendCommand(MsgCode.MSGCODE_SET_CONTROLLED, 1);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_ACK_POLARITY, 0);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT, 16);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_EOM, 4);
    }

    public void switchTvOff() {

        activeSource();

        sendCommand(MsgCode.MSGCODE_SET_CONTROLLED, 1);
        sendCommand(MsgCode.MSGCODE_TRANSMIT_ACK_POLARITY, 0);
        sendCommand(MsgCode.MSGCODE_TRANSMIT, 16);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_EOM, 54);

        byte[] data = new byte[5];
        data[0] = (byte)255;
        data[1] = (byte)12;
        data[2] = (byte)68;
        data[3] = (byte)65;
        data[data.length-1] = (byte)254;
        sendData(data);

        if(true == true)return;

//        sendCommand(MsgCode.MSGCODE_SET_CONTROLLED, 1);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_ACK_POLARITY, 0);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT, 16);


//        byte[] data1 = new byte[13];
//        data1[0] = (byte)255;
//        data1[1] = (byte)12;/* 0C */
//        data1[2] = (byte)69;/* 36 */
//        data1[3] = (byte)68;/* 36 */
//        data1[4] = (byte)65;/* 36 */
//        data1[5] = (byte)80;/* 36 */
//        data1[6] = (byte)122;/* 36 */
//        data1[7] = (byte)11;/* 36 */
//        data1[8] = (byte)69;/* 36 */
//        data1[9] = (byte)69;/* 36 */
//        data1[10] = (byte)80;/* 36 */
//        data1[11] = (byte)0;/* 36 */
//        data1[12] = (byte)0;/* 36 */
//        data1[data1.length-1] = (byte)254;
//        sendData(data1);
//
//        if(true == true)return;

        byte[] dataX = new byte[4];
        dataX[0] = (byte)255;
        dataX[1] = (byte)24;/* 18 */
        dataX[2] = (byte)1;/* 01 */
        dataX[dataX.length-1] = (byte)254;
        sendData(dataX);

        /* 18:01 */

        byte[] data0 = new byte[4];
        data0[0] = (byte)255;
        data0[1] = (byte)14;/* 0E */
        data0[2] = (byte)0;/* 00 */
        data0[data0.length-1] = (byte)254;
        sendData(data0);

        /* 0E:00 */
//
//        byte[] data = new byte[4];
//        data[0] = (byte)255;
//        data[1] = (byte)11;/* 0B */
//        data[2] = (byte)16;/* 10 */
//        data[data.length-1] = (byte)254;
//        sendData(data);

        /* 0B:10 */

        byte[] dataP = new byte[4];
        dataP[0] = (byte)255;
        dataP[1] = (byte)11;/* 0C */
        dataP[2] = (byte)68;/* 44 */
        dataP[dataP.length-1] = (byte)254;
        sendData(dataP);

        byte[] dataV = new byte[4];
        dataV[0] = (byte)255;
        dataV[1] = (byte)12;/* 0C */
        dataV[2] = (byte)29;/* 41 */
        dataV[dataV.length-1] = (byte)254;
        sendData(dataV);

//        byte[] dataD = new byte[4];
//        dataD[0] = (byte)255;
//        dataD[1] = (byte)12;/* 0C */
//        dataD[2] = (byte)69;/* 45 */
//        dataD[dataD.length-1] = (byte)254;
//        sendData(dataD);

        /* 0C:36 */
    }

    public void volumeUp() {

        usbSerialDevice.write(hexStringToByteArray("454441"));
//        sendCommand((byte) 11 ,hexStringToByteArray("454441"));
//        sendCommand((byte) 6 ,hexStringToByteArray("44"));
//        sendCommand((byte) 11 ,hexStringToByteArray("41"));
//        sendCommand((byte) 5 ,hexStringToByteArray("00"));
//        sendCommand((byte) 6 ,hexStringToByteArray("00"));


//        sendCommand(MsgCode.MSGCODE_SET_CONTROLLED, 1);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_ACK_POLARITY, 0);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT, 16);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_EOM, 24);
    }

    public void volumeDown() {

        sendCommand((byte) 24 ,hexStringToByteArray("01"));
        sendCommand((byte) 14 ,hexStringToByteArray("00"));
        sendCommand((byte) 5 ,hexStringToByteArray("45"));
        sendCommand((byte) 6 ,hexStringToByteArray("44"));
        sendCommand((byte) -122 ,hexStringToByteArray("41"));
        sendCommand((byte) 5 ,hexStringToByteArray("50"));
        sendCommand((byte) 6,hexStringToByteArray("7A"));
        sendCommand((byte) -122 ,hexStringToByteArray("0B"));
        sendCommand((byte) 5 ,hexStringToByteArray("45"));
        sendCommand((byte) -122 ,hexStringToByteArray("45"));
        sendCommand((byte) 5 ,hexStringToByteArray("50"));
        sendCommand((byte) 6 ,hexStringToByteArray("00"));
        sendCommand((byte) -122 ,hexStringToByteArray("00"));

//        sendCommand((byte) 5 ,hexStringToByteArray("45"));
//        sendCommand((byte) 6 ,hexStringToByteArray("44"));
//        sendCommand((byte) -122 ,hexStringToByteArray("42"));

//        sendCommand(MsgCode.MSGCODE_SET_CONTROLLED, 1);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_ACK_POLARITY, 0);
//        sendCommand((byte) 5 ,hexStringToByteArray("45"));
//        sendCommand((byte) 6 ,hexStringToByteArray("44"));
//        sendCommand((byte) -122 ,hexStringToByteArray("41"));
//        sendCommand((byte) 5 ,hexStringToByteArray("50"));
//        sendCommand((byte) 6 ,hexStringToByteArray("7A"));
//        sendCommand((byte) -122 ,hexStringToByteArray("22"));
//        sendCommand((byte) 5 ,hexStringToByteArray("45"));
//        sendCommand((byte) 6 ,hexStringToByteArray("44"));
//        sendCommand((byte) -122 ,hexStringToByteArray("41"));
//        sendCommand((byte) 5 ,hexStringToByteArray("50"));
//        sendCommand((byte) 6 ,hexStringToByteArray("7A"));
//        sendCommand((byte) -122 ,hexStringToByteArray("26"));
//        sendCommand((byte) 5 ,hexStringToByteArray("45"));



//        sendCommand(MsgCode.MSGCODE_SET_CONTROLLED, 1);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_ACK_POLARITY, 0);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT, 69);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_EOM, hexStringToByteArray("4442"));

//        sendCommand(MsgCode.MSGCODE_SET_ACTIVE_SOURCE, 1);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT, 69);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT, 68);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT, 66);

//        String example = "454442";
//        activeSource();
//        sendCommand(MsgCode.ZZZZ, hexStringToByteArray("4442"));
//        sendCommand(MsgCode.MSGCODE_FRAME_START, hexStringToByteArray("c1"));

//        sendCommand(MsgCode.MSGCODE_SET_CONTROLLED, 1);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_ACK_POLARITY, 0);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT, 16);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_EOM, 54);

//        sendCommand(MsgCode.MSGCODE_SET_CONTROLLED, 1);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT_ACK_POLARITY, 0);
//        sendCommand(MsgCode.MSGCODE_TRANSMIT, 44);

//        String example = "45:44:42";
//        String example = "0x2D:0x2C:0x2A";
//        switchTvOn();
//        String example = "4442";
//        byte[] bytes = hexStringToByteArray(example);
//        sendCommand((byte) 69 ,hexStringToByteArray("4442"));
//
//        String example2 = "05c1";
//        byte[] bytes2 = hexStringToByteArray(example2);
//        sendData(bytes2);

//        sendCommand(MsgCode.MSGCODE_TRANSMIT_EOM, 42);
    }

    public void activeSource() {
        sendCommand(MsgCode.MSGCODE_SET_CONTROLLED, 1);
        // marking the adapter as active source
        sendCommand(MsgCode.MSGCODE_SET_ACTIVE_SOURCE, 1);
        // powering on 'TV'
        sendCommand(MsgCode.MSGCODE_TRANSMIT_ACK_POLARITY, 0);
        sendCommand(MsgCode.MSGCODE_TRANSMIT, 16);
        sendCommand(MsgCode.MSGCODE_TRANSMIT_EOM, 4);
        // active source
        sendCommand(MsgCode.MSGCODE_TRANSMIT_ACK_POLARITY, 1);
        sendCommand(MsgCode.MSGCODE_TRANSMIT, 31);
        sendCommand(MsgCode.MSGCODE_TRANSMIT, 130);
        sendCommand(MsgCode.MSGCODE_TRANSMIT, 16);
        sendCommand(MsgCode.MSGCODE_TRANSMIT_EOM, 0);
    }

    @Override
    public void onReceivedData(byte[] bytes) {
        Log.v(LOG_TAG, "onReceivedData: " + Arrays.toString(bytes));
        for(byte b : bytes) {
            currentPacket.add(b);
            if(b == MSG_END) {
                byte[] packetBytes = new byte[currentPacket.size()];
                for(int i=0; i<packetBytes.length; i++) { packetBytes[i] = currentPacket.get(i); }
                onReceivedPacket(packetBytes);
                currentPacket.clear();
            }
        }
    }

    private void onReceivedPacket(byte[] bytes) {
        Log.d(LOG_TAG, "onReceivedPacket: " + Arrays.toString(bytes));
        if(bytes.length <= 2) {
            Log.w(LOG_TAG, "Invalid response: " + Arrays.toString(bytes));
        }
        else {
            MsgCode msgCode = getMsgCode(bytes[1]);
            byte[] params = new byte[bytes.length-3];
            System.arraycopy(bytes, 2, params, 0, params.length);
            onReceivedPacket(msgCode, params);
        }
    }

    private void onReceivedPacket(MsgCode msgCode, byte[] params) {
//        Log.d(LOG_TAG, "onReceivedPacket: " + msgCode + ", " + Arrays.toString(params));
        switch(msgCode) {
            case MSGCODE_COMMAND_ACCEPTED:
                Log.d(LOG_TAG, "ACCEPTED " + getMsgCode(params[0]));
                break;
            case MSGCODE_COMMAND_REJECTED:
                Log.w(LOG_TAG, "REJECTED: " + getMsgCode(params[0]));
                break;
            case MSGCODE_FIRMWARE_VERSION:
                int firmwareVersion = 255 * (params[0] & 0xFF) + (params[1] & 0xFF);
                Log.i(LOG_TAG, "Firmware version is " + firmwareVersion);
                break;
            case MSGCODE_GET_OSD_NAME:
                String osdName = new String(params);
                Log.i(LOG_TAG, "OSD name is '" + osdName + "'");
                break;
        }
    }

    private void sendCommand(MsgCode code) {
        sendCommand(code, new byte[0]);
    }

    private void sendCommand(MsgCode code, int param) {
        sendCommand(code, new byte[] { (byte) param });
    }

    private void sendCommand(MsgCode code, byte[] params) {
        Log.i(LOG_TAG, "sendCommand: " + code + ", " + Arrays.toString(params));
        sendCommand((byte) code.ordinal(), params);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private void sendCommand(byte command, byte[] params) {
        Log.d(LOG_TAG, "sendCommand: " + command + ", " + Arrays.toString(params));
        byte[] data = new byte[3 + params.length];
        data[0] = MSG_START;
        data[1] = command;
        System.arraycopy(params, 0, data, 2, params.length);
        data[data.length-1] = MSG_END;
        sendData(data);
    }

    private void sendData(byte[] data) {
        Log.v(LOG_TAG, "sendData: " + Arrays.toString(data));
        usbSerialDevice.write(data);
    }

    private static MsgCode getMsgCode(byte b) {
        int i = b & 0xFF; // unsigned int
        if(i < MsgCode.values().length) {
            return MsgCode.values()[i];
        }
        else {
            return MsgCode.MSGCODE_UNKNOWN;
        }
    }
}

