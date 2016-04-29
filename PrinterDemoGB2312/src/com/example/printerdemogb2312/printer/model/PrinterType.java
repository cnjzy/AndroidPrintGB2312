/*
 * File Name: PrinterType.java 
 * History:
 * Created by Administrator on 2016-4-28
 */
package com.example.printerdemogb2312.printer.model;

import com.example.printerdemogb2312.printer.usb.UsbPrinter.ALIGNMENT;

/**
 * 打印数据格式 (Description)
 * 
 * @author zaokafei
 * @version 1.0
 * @date 2016-4-28
 */
public abstract class PrinterType {
    private ALIGNMENT alignment = ALIGNMENT.LEFT;// 对齐方式(默认左对齐)

    public ALIGNMENT getAlignment() {
        return alignment;
    }

    public void setAlignment(ALIGNMENT alignment) {
        this.alignment = alignment;
    }

}