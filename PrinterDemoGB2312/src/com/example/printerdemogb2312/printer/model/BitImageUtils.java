/*
 * File Name: BitImageUtils.java 
 * History:
 * Created by Administrator on 2016-4-28
 */
package com.example.printerdemogb2312.printer.model;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BitImageUtils {
    public static HashMap<String, Object> getUserPrintImageType(Bitmap bmp) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        byte[] data = null;

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int bw = (w + 7) / 8;
        if (bw > 255) {
            bw = 255;
        }
        int bh = h / 8;
        if (bh > 255) {
            bh = 255;
        }

        int width = bw * 8;
        int height = bh * 8;
        int pitch = h / 8;
        data = new byte[w * pitch];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (bmp.getPixel(x, y) == Color.BLACK) {
                    if (x >= width || y >= height) {
                        continue;
                    }
                    int mask = (0x0080 >> (y % 8));
                    data[x * pitch + y / 8] |= mask;
                }
            }
        }
        result.put("img", data);
        result.put("width", width);
        result.put("height", height);
        return result;
    }
}
