/*
 * File Name: StringType.java 
 * History:
 * Created by Administrator on 2016-4-28
 */
package com.example.printerdemogb2312.printer.model;

import com.example.printerdemogb2312.printer.usb.UsbPrinter.CHARACTERSIZE;
import com.example.printerdemogb2312.printer.usb.UsbPrinter.FONT;

/**
 * 打印文字内容的格式 (Description)
 * 
 * @author zaokafei
 * @version 1.0
 * @date 2016-4-28
 */
public class StringType extends PrinterType {
    private String content = "Hello world !";
    private FONT font = FONT.FONT_A;// 字体(仅文字起作用)
    private boolean isBold = false;// 是否粗体(仅文字起作用)
    private boolean isUnderLine = false;// 是否下划线(仅文字起作用)
    private CHARACTERSIZE characterSize = CHARACTERSIZE.Standard;// 字符大小(仅文字起作用)
    private boolean isLF = true;// 是否换行

    public boolean isLF() {
        return isLF;
    }

    public void setLF(boolean isLF) {
        this.isLF = isLF;
    }

    public FONT getFont() {
        return font;
    }

    public boolean isBold() {
        return isBold;
    }

    public boolean isUnderLine() {
        return isUnderLine;
    }

    public CHARACTERSIZE getCharacterSize() {
        return characterSize;
    }

    public void setFont(FONT font) {
        this.font = font;
    }

    public void setBold(boolean isBold) {
        this.isBold = isBold;
    }

    public void setUnderLine(boolean isUnderLine) {
        this.isUnderLine = isUnderLine;
    }

    public void setCharacterSize(CHARACTERSIZE characterSize) {
        this.characterSize = characterSize;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
