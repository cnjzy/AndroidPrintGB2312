/*
 * File Name: PrinterInterface.java 
 * History:
 * Created by Administrator on 2016-4-28
 */
package com.example.printerdemogb2312.printer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.graphics.Bitmap;

import com.example.printerdemogb2312.printer.model.ImageType;
import com.example.printerdemogb2312.printer.model.StringType;
import com.example.printerdemogb2312.printer.usb.UsbPrinter.ALIGNMENT;
import com.example.printerdemogb2312.printer.usb.UsbPrinter.CHARACTERSIZE;
import com.example.printerdemogb2312.printer.usb.UsbPrinter.FONT;

/**
 * 打印接口 (Description)
 * 
 * @author zaokafei
 * @version 1.0
 * @date 2016-4-28
 */
public interface PrinterInterface {

    /**
     * 打印纯文字信息
     * 
     * @param string
     *            内容
     * @param font
     *            字体
     * @param alignment
     *            对其方式
     * @param bold
     *            字行
     * @param underlined
     *            下划线
     * @param doubleHeight
     * @param doubleWidth
     */
    public void printString(String string, ALIGNMENT alignment, FONT font, Boolean bold, Boolean underlined,
            CHARACTERSIZE characterSize, boolean isCut) throws IOException;// 打印纯文字

    public void printString(StringType stringType, boolean isCut) throws IOException;

    public void printImage(File file, ALIGNMENT alignment, boolean isCut) throws IOException;// 打印图片

    public void printImage(ImageType fileType, boolean isCut) throws IOException;// 打印图片

    public void printImage(Bitmap bitmap, ALIGNMENT alignment, boolean isCut) throws IOException;// 打印图片

    public void printStringImage(String string, ALIGNMENT alignment, FONT font, Boolean bold, Boolean underlined,
            CHARACTERSIZE characterSize, ALIGNMENT fileAlignment, File fiel, boolean isCut) throws IOException;// 打印文字和图片

    public void printStringList(List<StringType> data, boolean isCut) throws IOException;// 批量打印文字

    public void printImageList(List<ImageType> data, boolean isCut) throws IOException;// 批量打印图片

    public void printStringAndImageList(List<StringType> data1, List<ImageType> data2, boolean isCut)
            throws IOException;// 批量打印文字和图片
}
