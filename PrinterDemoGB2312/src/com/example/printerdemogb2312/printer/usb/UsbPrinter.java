/*
 * File Name: UsbPrinter.java 
 * History:
 * Created by Administrator on 2016-4-28
 */
package com.example.printerdemogb2312.printer.usb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.example.printerdemogb2312.printer.PrinterEquipment;
import com.example.printerdemogb2312.printer.PrinterInterface;
import com.example.printerdemogb2312.printer.model.BitImageUtils;
import com.example.printerdemogb2312.printer.model.ImageType;
import com.example.printerdemogb2312.printer.model.StringType;

/**
 * USB 打印设备 (Description)
 * 
 * @author zaokafei
 * @version 1.0
 * @date 2016-4-28
 */
public class UsbPrinter extends PrinterEquipment implements PrinterInterface {
    private static final String TAG = "UsbPrinter";
    private String command = "";

    // 基础配置文件
    private final UsbDevice mDevice;
    private final UsbDeviceConnection mConnection;
    private final UsbInterface mInterface;
    private final UsbEndpoint mEndpoint;

    private static final int TRANSFER_TIMEOUT = 200;

    // 定义信息
    public static enum ALIGNMENT {
        LEFT, CENTER, RIGHT
    }

    public static enum FONT {
        FONT_A, FONT_B
    }

    public static enum CHARACTERSIZE {
        Standard, DoubleWidth, DoubleHeight, DoubleWH
    }

    byte[] CMD_INIT = { 0x1B, 0x40 };
    byte[] CMD_UPLOAD_IMAGE = { 0x1D, 0x2A, 0, 0 };
    byte[] CMD_PRINT_IMAGE = { 0x1D, 0x2F, 0 };
    byte[] CMD_CUT = { 0x1D, 0x56, 0x01 };
    byte[] ESC_LINE_SPANCE = { 0x1B, 0x33, 0 };// 行间距

    public UsbPrinter(Context context, UsbDevice device) throws IOException {
        UsbInterface iface = null;
        UsbEndpoint epout = null;

        for (int i = 0; i < device.getInterfaceCount(); i++) {
            iface = device.getInterface(i);
            if (iface == null)
                throw new IOException("failed to get interface " + i);

            int epcount = iface.getEndpointCount();
            for (int j = 0; j < epcount; j++) {
                UsbEndpoint ep = iface.getEndpoint(j);
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    epout = ep;
                    break;
                }
            }

            if (epout != null)
                break;
        }

        if (epout == null) {
            throw new IOException("no output endpoint.");
        }

        mDevice = device;
        mInterface = iface;
        mEndpoint = epout;

        UsbManager usbman = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mConnection = usbman.openDevice(device);

        if (mConnection == null) {
            throw new IOException("failed to open usb device.");
        }

        mConnection.claimInterface(mInterface, true);
    }

    public void write(byte[] data) throws IOException {
        if (mConnection.bulkTransfer(mEndpoint, data, data.length, TRANSFER_TIMEOUT) != data.length)
            throw new IOException("failed to write usb endpoint.");
    }

    public void close() {
        mConnection.releaseInterface(mInterface);
        mConnection.close();
    }

    /**
     * 切纸
     * 
     * @throws IOException
     */
    public void cutPaper() throws IOException {
        String command = "GS V 65 20";
        byte[] b = EscposUtil.convertEscposToBinary(command);
        if (b != null)
            write(b);
    }

    // 初始化打印机
    private void initPrinter() throws IOException {
        write(CMD_INIT);
    }

    /**
     * 打印行间距
     * 
     * @param b
     * @throws IOException
     */
    private void setLineSpance(byte b) throws IOException {
        if (b <= 0) {
            b = 50;
        }
        ESC_LINE_SPANCE[2] = b;
        write(ESC_LINE_SPANCE);
    }

    // 对齐方式设定
    private void selectAlignment(ALIGNMENT alignment) throws IOException {
        if (alignment == null) {
            return;
        }
        int iAlignment = 0;
        switch (alignment) {
        case LEFT:
            iAlignment = 0;
            break;
        case CENTER:
            iAlignment = 1;
            break;
        case RIGHT:
            iAlignment = 2;
            break;
        default:
            iAlignment = 0;
        }
        byte[] b;
        command = String.format("ESC a %d", iAlignment);
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null)
            write(b);
    }

    // 选择字体
    private void selectFont(FONT font) throws IOException {
        if (font == null) {
            return;
        }
        switch (font) {
        case FONT_A:
            command = "ESC M 0";
            break;
        case FONT_B:
            command = "ESC M 1";
            break;
        default:
        }
        byte[] b;
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null) {
            write(b);
        }
    }

    // 字体粗体
    private void selectFontBold(Boolean bold) throws IOException {
        if (bold == null) {
            return;
        }
        if (bold.booleanValue()) {
            command = "ESC E 1";
        } else {
            command = "ESC E 0";
        }
        byte[] b;
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null)
            write(b);
    }

    // 字体下划线
    private void selectFontUndler(Boolean underlined) throws IOException {
        if (underlined == null) {
            return;
        }
        if (underlined.booleanValue()) {
            command = "ESC - 49";
        } else {
            command = "ESC - 48";
        }
        byte[] b;
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null)
            write(b);
    }

    /**
     * 选择字体大小
     * 
     * @param characterSize
     * @throws IOException
     */
    private void selectCharsetSize(CHARACTERSIZE characterSize) throws IOException {
        if (characterSize == null) {
            return;
        }
        int options = 0;
        switch (characterSize) {
        case Standard:
            break;
        case DoubleWidth:
            options |= 16;
            break;
        case DoubleHeight:
            options |= 1;
            break;
        case DoubleWH:
            options |= 1;
            options |= 16;
            break;
        default:
        }
        byte[] b;
        command = String.format("GS ! %d", options);
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null) {
            write(b);
        }
    }

    // 字符间距
    private void selectFondSpace(int i) throws IOException {
        if (i > 255) {
            i = 255;
        }
        if (i < 0) {
            i = 0;
        }
        byte[] b;
        command = String.format("ESC SP %d", i);
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null) {
            write(b);
        }
    }

    // 设置行间距
    private void selectLineSpaceDefault() throws IOException {
        byte[] b;
        command = "ESC 3";
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null) {
            write(b);
        }
    }

    // 设置行间距
    private void selectLineSpace(int space) throws IOException {
        if (space > 255) {
            space = 255;
        }
        if (space < 0) {
            space = 0;
        }
        byte[] b;
        command = String.format("ESC 3 %d", space);
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null) {
            write(b);
        }
    }

    // 设置左行间距
    private void setLeftSpace(int space) throws IOException {
        if (space > 255) {
            space = 255;
        }
        if (space < 0) {
            space = 0;
        }
        byte[] b;
        command = String.format("ESC 3 %d", space);
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null) {
            write(b);
        }
    }

    @Override
    public void printString(String string, ALIGNMENT alignment, FONT font, Boolean bold, Boolean underlined,
            CHARACTERSIZE characterSize, boolean isCut) throws IOException {
        byte[] b;
        this.selectAlignment(alignment);
        this.selectFont(font);
        this.selectFontBold(bold);
        this.selectFontUndler(underlined);
        this.selectCharsetSize(characterSize);
        command = String.format("'%s' LF", string);
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null) {
            write(b);
        }
        if (isCut) {
            write("\n\n\n\n".getBytes());
            cutPaper();
        }
    }

    @Override
    public void printImage(File file, ALIGNMENT alignment, boolean isCut) throws IOException {
        // TODO Auto-generated method stub
        Bitmap fileBtp = null;
        if (file != null) {
            FileInputStream fis = new FileInputStream(file);
            fileBtp = BitmapFactory.decodeStream(fis);
        }
        byte[] b = null;
        HashMap<String, Object> result = null;
        if (fileBtp != null) {
            result = BitImageUtils.getUserPrintImageType(fileBtp);
        }
        b = (byte[]) result.get("img");
        if (b != null && b.length > 0) {
            selectAlignment(alignment);
            CMD_UPLOAD_IMAGE[2] = (byte) ((Integer.parseInt(result.get("width").toString())) / 8);
            CMD_UPLOAD_IMAGE[3] = (byte) ((Integer.parseInt(result.get("height").toString())) / 8);
            write(CMD_UPLOAD_IMAGE);
            write(b);
            write(CMD_PRINT_IMAGE);

        }
        if (isCut) {
            write("\n\n\n\n".getBytes());
            cutPaper();
        }

    }

    @Override
    public void printImage(Bitmap bitmap, ALIGNMENT alignment, boolean isCut) throws IOException {

        byte[] b = null;
        HashMap<String, Object> result = null;
        if (bitmap != null) {
            result = BitImageUtils.getUserPrintImageType(bitmap);
        }
        b = (byte[]) result.get("img");
        if (b != null && b.length > 0) {
            selectAlignment(alignment);
            CMD_UPLOAD_IMAGE[2] = (byte) ((Integer.parseInt(result.get("width").toString())) / 8);
            CMD_UPLOAD_IMAGE[3] = (byte) ((Integer.parseInt(result.get("height").toString())) / 8);
            write(CMD_UPLOAD_IMAGE);
            write(b);
            write(CMD_PRINT_IMAGE);

        }
        if (isCut) {
            write("\n\n\n\n".getBytes());
            cutPaper();
        }
    }

    @Override
    public void printStringImage(String string, ALIGNMENT alignment, FONT font, Boolean bold, Boolean underlined,
            CHARACTERSIZE characterSize, ALIGNMENT fileAlignment, File file, boolean isCut) throws IOException {
        // TODO Auto-generated method stub
        byte[] b;
        this.selectAlignment(alignment);
        this.selectFont(font);
        this.selectFontBold(bold);
        this.selectFontUndler(underlined);
        this.selectCharsetSize(characterSize);
        command = String.format("'%s' LF", string);
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null) {
            write(b);
        }
        write("\n\n".getBytes());

        // 打印图片
        byte[] b2;
        this.selectAlignment(fileAlignment);
        Bitmap fileBtp = null;
        if (file != null) {
            FileInputStream fis = new FileInputStream(file);
            fileBtp = BitmapFactory.decodeStream(fis);
        }
        HashMap<String, Object> result = null;
        if (fileBtp != null) {
            result = BitImageUtils.getUserPrintImageType(fileBtp);
        }
        b2 = (byte[]) result.get("img");
        if (b != null && b.length > 0) {

            selectAlignment(alignment);
            CMD_UPLOAD_IMAGE[2] = (byte) ((Integer.parseInt(result.get("width").toString())) / 8);
            CMD_UPLOAD_IMAGE[3] = (byte) ((Integer.parseInt(result.get("height").toString())) / 8);
            write(CMD_UPLOAD_IMAGE);
            write(b2);
            write(CMD_PRINT_IMAGE);

        }
        if (isCut) {
            write("\n\n\n\n".getBytes());
            cutPaper();
        }
    }

    @Override
    public void printStringList(List<StringType> data, boolean isCut) throws IOException {
        if (data != null && data.size() > 0) {
            int isize = data.size();
            for (int i = 0; i < isize; i++) {
                StringType type = data.get(i);
                byte[] b;
                this.selectAlignment(type.getAlignment());
                this.selectFont(type.getFont());
                this.selectFontBold(type.isBold());
                this.selectFontUndler(type.isUnderLine());
                this.selectCharsetSize(type.getCharacterSize());
                if (type.isLF()) {
                    command = String.format("'%s' LF", type.getContent());
                    Log.e("vvvvvvvvvvv", command);
                } else {
                    command = type.getContent();
                    Log.e("bbbbbbbbbb", command);
                }
                b = EscposUtil.convertEscposToBinary(command);
                if (b != null) {
                    write(b);
                }
            }
        }
        if (isCut) {
            // write("\n\n\n\n".getBytes());
            cutPaper();
        }
    }

    @Override
    public void printImageList(List<ImageType> data, boolean isCut) throws IOException {
        if (data != null && data.size() > 0) {
            int isize = data.size();
            for (int i = 0; i < isize; i++) {
                ImageType type = data.get(i);
                byte[] b = null;
                HashMap<String, Object> result = null;
                if (type.getFileBtp() != null) {
                    result = BitImageUtils.getUserPrintImageType(type.getFileBtp());
                }
                b = (byte[]) result.get("img");
                if (b != null && b.length > 0) {
                    this.selectAlignment(type.getAlignment());
                    CMD_UPLOAD_IMAGE[2] = (byte) ((Integer.parseInt(result.get("width").toString())) / 8);
                    CMD_UPLOAD_IMAGE[3] = (byte) ((Integer.parseInt(result.get("height").toString())) / 8);
                    write(CMD_UPLOAD_IMAGE);
                    write(b);
                    write(CMD_PRINT_IMAGE);
                    write("\n\n\n\n\n".getBytes());
                    write(CMD_CUT);
                }
            }
        }
        if (isCut) {
            write("\n\n\n\n".getBytes());
            cutPaper();
        }

    }

    @Override
    public void printStringAndImageList(List<StringType> data1, List<ImageType> data2, boolean isCut)
            throws IOException {
        if (data1 != null && data1.size() > 0) {
            int isize = data1.size();
            this.setLineSpance((byte) 50);
            for (int i = 0; i < isize; i++) {
                StringType type = data1.get(i);
                byte[] b;
                this.selectAlignment(type.getAlignment());
                this.selectFont(type.getFont());
                this.selectFontBold(type.isBold());
                this.selectFontUndler(type.isUnderLine());
                this.selectCharsetSize(type.getCharacterSize());
                command = String.format("'%s' LF", type.getContent());
                b = EscposUtil.convertEscposToBinary(command);
                if (b != null) {
                    write(b);
                }
            }
        }
        if (data2 != null && data2.size() > 0) {
            int isize = data2.size();
            for (int i = 0; i < isize; i++) {
                ImageType type = data2.get(i);

                byte[] b = null;
                HashMap<String, Object> result = null;
                if (type.getFileBtp() != null) {
                    result = BitImageUtils.getUserPrintImageType(type.getFileBtp());
                }
                b = (byte[]) result.get("img");
                if (b != null && b.length > 0) {
                    this.selectAlignment(type.getAlignment());
                    CMD_UPLOAD_IMAGE[2] = (byte) ((Integer.parseInt(result.get("width").toString())) / 8);
                    CMD_UPLOAD_IMAGE[3] = (byte) ((Integer.parseInt(result.get("height").toString())) / 8);
                    write(CMD_UPLOAD_IMAGE);
                    write(b);
                    write(CMD_PRINT_IMAGE);
                    write("\n\n\n\n\n".getBytes());
                    write(CMD_CUT);
                }
            }
        }
        if (isCut) {
            write("\n\n\n\n".getBytes());
            cutPaper();
        }
    }

    @Override
    public void printString(StringType stringType, boolean isCut) throws IOException {
        byte[] b;
        this.selectAlignment(stringType.getAlignment());
        this.selectFont(stringType.getFont());
        this.selectFontBold(stringType.isBold());
        this.selectFontUndler(stringType.isUnderLine());
        this.selectCharsetSize(stringType.getCharacterSize());
        command = String.format("'%s' LF", stringType.getContent());
        b = EscposUtil.convertEscposToBinary(command);
        if (b != null) {
            write(b);
        }
        if (isCut) {
            write("\n\n\n\n".getBytes());
            cutPaper();
        }
    }

    @Override
    public void printImage(ImageType fileType, boolean isCut) throws IOException {
        // TODO Auto-generated method stub
        byte[] b = null;
        HashMap<String, Object> result = null;
        if (fileType != null && fileType.getFileBtp() != null) {
            result = BitImageUtils.getUserPrintImageType(fileType.getFileBtp());
        }
        b = (byte[]) result.get("img");
        if (b != null && b.length > 0) {
            initPrinter();
            this.selectAlignment(fileType.getAlignment());
            CMD_UPLOAD_IMAGE[2] = (byte) ((Integer.parseInt(result.get("width").toString())) / 8);
            CMD_UPLOAD_IMAGE[3] = (byte) ((Integer.parseInt(result.get("height").toString())) / 8);
            write(CMD_UPLOAD_IMAGE);
            write(b);
            write(CMD_PRINT_IMAGE);
        }
        if (isCut) {
            write("\n\n\n\n".getBytes());
            cutPaper();
        }
    }

    /**
     * 
     * @param tilte1
     *            餐厅名称
     * @param title2
     *            食堂
     * @param titls3
     *            订单title信息
     * @param title4
     *            菜品
     * @param title_line
     *            横线
     * @param orderInfo
     *            订单信息
     * @param title_line2
     *            横线
     * @param money_type
     *            总金额
     * @param Imgtype
     *            二维码
     * @param welcome_type
     *            欢迎语
     * @param tel_type
     *            电话
     * @param time_type
     *            打印时间
     * @throws IOException
     */
    public void printOrderMenu(StringType tilte1, StringType title2, List<StringType> titls3, StringType title4,
            StringType title_line, List<StringType> orderInfo, StringType title_line2, StringType money_type,
            ImageType Imgtype, StringType welcome_type, StringType tel_type, StringType time_type,
            StringType custorm_type) throws IOException {
        this.initPrinter();
        this.setLineSpance((byte) 50);
        this.printString(tilte1, false);
        this.printString(title2, false);
        write("\n".getBytes());
        this.printStringList(titls3, false);
        write("\n".getBytes());
        this.printString(title4, false);
        this.printString(title_line, false);
        this.printStringList(orderInfo, false);
        this.printString(title_line2, false);
        write("\n".getBytes());
        this.printString(money_type, false);
        write("\n".getBytes());
        this.printImage(Imgtype, false);
        write("\n".getBytes());
        this.printString(welcome_type, false);
        this.printString(tel_type, false);
        write("\n".getBytes());
        this.printString(time_type, false);
        write("\n".getBytes());
        this.printString(custorm_type, true);

    }
}
