/*
 * File Name: FactoryPrinter.java 
 * History:
 * Created by Administrator on 2016-4-28
 */
package com.example.printerdemogb2312.printer;

import java.io.IOException;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.example.printerdemogb2312.printer.usb.UsbPrinter;

/**
 * ¥Ú”°π§≥ß (Description)
 * 
 * @author zaokafei
 * @version 1.0
 * @date 2016-4-28
 */
public class FactoryPrinter {
    private Context mContext;
    private UsbPrinter mUsbPrinter;
    private UsbDevice mUsbDevice;

    public FactoryPrinter(Context con, UsbDevice device) {
        mContext = con;
        mUsbDevice = device;
    }

    public UsbPrinter getUSBSingleStance() throws IOException {
        if (mUsbPrinter == null) {
            synchronized (FactoryPrinter.class) {
                if (mUsbPrinter == null) {
                    mUsbPrinter = new UsbPrinter(mContext, mUsbDevice);
                }
            }
        }
        return mUsbPrinter;
    }

}
