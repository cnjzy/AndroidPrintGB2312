package com.example.printerdemogb2312;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.printerdemogb2312.printer.FactoryPrinter;
import com.example.printerdemogb2312.printer.model.ImageType;
import com.example.printerdemogb2312.printer.model.StringType;
import com.example.printerdemogb2312.printer.usb.UsbPrinter.ALIGNMENT;
import com.example.printerdemogb2312.printer.usb.UsbPrinter.CHARACTERSIZE;
import com.example.printerdemogb2312.printer.usb.UsbPrinter.FONT;
import com.example.printerdemogb2312.printer.usb.UsbPrinterUtil;

public class MainActivity extends Activity implements OnClickListener {

    UsbPrinterUtil mUtil;
    FactoryPrinter mFactoryPrinter;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        final TextView tvDeviece = (TextView) findViewById(R.id.tv_device);
        Button btnString = (Button) findViewById(R.id.btnString);
        Button btnImg = (Button) findViewById(R.id.btnImg);
        Button btnMenu = (Button) findViewById(R.id.btnMenu);
        Button btnMenuImg = (Button) findViewById(R.id.btnMenuImage);
        btnString.setOnClickListener(this);
        btnImg.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnMenuImg.setOnClickListener(this);

        mUtil = new UsbPrinterUtil(this);
        List<UsbDevice> devs = mUtil.getUsbPrinterList();

        try {
            Spinner spinner = (Spinner) findViewById(R.id.spinnerPortType);

            String[] strDev = new String[devs.size()];
            for (int i = 0; i < strDev.length; i++) {
                UsbDevice d = devs.get(i);
                strDev[i] = String.format("USB[%04X:%04X]: id=%d, %s", d.getVendorId(), d.getProductId(),
                        d.getDeviceId(), d.getDeviceName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strDev);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                    List<UsbDevice> devs = mUtil.getUsbPrinterList();
                    UsbDevice device = devs.get(position);
                    mFactoryPrinter = new FactoryPrinter(mContext, device);
                    mUtil.requestPermission(device, null);
                    tvDeviece.setText(String.format("USB[%04X:%04X]: id=%d, %s", device.getVendorId(),
                            device.getProductId(), device.getDeviceId(), device.getDeviceName()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }

            });

        } catch (Exception e) {
            messageBox(this, "Exception: " + e.toString() + " - " + e.getMessage(), "onCreate Error");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnString:
            printString();
            break;
        case R.id.btnImg:
            printImg();
            break;
        case R.id.btnMenu:
            printMenu();
            break;
        case R.id.btnMenuImage:
            break;
        }
    }

    private void printString() {
        try {
            StringType type = new StringType();
            type.setAlignment(ALIGNMENT.LEFT);
            type.setBold(false);
            type.setCharacterSize(CHARACTERSIZE.Standard);
            type.setContent("�����¾����Ǵ���ʱ�����ӣ����������ѧ��Ʒ�������¾�������Ϊ��֮�������й���ʷ����ΰ�������֮һ�����й���ѧ����ѧ�����Ρ��ڽ̵Ȳ��������Ӱ�졣�����Ϲ��̿�����֯ͳ�ƣ������¾����ǳ��ˡ�ʥ�������ⱻ���������ַ����������Ļ�������");
            type.setFont(FONT.FONT_A);
            type.setUnderLine(false);
            mFactoryPrinter.getUSBSingleStance().printString(type, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printImg() {
        try {
            ImageType type = new ImageType();
            type.setAlignment(ALIGNMENT.CENTER);
            Bitmap bmp = BitmapFactory.decodeStream(mContext.getAssets().open("abc.png"));
            type.setFileBtp(bmp);
            mFactoryPrinter.getUSBSingleStance().printImage(type, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printMenu() {
        try {
            String str_blank1 = "   ";
            String str_blank2 = "       ";
            StringType title1 = new StringType();
            title1.setAlignment(ALIGNMENT.LEFT);
            title1.setContent("              ���Բ���(�Ļݶ���)            ");
            title1.setFont(FONT.FONT_A);

            StringType title2 = new StringType();
            title2.setAlignment(ALIGNMENT.LEFT);
            title2.setBold(true);
            title2.setCharacterSize(CHARACTERSIZE.DoubleWH);
            title2.setContent("        ʳ��-7          ");

            ArrayList<StringType> title3 = new ArrayList<StringType>();

            // ������
            StringType order_type = new StringType();
            order_type.setAlignment(ALIGNMENT.LEFT);
            order_type.setContent("������:" + str_blank2 + "44234234234234");
            title3.add(order_type);

            // �µ�ʱ��
            StringType ordertime_type = new StringType();
            ordertime_type.setAlignment(ALIGNMENT.LEFT);
            ordertime_type.setContent("��  ��:" + str_blank2 + "2015-02-2 21:25:25");
            title3.add(ordertime_type);

            // ֧����ʽ
            StringType pay_type = new StringType();
            pay_type.setAlignment(ALIGNMENT.LEFT);
            pay_type.setContent("֧����ʽ:" + str_blank1 + "  ΢��֧��");
            title3.add(pay_type);

            // ��ע
            StringType remark_type = new StringType();
            remark_type.setAlignment(ALIGNMENT.LEFT);
            remark_type.setContent("��  ע:" + str_blank2 + "��������,�����");
            title3.add(remark_type);

            //
            // ��Ʒ
            StringType title4 = new StringType();
            title4.setAlignment(ALIGNMENT.LEFT);
            title2.setBold(true);
            title4.setContent("��Ʒ:" + "                      ����  X ����");

            StringType title_line = new StringType();
            title_line.setContent("_____________________________________________");

            // ��Ʒ
            ArrayList<StringType> orderInfo = new ArrayList<StringType>();

            StringType type1 = new StringType();
            type1.setAlignment(ALIGNMENT.LEFT);
            type1.setContent("��������" + "                    " + "28.00 X 4" + "    ");
            orderInfo.add(type1);

            StringType type2 = new StringType();
            type2.setAlignment(ALIGNMENT.LEFT);
            type2.setContent("��������" + "                    " + "28.00 X 4" + "    ");
            orderInfo.add(type2);

            // ����
            StringType title_line2 = new StringType();
            title_line2.setContent("_____________________________________________");

            // �ܽ��

            StringType money_type = new StringType();
            money_type.setAlignment(ALIGNMENT.LEFT);
            money_type.setContent("�ܽ��:" + "                      28000");

            // ��ӡ��ά��
            ImageType Imgtype = new ImageType();
            Imgtype.setAlignment(ALIGNMENT.CENTER);
            Bitmap bmp = BitmapFactory.decodeStream(mContext.getAssets().open("abc.png"));
            Imgtype.setFileBtp(bmp);

            // ��ӭ����
            StringType welcome_type = new StringType();
            welcome_type.setAlignment(ALIGNMENT.CENTER);
            welcome_type.setContent("   ��ӭ�ٴι���        ");

            StringType tel_type = new StringType();
            tel_type.setAlignment(ALIGNMENT.CENTER);
            tel_type.setContent(" ���ߵ绰��400-800-800 ");

            StringType time_type = new StringType();
            time_type.setAlignment(ALIGNMENT.LEFT);
            time_type.setContent("��ӡʱ��:2016-04-29 12:25:36");

            StringType custorm_type = new StringType();
            custorm_type.setAlignment(ALIGNMENT.RIGHT);
            custorm_type.setBold(true);
            custorm_type.setCharacterSize(CHARACTERSIZE.DoubleWidth);

            custorm_type.setContent("   �˿�����     ");

            // mFactoryPrinter.getUSBSingleStance().printStringList(data, true);
            mFactoryPrinter.getUSBSingleStance().printOrderMenu(title1, title2, title3, title4, title_line, orderInfo,
                    title_line2, money_type, Imgtype, welcome_type, tel_type, time_type, custorm_type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void messageBox(final Context context, final String message, final String title) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(message);
                // alertDialog.setButton2("OK", new OnClickListener(){});

                alertDialog.show();
            }
        });
    }

}
