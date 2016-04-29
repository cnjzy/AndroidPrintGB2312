/*
 * File Name: PrinterType.java 
 * History:
 * Created by Administrator on 2016-4-28
 */
package com.example.printerdemogb2312.printer.model;

import com.example.printerdemogb2312.printer.usb.UsbPrinter.ALIGNMENT;

/**
 * ��ӡ���ݸ�ʽ (Description)
 * 
 * @author zaokafei
 * @version 1.0
 * @date 2016-4-28
 */
public abstract class PrinterType {
    private ALIGNMENT alignment = ALIGNMENT.LEFT;// ���뷽ʽ(Ĭ�������)

    public ALIGNMENT getAlignment() {
        return alignment;
    }

    public void setAlignment(ALIGNMENT alignment) {
        this.alignment = alignment;
    }

}