package com.example.hungjuhsiao.invoice;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hungju.hsiao on 2017/2/22.
 */

public class DbHelper extends SQLiteOpenHelper {

    private final static int _DBVersion = 1; //<-- 版本
    private final static String _DBName = "SampleList.db";  //<-- db name
//    private final static String _TableName = "Invoice"; //<-- table name
     private String _TableName; //<-- table name

    private final static String _TableNameWin = "InvoiceWin"; //<-- table name
    private final static String _TableNameLost = "InvoiceLost"; //<-- table name




    public DbHelper(Context context) {
        super(context, _DBName, null, _DBVersion);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQLWin = "CREATE TABLE IF NOT EXISTS " + _TableNameWin + "( " +
        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "invNum VARCHAR(15), " + "invDate VARCHAR(10), "+ "sellerName VARCHAR(30), " +
                "invStatus VARCHAR(10), "+"invPeriod VARCHAR(10), "+"sellerBan VARCHAR(10), "+
                "sellerAddress VARCHAR(40), "+"invoiceTime VARCHAR(15), "+
                "detail VARCHAR(255), "+
        "getMoney int(30)," + "costMoney int(30)" +
        ");";

        final String SQLLost = "CREATE TABLE IF NOT EXISTS " + _TableNameLost + "( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "invNum VARCHAR(15), " + "invDate VARCHAR(10), "+ "sellerName VARCHAR(30), " +
                "invStatus VARCHAR(10), "+"invPeriod VARCHAR(10), "+"sellerBan VARCHAR(10), "+
                "sellerAddress VARCHAR(40), "+"invoiceTime VARCHAR(15), "+
                "detail VARCHAR(255), "+
                "getMoney int(30)," + "costMoney int(30)" +
                ");";
        db.execSQL(SQLWin);
        db.execSQL(SQLLost);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// TODO Auto-generated method stub
        final String SQL = "DROP TABLE " + _TableName;
        db.execSQL(SQL);

    }
}



