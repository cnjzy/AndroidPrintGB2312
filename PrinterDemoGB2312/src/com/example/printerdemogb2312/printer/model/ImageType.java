/*
 * File Name: ImageType.java 
 * History:
 * Created by Administrator on 2016-4-28
 */
package com.example.printerdemogb2312.printer.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Í¼Æ¬·â×° (Description)
 * 
 * @author zaokafei
 * @version 1.0
 * @date 2016-4-28
 */
public class ImageType extends PrinterType {
    private File fileImage = null;
    private Bitmap fileBtp = null;

    public Bitmap getFileBtp() throws FileNotFoundException {
        if (fileImage != null) {
            FileInputStream fis = new FileInputStream(fileImage);
            fileBtp = BitmapFactory.decodeStream(fis);
        }
        return fileBtp;
    }

    public void setFileImage(File fileImage) {
        this.fileImage = fileImage;
    }

    public void setFileBtp(Bitmap fileBtp) {
        this.fileBtp = fileBtp;
    }

}
