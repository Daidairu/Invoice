package com.example.hungjuhsiao.invoice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.view.ScrollingView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONArray;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements AsyncResponse {


    DbHelper dbhelper;

    String[] periodNumber;
    String[] invData;
    String[] invDetailList;
    private final String SqliteTableLost = "InvoiceLost";
    private final String SqliteTableWin = "InvoiceWin";

    private final int ssPrice = 10000000;
    private final int sPrice = 2000000;
    private final int price1 = 200000;
    private final int price2 = 40000;
    private final int price3 = 10000;
    private final int price4 = 4000;
    private final int price5 = 1000;
    private final int price6 = 200;
    private final int noPrice = 0;

    TextView textView_money;
    TextView textView_cost;

    ListView lvLost;
    ListView lvWin;

    String[] invMoneyData;


    SimpleCursorAdapter adapter;

    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;
    String scanNumberNumber;//12345678
    String scanNumberDate;//10602

    public static int screenWidthDp;
    public static int screenHeightDp;
    public static int screenDpi;

    private String QRVersion = "0.3";
    private String QRType = "QRCode";
    private String QRInvNum;
    private String QRAction = "qryInvDetail";
    private String QRGeneration = "V2";
    private String QRInvTerm;
    private String QRInvDate;
    private String QREncrypt;
    private String QRSellerID;
    private String QRUUID = "190b5f33-ad02-4681-ae88-bbe11bcace9f";
    private String QRRandomNumber;
    private String QRAppID = "EINV3201702247741";

    SoundPool soundPool;
    HashMap<Integer, Integer> sounddata;

    Boolean spIsLoaded = false;


//    // 星城廣告物件
//    private AdsManager adsManager;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private OutputStream os;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tttest);

        isFirst();

        init();

//        SQLiteDatabase db = dbhelper.getWritableDatabase();
//        db.execSQL("delete from " + SqliteTableLost);// DELETE ALL LIST <3
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText("");


        beepManager = new BeepManager(this);
    }

    public void isFirst()
    {

        preferences = getSharedPreferences("count", Context.MODE_PRIVATE);
        //判断是不是首次登录，
        if (preferences.getBoolean("firststart", true)) {
            editor = preferences.edit();
            //将登录标志位设置为false，下次登录时不在显示首次登录界面
            editor.putBoolean("firststart", false);
            editor.commit();
            //
            AlertDialog.Builder firstDialog = new AlertDialog.Builder(MainActivity.this);
            firstDialog.setTitle("使用說明");
            View viewDialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.firstdialog, null);
            firstDialog.setView(viewDialog);
            firstDialog.setPositiveButton("我知道了", null);
            firstDialog.show();
        }
    }


    public void init() {
        //排版用
        DisplayMetrics metrics = new DisplayMetrics();
        MainActivity.this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        screenWidthDp = metrics.widthPixels;
        screenHeightDp = metrics.heightPixels;

        screenDpi = metrics.densityDpi;
        Log.e("TAG", "h*w:" + screenHeightDp + "/" + screenWidthDp + "/" + screenDpi);
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 4);
        sounddata = new HashMap<Integer, Integer>();
        sounddata.put(1, soundPool.load(this, R.raw.bingo, 1));
        sounddata.put(2, soundPool.load(this, R.raw.nobingo, 1));
        sounddata.put(3, soundPool.load(this, R.raw.noperiod, 1));
        sounddata.put(4, soundPool.load(this, R.raw.haschecked, 1));

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool sound, int sampleId, int status) {
                spIsLoaded = true;
            }
        });


//        // 廣告管理器初始化
//        adsManager = new AdsManager(this);
//        adsManager.displayAds(this);

        openSqlLite();
        setUI();
        setListView();
        setMoneyView();
    }

    public void openSqlLite() {
        dbhelper = new DbHelper(this);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        final String SQLLost = "CREATE TABLE IF NOT EXISTS " + SqliteTableLost + "( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "invNum VARCHAR(15), " + "invDate VARCHAR(10), " + "sellerName VARCHAR(30), " +
                "invStatus VARCHAR(10), " + "invPeriod VARCHAR(10), " + "sellerBan VARCHAR(10), " +
                "sellerAddress VARCHAR(40), " + "invoiceTime VARCHAR(15), " +
                "detail VARCHAR(255), " +
                "getMoney int(30)," + "costMoney int(30)" +
                ");";
        db.execSQL(SQLLost);

        final String SQLWin = "CREATE TABLE IF NOT EXISTS " + SqliteTableWin + "( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "invNum VARCHAR(15), " + "invDate VARCHAR(10), " + "sellerName VARCHAR(30), " +
                "invStatus VARCHAR(10), " + "invPeriod VARCHAR(10), " + "sellerBan VARCHAR(10), " +
                "sellerAddress VARCHAR(40), " + "invoiceTime VARCHAR(15), " +
                "detail VARCHAR(255), " +
                "getMoney int(30)," + "costMoney int(30)" +
                ");";
        db.execSQL(SQLWin);

//        final String DROP_TABLE = "DROP TABLE IF EXISTS " + SqliteTable;
//        db.execSQL(DROP_TABLE);

    }

    private void setUI() {
        RelativeLayout relativeMoney = (RelativeLayout) findViewById(R.id.relativeMoney);
        ViewGroup.LayoutParams paramsMoney = relativeMoney.getLayoutParams();
        paramsMoney.height = screenHeightDp / 5 / 3 / 3 * 3 * 2;


        textView_money = (TextView) findViewById(R.id.textView_getMoney);
        textView_money.setHeight(screenHeightDp / 5 / 3 / 3 * 3);
        textView_money.setWidth(screenWidthDp / 2);
        textView_money.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView_money.setTextColor(Color.parseColor("#6a4428"));
//        textView_money.setTypeface(Typeface.createFromAsset(getAssets()
//                , "fonts/word.TTF"));

        textView_cost = (TextView) findViewById(R.id.textView_costMoney);
        textView_cost.setHeight(screenHeightDp / 5 / 3 / 3 * 3);
        textView_cost.setWidth(screenWidthDp / 2);
        textView_cost.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView_cost.setTextColor(Color.parseColor("#6a4428"));
//        textView_cost.setTypeface(Typeface.createFromAsset(getAssets()
//                , "fonts/word.TTF"));


        ImageView imageViewLeft = (ImageView) findViewById(R.id.imageViewLeft);
        imageViewLeft.getLayoutParams().height = screenHeightDp / 5 / 3 / 3 * 3;

        ImageView imageViewRight = (ImageView) findViewById(R.id.imageViewRight);
        imageViewRight.getLayoutParams().height = screenHeightDp / 5 / 3 / 3 * 3;


        lvLost = (ListView) findViewById(R.id.listview_left);
        LinearLayout.LayoutParams lpLost = (LinearLayout.LayoutParams) lvLost.getLayoutParams();
        lpLost.height = screenHeightDp / 5 / 9 * 20;
        lpLost.width = screenWidthDp / 2;
        lvLost.setLayoutParams(lpLost);

        RelativeLayout relativeL = (RelativeLayout) findViewById(R.id.relativeL);
        ViewGroup.LayoutParams paramsL = relativeL.getLayoutParams();
        paramsL.height = screenHeightDp / 5 / 9 * 23;
        paramsL.width = screenWidthDp / 2;
        relativeL.setBackground(getResources().getDrawable(R.mipmap.invlistleft));


        lvWin = (ListView) findViewById(R.id.listview_right);
        LinearLayout.LayoutParams lpWin = (LinearLayout.LayoutParams) lvWin.getLayoutParams();
        lpWin.height = screenHeightDp / 5 / 9 * 20;
        lpWin.width = screenWidthDp / 2;
        lvWin.setLayoutParams(lpWin);

        RelativeLayout rr = (RelativeLayout) findViewById(R.id.relativeR);
        ViewGroup.LayoutParams params2 = rr.getLayoutParams();
        params2.height = screenHeightDp / 5 / 9 * 23;
        params2.width = screenWidthDp / 2;
        rr.setBackground(getResources().getDrawable(R.mipmap.invlistright));


        Button btn_L = (Button) findViewById(R.id.btnClear_left);
        RelativeLayout.LayoutParams lp_btn_L = (RelativeLayout.LayoutParams) btn_L.getLayoutParams();
        lp_btn_L.height = screenHeightDp / 5 / 3 * 1/10*12;
        btn_L.setLayoutParams(lp_btn_L);
        btn_L.setOnClickListener(BtnOnClickEvent);


        Button btn_R = (Button) findViewById(R.id.btnClear_right);
        RelativeLayout.LayoutParams lp_btn_R = (RelativeLayout.LayoutParams) btn_R.getLayoutParams();
        lp_btn_R.height = screenHeightDp / 5 / 3 * 1/10*12;
        btn_R.setLayoutParams(lp_btn_R);
        btn_R.setOnClickListener(BtnOnClickEvent);


        LinearLayout linearLine = (LinearLayout) findViewById(R.id.LinearLine);
        ViewGroup.LayoutParams line = linearLine.getLayoutParams();
        line.height = screenHeightDp / 5 / 9 * 20;
    }

    public void playSound(int sound, int number) {
        AudioManager am = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = volumnCurrent / audioMaxVolumn;

        soundPool.play(sounddata.get(sound),
                1,//volumnRatio,// 左声道音量
                1,//volumnRatio,// 右声道音量
                1, // 优先级
                number,// 循环播放次数
                1);// 回放速度，该值在0.5-2.0之间 1为正常速度
    }


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() == null) { //   (result.getText() == null||result.getText().equals(lastText))
                // Prevent duplicate scans
                Log.e("TAG", result.getText());
                return;
            } else if (result.getText().equals(lastText)) {
                //Toast.makeText(MainActivity.this, "已對過!", Toast.LENGTH_SHORT).show();
                return;
            }

            lastText = result.getText();
//          barcodeView.setStatusText(result.getText());
            beepManager.playBeepSoundAndVibrate();
            Log.e("TAG", "" + result.getText());
            Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
            if (lastText.length() >= 77) {//發票QR CODE固定77碼
                setQRCodeData(lastText);
            }

            //Added preview of scanned barcode
//            ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
//            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
//        adsManager.setAdsOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
//        adsManager.setAdsOnPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 情除廣告
//        adsManager.clearAds();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private void setQRCodeData(String lastText) {
        QRInvNum = lastText.substring(0, 10);

        if (Integer.parseInt(lastText.substring(10, 15)) % 2 == 0) {
            QRInvTerm = lastText.substring(10, 15);//10108
        } else {
            QRInvTerm = String.valueOf(Integer.parseInt(lastText.substring(10, 15)) + 1);//10109+1
        }

        QRInvDate = String.valueOf(
                Integer.parseInt(lastText.substring(10, 13)) + 1911) + "/" + lastText.substring(13, 15) +
                "/" + lastText.substring(15, 17);// 2017/01/11
        QREncrypt = lastText.substring(53, 77);
        QRSellerID = lastText.substring(45, 53);
        QRRandomNumber = lastText.substring(17, 21);

        Log.e("TAG", "QRInvNum:" + QRInvNum + " /" + "QRInvTerm:" + QRInvTerm + " /" + "QRInvDate:" + QRInvDate + " /" + "QREncrypt:" + QREncrypt + " /" +
                "QRSellerID:" + QRSellerID + " /" + "QRRandomNumber:" + QRRandomNumber);

        compare(lastText.substring(0, 17));
    }

    private void compare(String scanNumber) {   //AA-12345678-1061224
        scanNumberNumber = scanNumber.substring(2, 10);//12345678
        scanNumberDate = scanNumber.substring(10, 17);//1061224

        if (checkNum(scanNumber)) {
            getNumber(scanNumberDate.substring(0, 5));//10612
        } else {
            playSound(4, 1);
            Toast.makeText(MainActivity.this, "對過了", Toast.LENGTH_SHORT).show();
        }
    }

    private void getNumber(String invPeriod) {
        GetInvoiceApi getApiTaskNumber = new GetInvoiceApi();
        getApiTaskNumber.delegate = this;
        getApiTaskNumber.execute(invPeriod);
    }

    //this override the implemented method from asyncTask
    @Override
    public void processFinish(String[] output) {
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.
        Log.e("TAG","20180103"+"output"+output);
        periodNumber = output;
        try {
            if (!periodNumber[4].equals(null)) {
                getInvData();
            }
        } catch (Exception e) {
//            getInvData();//
            if (spIsLoaded == true) {
                playSound(3, 1);
            }
            Toast.makeText(MainActivity.this, "尚未開獎!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getInvData() {
        GetInvoiceData getApiTaskData = new GetInvoiceData();

        getApiTaskData.delegate = this;
        getApiTaskData.execute(QRVersion, QRType, QRInvNum, QRAction, QRGeneration, QRInvTerm,
                QRInvDate, QREncrypt, QRSellerID, QRUUID, QRRandomNumber, QRAppID);
    }

    @Override
    public void processFinishInvData(String[] output) {
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.
        invData = output;

        spiltInvDetail(invData[11]);

//        for (int i = 0; i < invData.length; i++) {
//            Log.e("TAG", "inv-" + i + invData[i]);
//        }
        //function go to addprice and bingo
        checkAndAddPrice(scanNumberNumber);
    }

    private String[] spiltInvDetail(String invData) {
        try {
            JSONArray jsonArray = new JSONArray(invData);

            invDetailList = new String[5 * jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                invDetailList[i * 5 + 0] = jsonArray.getJSONObject(i).getString("amount");
                invDetailList[i * 5 + 1] = jsonArray.getJSONObject(i).getString("description");
                invDetailList[i * 5 + 2] = jsonArray.getJSONObject(i).getString("unitPrice");
                invDetailList[i * 5 + 3] = jsonArray.getJSONObject(i).getString("quantity");
                invDetailList[i * 5 + 4] = jsonArray.getJSONObject(i).getString("rowNum");
            }
//            for (int i = 0; i < invDetailList.length; i++) {
//                Log.e("TAG", i + invDetailList[i]);
//            }
        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }
        return invDetailList;
    }

    private void checkAndAddPrice(String scanNumberNumber) {
        Log.e("TAG", "scanNumberNumber:" + scanNumberNumber);

        if (scanNumberNumber.equals(periodNumber[0]))//特別獎1000w
        {
            Toast.makeText(MainActivity.this, "中特別獎!", Toast.LENGTH_SHORT).show();
            dataInsertSQL(ssPrice);
        } else if (scanNumberNumber.equals(periodNumber[1]) ||
                scanNumberNumber.equals(periodNumber[2]) ||
                scanNumberNumber.equals(periodNumber[3]))//特獎200w
        {
            Toast.makeText(MainActivity.this, "中特獎!", Toast.LENGTH_SHORT).show();
            dataInsertSQL(sPrice);
        } else if (scanNumberNumber.equals(periodNumber[4])
                || scanNumberNumber.equals(periodNumber[5])
                || scanNumberNumber.equals(periodNumber[6])
                || scanNumberNumber.equals(periodNumber[7])
                || scanNumberNumber.equals(periodNumber[8])
                || scanNumberNumber.equals(periodNumber[9])
                || scanNumberNumber.equals(periodNumber[10])
                || scanNumberNumber.equals(periodNumber[11])
                || scanNumberNumber.equals(periodNumber[12])
                || scanNumberNumber.equals(periodNumber[13]))//頭獎20w
        {
            Toast.makeText(MainActivity.this, "中頭獎!", Toast.LENGTH_SHORT).show();
            dataInsertSQL(price1);
        } else if (spiltNumber(scanNumberNumber, 1).equals(spiltNumber(periodNumber[4], 1))
                || spiltNumber(scanNumberNumber, 1).equals(spiltNumber(periodNumber[5], 1))
                || spiltNumber(scanNumberNumber, 1).equals(spiltNumber(periodNumber[6], 1))
                || spiltNumber(scanNumberNumber, 1).equals(spiltNumber(periodNumber[7], 1))
                || spiltNumber(scanNumberNumber, 1).equals(spiltNumber(periodNumber[8], 1))
                || spiltNumber(scanNumberNumber, 1).equals(spiltNumber(periodNumber[9], 1))
                || spiltNumber(scanNumberNumber, 1).equals(spiltNumber(periodNumber[10], 1))
                || spiltNumber(scanNumberNumber, 1).equals(spiltNumber(periodNumber[11], 1))
                || spiltNumber(scanNumberNumber, 1).equals(spiltNumber(periodNumber[12], 1))
                || spiltNumber(scanNumberNumber, 1).equals(spiltNumber(periodNumber[13], 1)))//2獎7碼4w
        {
            Toast.makeText(MainActivity.this, "中二獎!", Toast.LENGTH_SHORT).show();
            dataInsertSQL(price2);
        } else if (spiltNumber(scanNumberNumber, 2).equals(spiltNumber(periodNumber[4], 2))
                || spiltNumber(scanNumberNumber, 2).equals(spiltNumber(periodNumber[5], 2))
                || spiltNumber(scanNumberNumber, 2).equals(spiltNumber(periodNumber[6], 2))
                || spiltNumber(scanNumberNumber, 2).equals(spiltNumber(periodNumber[7], 2))
                || spiltNumber(scanNumberNumber, 2).equals(spiltNumber(periodNumber[8], 2))
                || spiltNumber(scanNumberNumber, 2).equals(spiltNumber(periodNumber[9], 2))
                || spiltNumber(scanNumberNumber, 2).equals(spiltNumber(periodNumber[10], 2))
                || spiltNumber(scanNumberNumber, 2).equals(spiltNumber(periodNumber[11], 2))
                || spiltNumber(scanNumberNumber, 2).equals(spiltNumber(periodNumber[12], 2))
                || spiltNumber(scanNumberNumber, 2).equals(spiltNumber(periodNumber[13], 2)))//3獎6碼1w
        {
            Toast.makeText(MainActivity.this, "中三獎!", Toast.LENGTH_SHORT).show();
            dataInsertSQL(price3);
        } else if (spiltNumber(scanNumberNumber, 3).equals(spiltNumber(periodNumber[4], 3))
                || spiltNumber(scanNumberNumber, 3).equals(spiltNumber(periodNumber[5], 3))
                || spiltNumber(scanNumberNumber, 3).equals(spiltNumber(periodNumber[6], 3))
                || spiltNumber(scanNumberNumber, 3).equals(spiltNumber(periodNumber[7], 3))
                || spiltNumber(scanNumberNumber, 3).equals(spiltNumber(periodNumber[8], 3))
                || spiltNumber(scanNumberNumber, 3).equals(spiltNumber(periodNumber[9], 3))
                || spiltNumber(scanNumberNumber, 3).equals(spiltNumber(periodNumber[10], 3))
                || spiltNumber(scanNumberNumber, 3).equals(spiltNumber(periodNumber[11], 3))
                || spiltNumber(scanNumberNumber, 3).equals(spiltNumber(periodNumber[12], 3))
                || spiltNumber(scanNumberNumber, 3).equals(spiltNumber(periodNumber[13], 3)))//4獎5碼4000
        {
            Toast.makeText(MainActivity.this, "中四獎!", Toast.LENGTH_SHORT).show();
            dataInsertSQL(price4);
        } else if (spiltNumber(scanNumberNumber, 4).equals(spiltNumber(periodNumber[4], 4))
                || spiltNumber(scanNumberNumber, 4).equals(spiltNumber(periodNumber[5], 4))
                || spiltNumber(scanNumberNumber, 4).equals(spiltNumber(periodNumber[6], 4))
                || spiltNumber(scanNumberNumber, 4).equals(spiltNumber(periodNumber[7], 4))
                || spiltNumber(scanNumberNumber, 4).equals(spiltNumber(periodNumber[8], 4))
                || spiltNumber(scanNumberNumber, 4).equals(spiltNumber(periodNumber[9], 4))
                || spiltNumber(scanNumberNumber, 4).equals(spiltNumber(periodNumber[10], 4))
                || spiltNumber(scanNumberNumber, 4).equals(spiltNumber(periodNumber[11], 4))
                || spiltNumber(scanNumberNumber, 4).equals(spiltNumber(periodNumber[12], 4))
                || spiltNumber(scanNumberNumber, 4).equals(spiltNumber(periodNumber[13], 4)))//5獎4碼1000
        {
            Toast.makeText(MainActivity.this, "中五獎!", Toast.LENGTH_SHORT).show();
            dataInsertSQL(price5);
        } else if (spiltNumber(scanNumberNumber, 5).equals(spiltNumber(periodNumber[4], 5))
                || spiltNumber(scanNumberNumber, 5).equals(spiltNumber(periodNumber[5], 5))
                || spiltNumber(scanNumberNumber, 5).equals(spiltNumber(periodNumber[6], 5))
                || spiltNumber(scanNumberNumber, 5).equals(spiltNumber(periodNumber[7], 5))
                || spiltNumber(scanNumberNumber, 5).equals(spiltNumber(periodNumber[8], 5))
                || spiltNumber(scanNumberNumber, 5).equals(spiltNumber(periodNumber[9], 5))
                || spiltNumber(scanNumberNumber, 5).equals(spiltNumber(periodNumber[10], 5))
                || spiltNumber(scanNumberNumber, 5).equals(spiltNumber(periodNumber[11], 5))
                || spiltNumber(scanNumberNumber, 5).equals(spiltNumber(periodNumber[12], 5))
                || spiltNumber(scanNumberNumber, 5).equals(spiltNumber(periodNumber[13], 5))
                || spiltNumber(scanNumberNumber, 5).equals(periodNumber[14])
                || spiltNumber(scanNumberNumber, 5).equals(periodNumber[15])
                || spiltNumber(scanNumberNumber, 5).equals(periodNumber[16])
                || spiltNumber(scanNumberNumber, 5).equals(periodNumber[17])
                || spiltNumber(scanNumberNumber, 5).equals(periodNumber[18])
                || spiltNumber(scanNumberNumber, 5).equals(periodNumber[19]))//6獎3碼200
        {
            Toast.makeText(MainActivity.this, "中六獎!", Toast.LENGTH_SHORT).show();
            dataInsertSQL(price6);
        } else {
            Toast.makeText(MainActivity.this, "沒中......", Toast.LENGTH_SHORT).show();
            dataInsertSQL(noPrice);//沒中
        }
    }

    private String spiltNumber(String num, int n) {
        try {
            String cutnum = num.substring(n, 8);
            return cutnum;
        } catch (Exception e) {
            return "";
        }
    }

    private void dataInsertSQL(int price) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("invNum", invData[3]);
        values.put("invDate", invData[4]);
        values.put("sellerName", invData[5]);
        values.put("invStatus", invData[6]);
        values.put("invPeriod", invData[7]);
        values.put("sellerBan", invData[8]);
        values.put("sellerAddress", invData[9]);
        values.put("invoiceTime", invData[10]);
        values.put("detail", invData[11]);
        values.put("getMoney", price);
        values.put("costMoney", getCostMoney(invDetailList));

        if (price == 0) {
            if (spIsLoaded == true) {
                playSound(2, 1);
            }
            db.insert(SqliteTableLost, null, values);
        } else {
            if (spIsLoaded == true) {
                playSound(1, 1);
            }
            db.insert(SqliteTableWin, null, values);
        }

//        for (int i = 0; i < 11; i++) {
//            Log.e("TAGQQ", invData[i] + "");
//        }
        setListView();
        setMoneyView();
    }

    private int getCostMoney(String[] invDetailList) {
        double costMoney = 0;
        for (int i = 0; i < invDetailList.length; i = i + 5) {
//          costMoney = costMoney + (Double.parseDouble(invDetailList[i + 2]) * Double.parseDouble(invDetailList[i + 3]));
            costMoney = costMoney + Double.parseDouble(invDetailList[i]);
        }

        BigDecimal bd = new BigDecimal(costMoney);
        bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);// 小數後面四位, 四捨五入
        return bd.intValue();
    }

    private void setListView() {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String[] sqlLW = {SqliteTableLost, SqliteTableWin};
        ListView[] listLW = {lvLost, lvWin};
        for (int i = 0; i < 2; i++) {
            final Cursor cursor = db.rawQuery("select _id,invNum,invDate,sellerName,invStatus,invPeriod,sellerBan,sellerAddress,invoiceTime,detail,getMoney,costMoney from " + sqlLW[i], null);

            if (cursor != null && cursor.getCount() >= 0) {
                Log.e("tag", "???" + "    " + cursor.toString());
                adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, new String[]{"invNum", "costMoney"}, new int[]{android.R.id.text1, android.R.id.text2}, 0);
                adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        if (view.getId() == android.R.id.text1) {
                            int dateIndex = cursor.getColumnIndex("invDate");
                            int getIndex = cursor.getColumnIndex("invNum");

                            String invNUM = cursor.getString(getIndex);
                            String invDATE = cursor.getString(dateIndex);
                            invDATE = String.valueOf(Integer.parseInt(invDATE.substring(0, 4)) - 1911) + "-" + invDATE.substring(4, 6) + "-";
                            TextView dateTextView = (TextView) view;
                            dateTextView.setTypeface(null, Typeface.BOLD);
                            dateTextView.setTextSize(17);
//                            dateTextView.setTypeface(Typeface.createFromAsset(getAssets()
//                                    , "fonts/word.TTF"));
                            dateTextView.setTextColor(Color.parseColor("#5f5f5f"));
                            dateTextView.setText(invDATE + invNUM.substring(2, 10));
                            return true;
                        } else if (view.getId() == android.R.id.text2) {
                            int getIndex = cursor.getColumnIndex("costMoney");
                            String cMoney = cursor.getString(getIndex);

                            TextView dateTextView = (TextView) view;

                            dateTextView.setGravity(Gravity.RIGHT);
                            dateTextView.setText(cMoney + "元");
                            dateTextView.setTextSize(16);
                            dateTextView.setTypeface(null, Typeface.BOLD);
//                            dateTextView.setTypeface(Typeface.createFromAsset(getAssets()
//                                    , "fonts/word.TTF"));
                            dateTextView.setTextColor(Color.parseColor("#2b5075"));
                            return true;
                        }
                        return false;
                    }
                });

                int sql = 2;
                if (i == 0) {
                    sql = 0;
                } else if (i == 1) {
                    sql = 1;
                }
                final int selectSql = sql;
                listLW[i].setAdapter(adapter);
                listLW[i].setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent, final View view, int position, long id) {


                        AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
//                      LayoutInflater inflater=MainActivity.this.getLayoutInflater();
//                      adb.setView(inflater.inflate(R.layout.dialog,null));

                        final View viewDialog = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog, null);
                        adb.setView(viewDialog);

//                       final Dialog dialog;

                        LinearLayout linearText = (LinearLayout) viewDialog.findViewById(R.id.LinearText);
                        ViewGroup.LayoutParams linearTextParams = linearText.getLayoutParams();
                        linearTextParams.height = screenHeightDp / 4;


                        TextView textViewData = (TextView) viewDialog.findViewById(R.id.textViewData);
                        textViewData.setHeight(screenHeightDp / 4);
//                        textViewData.setTypeface(Typeface.createFromAsset(getAssets()
//                                , "fonts/word.TTF"));
                        textViewData.setTextColor(Color.parseColor("#3b3b3b"));


                        ScrollView scView = (ScrollView) viewDialog.findViewById(R.id.scView);
                        scView.getLayoutParams().height = screenHeightDp / 4;


                        TextView textViewCost = (TextView) viewDialog.findViewById(R.id.textViewCost);
                        textViewCost.setWidth(screenWidthDp / 100 * 47);
                        textViewCost.setHeight(screenHeightDp / 15 / 10 * 6*5/4);
//                        textViewCost.setTypeface(Typeface.createFromAsset(getAssets()
//                                , "fonts/word.TTF"));
                        textViewCost.setTextColor(Color.parseColor("#404040"));

                        TextView textViewTotal = (TextView) viewDialog.findViewById(R.id.textViewTotal);
                        textViewTotal.setHeight(screenHeightDp / 16);

                        int numIndex = cursor.getColumnIndex("invNum");
                        int dateIndex = cursor.getColumnIndex("invDate");
                        int snameIndex = cursor.getColumnIndex("sellerName");
                        int statusIndex = cursor.getColumnIndex("invStatus");
                        int periodIndex = cursor.getColumnIndex("invPeriod");
                        int sbanIndex = cursor.getColumnIndex("sellerBan");
                        int saddIndex = cursor.getColumnIndex("sellerAddress");
                        int timeIndex = cursor.getColumnIndex("invoiceTime");
                        int detailIndex = cursor.getColumnIndex("detail");
                        int gmoneyIndex = cursor.getColumnIndex("getMoney");
                        int cmoneyIndex = cursor.getColumnIndex("costMoney");
                        String num = cursor.getString(numIndex);
                        String date = cursor.getString(dateIndex);
                        String sname = cursor.getString(snameIndex);
                        String status = cursor.getString(statusIndex);
                        String period = cursor.getString(periodIndex);
                        String sban = cursor.getString(sbanIndex);
                        String sadd = cursor.getString(saddIndex);
                        String t = cursor.getString(timeIndex);
                        String detailList = cursor.getString(detailIndex);
                        String gmoney = cursor.getString(gmoneyIndex);
                        String cmoney = cursor.getString(cmoneyIndex);
                        textViewData.setGravity(Gravity.CENTER_VERTICAL);
                        if (sadd.equals("")) {
                            textViewData.setText("  " + "發票號碼 : " + num + "\n" + "\n" + "  " + "發票期別 : " + period.substring(0, 3) + "年" + period.substring(3, 5) + "月" + "\n" + "\n" + "  " + "購買日期 : " +
                                    date.substring(4, 6) + "-" + date.substring(6, 8) + "  " + t + "\n" + "\n" + "  " + "商家統編 : " + sban);
                        } else {
                            textViewData.setText("  " + "發票號碼 : " + num + "\n" + "\n" + "  " + "發票期別 : " + period.substring(0, 3) + "年" + period.substring(3, 5) + "月" + "\n" + "\n" + "  " + "購買日期 : " +
                                    date.substring(4, 6) + "-" + date.substring(6, 8) + "  " + t + "\n" + "\n" + "  " + "購買地址 : " + sadd + "\n" + "  " + "商家統編 : " + sban);
                        }

                        String[] detailSpiltList = spiltInvDetail(detailList);
                        LinearLayout rllFish=(LinearLayout)viewDialog.findViewById(R.id.rllFish);
                        LinearLayout rllL = (LinearLayout) viewDialog.findViewById(R.id.rllL);
                        LinearLayout rllR = (LinearLayout) viewDialog.findViewById(R.id.rllR);
                        for (int i = 0; i < detailSpiltList.length; i = i + 5) {



                            TextView rlltextL = new TextView(MainActivity.this);

                            rlltextL.setGravity(Gravity.CENTER);
                            rlltextL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                            rlltextL.setText(detailSpiltList[i * 1 + 1] + " * " + detailSpiltList[i * 1 + 3]);
                            rlltextL.setEllipsize(TextUtils.TruncateAt.END);
                            rlltextL.setHorizontallyScrolling(true);
                            rlltextL.setTypeface(Typeface.DEFAULT_BOLD);
//                            rlltextL.setTypeface(Typeface.createFromAsset(getAssets()
//                                    , "fonts/word.TTF"));
                            rlltextL.setTextColor(Color.parseColor("#32537e"));
                            rllL.addView(rlltextL);


                            rlltextL.measure(0, 0);       //must call measure!
                            rlltextL.getMeasuredHeight(); //get height


                            ImageView imageFish = new ImageView(MainActivity.this);
                            imageFish.setImageResource(R.mipmap.datafish);
                            rllFish.addView(imageFish);
                            imageFish.getLayoutParams().height = rlltextL.getMeasuredHeight();//get 0
                            imageFish.getLayoutParams().width = screenHeightDp / 5 / 3 / 3 / 2 * 3 * 3 / 2/3*2;




                            TextView rlltextR = new TextView(MainActivity.this);
                            rlltextR.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                            rlltextR.setMaxLines(2);
                            rlltextR.setGravity(Gravity.CENTER);
                            rlltextR.setTypeface(Typeface.DEFAULT_BOLD);
//                            rlltextR.setTypeface(Typeface.createFromAsset(getAssets()
//                                    , "fonts/word.TTF"));
                            rlltextR.setTextColor(Color.parseColor("#e64572"));

                            rlltextR.setHorizontallyScrolling(true);
                            rlltextR.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            rlltextR.setText(detailSpiltList[i * 1 + 0] + "元");
                            rllR.addView(rlltextR);

                        }

                        viewDialog.measure(
                                View.MeasureSpec.makeMeasureSpec(screenWidthDp, View.MeasureSpec.AT_MOST),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                        LinearLayout linearTotal = (LinearLayout) viewDialog.findViewById(R.id.LinearTotal);
                        ViewGroup.LayoutParams linearTotalParams = linearTotal.getLayoutParams();
                        linearTotalParams.height = screenHeightDp / 16;

                        TextView textCost = (TextView) viewDialog.findViewById(R.id.textViewCost);
//                        textCost.setTypeface(Typeface.createFromAsset(getAssets()
//                                , "fonts/word.TTF"));
                        textCost.setTextColor(Color.parseColor("#404040"));


                        textViewTotal.setGravity(Gravity.CENTER);
                        textViewTotal.setMaxLines(1);
                        textViewTotal.setTextColor(Color.parseColor("#404040"));
//                        textViewTotal.setTypeface(Typeface.createFromAsset(getAssets()
//                                , "fonts/word.TTF"));
                        textViewTotal.setText(" : " + cmoney + " ");
                        Log.e("TAG", "還我長度= =" + viewDialog.getMeasuredWidth() + "//" + screenWidthDp + "//" + viewDialog.getWidth());

                        ImageView imageViewRfish = (ImageView) viewDialog.findViewById(R.id.imageView);
                        imageViewRfish.getLayoutParams().height = screenHeightDp / 5 / 3 / 3 * 2 / 3 * 2;
                        imageViewRfish.getLayoutParams().width = screenHeightDp / 5 / 3 / 3 / 2 * 3 * 3 / 2;


                        adb.setTitle("發票明細");
//                    adb.setMessage("");
                        final long idToRemove = id;
                        adb.setNeutralButton("刪除", new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                lastText = null;
                                String[] sqlLW = {SqliteTableLost, SqliteTableWin};

                                delSqlData(idToRemove, sqlLW[selectSql]);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        adb.setPositiveButton("取消", null);
                        adb.show();
//                      dialog=adb.create();
//                      dialog.show();
                    }
                });
            } else {
                Log.e("tag", "nullllllllllllllllllllllllllllllllllll?????");
            }
        }
    }

    private void setMoneyView() {
        searchLite();

        textView_cost.setText("$" + "  " + (Integer.parseInt(invMoneyData[0]) + Integer.parseInt(invMoneyData[2])));
        textView_money.setText("$" + "  " + (Integer.parseInt(invMoneyData[1]) + Integer.parseInt(invMoneyData[3])));
    }


    private void delSqlData(long id, String tableName) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + tableName + " WHERE " + "_id" + "= " + id);
        setMoneyView();
        setListView();
    }


    private String[] searchLite() {
        SQLiteDatabase db = dbhelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                // select "invNum" from SqliteTable where * order by _id
                "costMoney", "getMoney "
        };

// How you want the results sorted in the resulting Cursor
        // select _number from SqliteTable where * order by "_id"
        String sortOrder = "_id";
        String[] searchTables = {SqliteTableLost, SqliteTableWin};

        invMoneyData = new String[4];
        for (int i = 0; i < invMoneyData.length; i++) {
            invMoneyData[i] = "0";
        }

        for (int i = 0; i < 2; i++) {
            Cursor cursorMoney = db.query(
                    // select _number from SqliteTable where * order by _number
                    searchTables[i],  // The table to query
                    projection,                               // The columns to return
                    // select _number from SqliteTable where "*" order by _number
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            int rows_num = cursorMoney.getCount();    //取得資料表列數
            if (rows_num != 0) {
                String[] invCostMoney = new String[rows_num];
                String[] invGetMoney = new String[rows_num];

                cursorMoney.moveToFirst();            //將指標移至第一筆資料

                for (int j = 0; j < rows_num; j++) {

                    invCostMoney[j] = String.valueOf(cursorMoney.getInt(0));
                    invGetMoney[j] = String.valueOf(cursorMoney.getInt(1));
                    cursorMoney.moveToNext();        //將指標移至下一筆資料
                }

                for (int j = 0; j < invCostMoney.length; j++) {
                    invMoneyData[i * 2 + 0] = String.valueOf(Integer.parseInt(invMoneyData[i * 2 + 0]) + Integer.parseInt(invCostMoney[j]));
                }

                for (int j = 0; j < invGetMoney.length; j++) {
                    invMoneyData[i * 2 + 1] = String.valueOf(Integer.parseInt(invMoneyData[i * 2 + 1]) + Integer.parseInt(invGetMoney[j]));
                }
            }
            cursorMoney.close();        //關閉Cursor
        }
        db.close();    //關閉資料庫，釋放記憶體
        return invMoneyData;
    }

//    private void delDbData() {
//        SQLiteDatabase db = dbhelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
////        values.put("_number", spiltScanString);
////        values.put("_money", price);
//        String[] whereArgs = {"1"};
////        String[] whereArgs2={"2"};
////        String[] whereArgs3={"3"};
////        String[] whereArgs4={"4"};
//        db.delete(SqliteTable, "_id=?", whereArgs);
//        Log.e("tag", "delete successful");
////        searchLite();
//    }

    private boolean checkNum(String scanNumber) {
        scanNumber = scanNumber.substring(0, 10) + String.valueOf(Integer.parseInt(scanNumber.substring(10, 13)) + 1911) + scanNumber.substring(13, 17);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        final Cursor cursor = db.rawQuery("select _id,invNum,invDate from " + SqliteTableLost, null);
        cursor.moveToFirst();            //將指標移至第一筆資料
        for (int i = 0; i < cursor.getCount(); i++) {
            String invNum = cursor.getString(1);
            String invDate = cursor.getString(2);
            Log.e("TAG", "????????????" + (invNum + invDate));
            if (scanNumber.equals(invNum + invDate)) {
                Log.e("TAG", "same");
                return false;
            }
            cursor.moveToNext();        //將指標移至下一筆資料
        }

        final Cursor cursor2 = db.rawQuery("select _id,invNum,invDate from " + SqliteTableWin, null);
        cursor2.moveToFirst();            //將指標移至第一筆資料
        for (int i = 0; i < cursor2.getCount(); i++) {
            String invNum = cursor2.getString(1);
            String invDate = cursor2.getString(2);
            if (scanNumber.equals(invNum + invDate)) {
                Log.e("TAG", "same");
                return false;
            }
            cursor2.moveToNext();        //將指標移至下一筆資料
        }


        return true;
    }

    // 重新開始的觸發事件
    private final Button.OnClickListener BtnOnClickEvent = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            SQLiteDatabase db = dbhelper.getWritableDatabase();

            switch (v.getId()) {
                case R.id.btnClear_left:
                    db.execSQL("delete from " + SqliteTableLost);// DELETE ALL LIST <3
                    setListView();
                    setMoneyView();
                    break;
                case R.id.btnClear_right:
                    db.execSQL("delete from " + SqliteTableWin);// DELETE ALL LIST <3
                    setListView();
                    setMoneyView();
                    break;
            }
        }
    };
}


