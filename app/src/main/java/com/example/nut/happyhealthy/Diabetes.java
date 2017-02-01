package com.example.nut.happyhealthy;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Diabetes extends AppCompatActivity {

    //การประกาศตัวแปร
    private DiabetesTABLE objdiabetesTABLE;
    private EditText D_costSugarBefore,D_costSugarAfter;
    private String  str_D_Date, intCostSugarBefore,intCostSugarAfter,str_L_before,str_L_after ;
    private TextView  D_date;
    SimpleDateFormat df_show;
    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diabetes);



        //Bind wiget
        D_date = (TextView)findViewById(R.id.D_date);
        D_costSugarBefore = (EditText) findViewById(R.id.D_costSugarBefore);
        D_costSugarAfter = (EditText) findViewById(R.id.D_costSugarAfter);

        connectDataBase();


        df_show = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        c = Calendar.getInstance();
        D_date.setText(df_show.format(c.getTime()));



    }////main method

    private void connectDataBase() {

        objdiabetesTABLE = new DiabetesTABLE(this);

    }//ConnectDataBase

    public void ClickDisLevelsSugar(View view) {

        //get value edit tezt
        str_D_Date = df_show.format(c.getTime());
        intCostSugarBefore = D_costSugarBefore.getText().toString().trim();
        intCostSugarAfter = D_costSugarAfter.getText().toString().trim();


        //Checkspace
        if (str_D_Date.equals("") ||  intCostSugarBefore.equals("")|| intCostSugarAfter.equals("")) {

            showAlert();

            /**MyAlert myAlert = new MyAlert();
             myAlert.myDialog(this, "เบาหวาน", "กรุณาใส่ข้อมูลผู้ใช้งานให้ครบค่ะ");**/

        }else  {
            confirmDiabetes();
            showNotification();

        }
    }//Click

    private void confirmDiabetes() {

        str_L_after = findMyLevelDiseaseAfter();
        str_L_before =findMyLevelDiseaseBefore();

        // Find BMI
        /**int  intcostsugarbefore = Integer.parseInt(intCostSugarBefore);
        int  intcostsugarafter = Integer.parseInt(intCostSugarAfter);

        int IntCostSugarBefore = intcostsugarbefore;
        int IntCostSugarAfter = intcostsugarafter;**/

        //ShowConfrimDiabetes();

       AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("คุณต้องการบันทึกข้อมูลใช่ไหม?");
        builder.setMessage(" วันที่ :" + str_D_Date+ "\n"
                + " ค่าน้ำตาล : " + intCostSugarBefore +"\n"
                + " อยู่ในเกณฑ์ที่ : " + str_L_before );
        builder.setCancelable(false);
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                upDataDiabetestoSQLite();

           }
        });
        builder.show();


    }//ComfirmData

    /**private void ShowConfrimDiabetes() {
    }//ShowConfrimDiabetes**/

    private String findMyLevelDiseaseAfter() {
        String[] resultStrings = getResources().getStringArray(R.array.my_disease);
        String myResult = null;
        Integer IntCostSugarAfter = Integer.parseInt(intCostSugarAfter);

        if (IntCostSugarAfter >=300 ) {
            myResult = resultStrings[0];
        } else if (IntCostSugarAfter >= 200) {
            myResult = resultStrings[1];
        } else if (IntCostSugarAfter >=110 ) {
            myResult = resultStrings[2];
        } else if (IntCostSugarAfter <= 70) {
            myResult = resultStrings[3];
        } else {
            myResult = resultStrings[4];
        }


        return myResult;
    }//findMyLevelDiseaseAfter

    private String findMyLevelDiseaseBefore() {
        String[] resultStrings = getResources().getStringArray(R.array.my_disease);
        String myResult = null;
        Integer IntCostSugarBefore = Integer.parseInt(intCostSugarBefore);

        if (IntCostSugarBefore >=300 ) {
            myResult = resultStrings[0];
        } else if (IntCostSugarBefore >= 200) {
            myResult = resultStrings[1];
        } else if (IntCostSugarBefore >=100 ) {
            myResult = resultStrings[2];
        } else if (IntCostSugarBefore <= 70) {
            myResult = resultStrings[3];
        } else {
            myResult = resultStrings[4];
        }

        return myResult;
    }//findMyLevelDiseaseBefore

    private void upDataDiabetestoSQLite() {

        DiabetesTABLE objdiabetesTABLE = new DiabetesTABLE(this);
        long inSertDataUser = objdiabetesTABLE.addNewValueToSQLite
                (str_D_Date, Integer.parseInt(intCostSugarBefore), Integer.parseInt(intCostSugarAfter),str_L_before,str_L_after);
        D_date.setText("");
        D_costSugarBefore.setText("");
        D_costSugarAfter.setText("");
        Toast.makeText(Diabetes.this,"บันทึกข้อมูลเรียบร้อย",Toast.LENGTH_SHORT).show();
        Intent objIntent = new Intent(getApplicationContext(), DisplayDisease.class);
        objIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(objIntent);
        finish();
    }//upDataDiabetestoSQLite

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.warning);
        builder.setTitle("ข้อมูลไม่ครบถ้วน");
        builder.setMessage("กรุณาใส่ข้อมูลผู้ใช้งานให้ครบ");
        builder.setCancelable(false);
        builder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }//ShowAlert



    public void showNotification() {

        Intent intent = new Intent(this, DisplayDisease.class);
//        intent.putExtra("str_L_before", str_L_before);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(User.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_alert)
                        .setContentTitle("วันนี้ค่าน้ำตาลในเลือด")
                        .setContentText( " ค่าน้ำตาลก่อนอาหาร : " + str_L_before + "\n"
                                + " ค่าน้ำตาลหลังอาหาร : " + str_L_after)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1000, notification);

    }

    public void ClickHistoryDiabetes(View view) {
        startActivity(new Intent(Diabetes.this,History_Diabetes.class));
    }//ClickHistoryDiabetes


    public void ClickBackDiaHome(View view) {
        startActivity(new Intent(Diabetes.this,MainActivity.class));
    }//ClickBackHome



}//Main Class