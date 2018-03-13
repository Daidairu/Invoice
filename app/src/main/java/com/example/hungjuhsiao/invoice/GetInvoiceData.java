package com.example.hungjuhsiao.invoice;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.example.hungjuhsiao.invoice.BuildConfig.DEBUG;

/**
 * Created by hungju.hsiao on 2017/3/23.
 */

public class GetInvoiceData extends AsyncTask<String, Void, String[]> {

    String invoiceData[] = new String[12];

    String resultList;

    public AsyncResponse delegate = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String[] doInBackground(String... params) {

        String version = params[0];
        String type= params[1];
        String invNum= params[2];
        String action= params[3];
        String generation= params[4];
        String invTerm= params[5];
        String invDate= params[6];
        String encrypt= params[7];
        String sellerID= params[8];
        String UUID= params[9];
        String randomNumber= params[10];
        String appID= params[11];

        String data = ("version=" + version + "&type=" + type + "&invNum=" + invNum + "&action=" + action+
                "&generation=" + generation + "&invTerm=" + invTerm + "&invDate=" + invDate + "&encrypt=" + encrypt+
                "&sellerID=" + sellerID + "&UUID=" + UUID + "&randomNumber=" + randomNumber + "&appID=" + appID);
        Log.e("TAG", "" + "" + data);

        return getApiData(data);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String[] aVoid) {
        super.onPostExecute(aVoid);
        delegate.processFinishInvData(aVoid);
    }

    public String[] getApiData(String data) {

        HttpsURLConnection httpsURLConnection = null;

//        try{
//            signature=hmacSha1(data2);
//        }catch (Exception e)
//        {
//            signature="";
//        }

        byte[] post = data.getBytes(Charset.forName("UTF-8"));
        Log.e("TAG", "" + "" + post.toString());

        try {
            URL url = new URL("https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp");

            //Use MyX509TrustManager
//            TrustManager[] trustMyCerts = new TrustManager[] { new MyX509TrustManager()};

            //Trust All
            TrustManager[] trustMyCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }};

            //Implement  HostnameVerifier
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    if (DEBUG) {
                        System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                    }
                    return urlHostName.equalsIgnoreCase(session.getPeerHost());
                }
            };

            //Initial SSLContext
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustMyCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod("POST");
//            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-Length", Integer.toString(post.length));
            DataOutputStream dataOs = new DataOutputStream(con.getOutputStream());
//            dataOs.writeBytes("version=" + version + "&action=" + action + "&invTerm=" + invTerm + "&appID=" + appID);
            dataOs.write(post);
            dataOs.flush();
            dataOs.close();

            if (DEBUG) {
                System.out.println("Response Code : " + con.getResponseCode());
                System.out.println("Cipher Suite : " + con.getCipherSuite());
                System.out.println("\n");

                Certificate[] certs = con.getServerCertificates();
                for (Certificate cert : certs) {
                    System.out.println("Cert Type : " + cert.getType());
                    System.out.println("Cert Hash Code : " + cert.hashCode());
                    System.out.println("Cert Public Key Algorithm : " + cert.getPublicKey().getAlgorithm());
                    System.out.println("Cert Public Key Format : " + cert.getPublicKey().getFormat());
                    System.out.println("\n");
                }
            }

            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
                response.append(inputLine);
            }
            in.close();
            resultList = response.toString();
            Log.e("TAG","gid line184"+response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        for(int i=0;i<invoiceData.length;i++)
        {
            invoiceData[i]="";
        }
        /*
SQL 結果有多筆資料時使用JSONArray
只有一筆資料時直接建立JSONObject物件
*/
        try {
            JSONObject jsonData = new JSONObject(resultList);
//            JSONArray jsonArray = new JSONArray(resultList);

            invoiceData[0] = jsonData.getString("v");
            invoiceData[1] = jsonData.getString("code");
            invoiceData[2] = jsonData.getString("msg");
            invoiceData[3] = jsonData.getString("invNum");
            invoiceData[4] = jsonData.getString("invDate");
            invoiceData[5] = jsonData.getString("sellerName");
            invoiceData[6] = jsonData.getString("invStatus");
            invoiceData[7] = jsonData.getString("invPeriod");
            invoiceData[8] = jsonData.getString("sellerBan");
            invoiceData[10] = jsonData.getString("invoiceTime");
            invoiceData[11] = jsonData.getString("details");


            invoiceData[9] = jsonData.getString("sellerAddress");//有時候發票沒有這個內容

        } catch (Exception e) {
            Log.e("111111111111", "aaaa" + invoiceData[0]);
        }
        return invoiceData;
    }
}


