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
 * ��ӡ�ӿ� (Description)
 * 
 * @author zaokafei
 * @version 1.0
 * @date 2016-4-28
 */
public interface PrinterInterface {

    /**
     * ��ӡ��������Ϣ
     * 
     * @param string
     *            ����
     * @param font
     *            ����
     * @param alignment
     *            ���䷽ʽ
     * @param bold
     *            ����
     * @param underlined
     *            �»���
     * @param doubleHeight
     * @param doubleWidth
     */
    public void printString(String string, ALIGNMENT alignment, FONT font, Boolean bold, Boolean underlined,
            CHARACTERSIZE characterSize, boolean isCut) throws IOException;// ��ӡ������

    public void printString(StringType stringType, boolean isCut) throws IOException;

    public void printImage(File file, ALIGNMENT alignment, boolean isCut) throws IOException;// ��ӡͼƬ

    public void printImage(ImageType fileType, boolean isCut) throws IOException;// ��ӡͼƬ

    public void printImage(Bitmap bitmap, ALIGNMENT alignment, boolean isCut) throws IOException;// ��ӡͼƬ

    public void printStringImage(String string, ALIGNMENT alignment, FONT font, Boolean bold, Boolean underlined,
            CHARACTERSIZE characterSize, ALIGNMENT fileAlignment, File fiel, boolean isCut) throws IOException;// ��ӡ���ֺ�ͼƬ

    public void printStringList(List<StringType> data, boolean isCut) throws IOException;// ������ӡ����

    public void printImageList(List<ImageType> data, boolean isCut) throws IOException;// ������ӡͼƬ

    public void printStringAndImageList(List<StringType> data1, List<ImageType> data2, boolean isCut)
            throws IOException;// ������ӡ���ֺ�ͼƬ
}
