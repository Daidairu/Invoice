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
 * Created by hungju.hsiao on 2017/2/23.
 */

public class GetInvoiceApi extends AsyncTask<String, Void, String[]> {


    String invoiceList[] = new String[24];

    String invoiceListFake[];
    String resultList;

    String[] numberList1;
    String[] numberList2;
    public AsyncResponse delegate = null;
    int[] correctTime;
    String[] apiApi;


    private HostnameVerifier hnv;
    private X509TrustManager xtm;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String[] doInBackground(String... params) {

//        correctTime = checkTime();
//        apiApi = new String[2];
//        apiApi[0] = String.valueOf(correctTime[2]) + String.valueOf(correctTime[3]);
//        apiApi[1] = String.valueOf(correctTime[4]) + String.valueOf(correctTime[5]);
//
//        for (int i = 0; i < 2; i++) {
//            insert(apiApi, i);
//        }

        String apiPeriod = params[0];

        if(Integer.parseInt(apiPeriod)%2!=0)
        {
            apiPeriod=String.valueOf(Integer.parseInt(apiPeriod)+1);
        }

//
//        numberList1 = new String[insertFake().length];
//        for (int i = 0; i < insertFake().length; i++) {
//            numberList1[i] = insertFake()[i];
//        }
//        return numberList1;

        return insert(apiPeriod);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String[] aVoid) {
        super.onPostExecute(aVoid);
        delegate.processFinish(aVoid);
    }


    public static String hmacSha1(String base) throws NoSuchAlgorithmException, InvalidKeyException {
        String apiKey = "NHNGQ21YZDREdE56enVEbA==";

        if (TextUtils.isEmpty(base) || TextUtils.isEmpty(apiKey)) {
            return "";
        }

        String type = "HmacSHA1";
        SecretKeySpec secret = new SecretKeySpec(apiKey.getBytes(), type);
        Mac mac = Mac.getInstance(type);
        mac.init(secret);
        byte[] digest = mac.doFinal(base.getBytes());

        return Base64.encodeToString(digest, Base64.DEFAULT);

    }

    public String[] insert(String invTermArray) {

        HttpsURLConnection httpsURLConnection = null;

        String version = "0.2";
        String action = "QryWinningList";
        String invTerm = invTermArray;
        String UUID = "64732b0b-35d8-42eb-9d23-ecaf4b57abb1";
        String appID = "EINV3201702247741";
        String signature;

        String data = ("version=" + version + "&action=" + action + "&invTerm=" + invTerm + "&appID=" + appID);

//        try{
//            signature=hmacSha1(data2);
//        }catch (Exception e)
//        {
//            signature="";
//        }

//        String data2=("version=" + version + "&action=" + action + "&invTerm=" + invTerm + "&appID=" + appID+"&signature="+signature);
        Log.e("TAG", "" + "" + data);
        byte[] post = data.getBytes(Charset.forName("UTF-8"));
        Log.e("TAG", "" + "" + post.toString());

//        try {
//
//            URL url = new URL("https://einvoice.nat.gov.tw/PB2CAPIVAN/invapp/InvApp");
//            String token = "rbkY34HnL...";
//            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
//            urlConnection.setSSLSocketFactory(getSSLSocketFactory());
//            urlConnection.setHostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
////                        return true;
//                    HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
//                    return hv.verify("your_domain.com", session);
//                }
//            });
//            urlConnection.setRequestProperty("Authorization", "Bearer " + token);
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setRequestProperty("Content- Type", "application/x-www-form-urlencoded");
//            urlConnection.setRequestProperty("charset", "utf-8");
//            urlConnection.setRequestProperty("Content-Length",
//                    Integer.toString(post.length));
//
//            urlConnection.setDoOutput(true);
//            urlConnection.connect();
//            InputStream inputStream;
//            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                inputStream = urlConnection.getErrorStream();
//            } else {
//                inputStream = urlConnection.getInputStream();
//            }
//            return String.valueOf(urlConnection.getResponseCode()) + " " + urlConnection.getResponseMessage() + "\r\n" + parseStream(inputStream);
//        } catch (Exception e) {
//            Log.e("247",e.toString()+"");
//        }

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
            Log.e("TAG","resultList:"+resultList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }

//
//            DataOutputStream dataOutputStream = new DataOutputStream
//                    (httpsURLConnection.getOutputStream());
//
//            dataOutputStream.write(post);
//
//            dataOutputStream.flush();
//            dataOutputStream.close();
//
//            int rCode = httpsURLConnection.getResponseCode();
//            if (rCode == 200) {
//
//            } else {
//                Log.e("TAG", /*"访问失败"*/ "" + rCode);
//                Log.e("TAG", httpsURLConnection.getResponseCode() + "");
//            }
//        } catch (Exception e) {
//            httpsURLConnection.disconnect();
//            Log.e("TAG", "Output:" + e.toString());
//        }
//
//        BufferedReader in = null;
//
//        try {
//            in = new BufferedReader(new InputStreamReader
//                    (httpsURLConnection.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//
//            resultList = response.toString();
//            Log.e("TAG", "" + resultList);
//
//        } catch (IOException e) {
//            Log.e("TAG", "Input:" + e.toString());
//        }



        /*
SQL 結果有多筆資料時使用JSONArray
只有一筆資料時直接建立JSONObject物件
     JSONObject jsonData = new JSONObject(result);
*/
        try {
            JSONObject jsonData = new JSONObject(resultList);
//            JSONArray jsonArray = new JSONArray(resultList);


            for(int i=0;i<invoiceList.length;i++)
            {
                invoiceList[0]="0";//防NULL  先給初值
            }

            invoiceList[0] = jsonData.getString("superPrizeNo");
            invoiceList[1] = jsonData.getString("spcPrizeNo");
            invoiceList[4] = jsonData.getString("firstPrizeNo1");
            invoiceList[5] = jsonData.getString("firstPrizeNo2");
            invoiceList[6] = jsonData.getString("firstPrizeNo3");

            invoiceList[14] = jsonData.getString("sixthPrizeNo1");
            invoiceList[15] = jsonData.getString("sixthPrizeNo2");
            invoiceList[16] = jsonData.getString("sixthPrizeNo3");


            invoiceList[20] = jsonData.getString("v");
            invoiceList[21] = jsonData.getString("code");
            invoiceList[22] = jsonData.getString("msg");
            invoiceList[23] = jsonData.getString("invoYm");
            invoiceList[2] = jsonData.getString("spcPrizeNo2");
            invoiceList[3] = jsonData.getString("spcPrizeNo3");
            invoiceList[7] = jsonData.getString("firstPrizeNo4");
            invoiceList[8] = jsonData.getString("firstPrizeNo5");
            invoiceList[9] = jsonData.getString("firstPrizeNo6");
            invoiceList[10] = jsonData.getString("firstPrizeNo7");
            invoiceList[11] = jsonData.getString("firstPrizeNo8");
            invoiceList[12] = jsonData.getString("firstPrizeNo9");
            invoiceList[13] = jsonData.getString("firstPrizeNo10");
            invoiceList[17] = jsonData.getString("sixthPrizeNo4");
            invoiceList[18] = jsonData.getString("sixthPrizeNo5");
            invoiceList[19] = jsonData.getString("sixthPrizeNo6");

            return invoiceList;
        } catch (Exception e) {
            Log.e("111111111111", "aaaa" + invoiceList[21]);
            return invoiceList;
        }
    }

    private int[] checkTime() {
        //算當前時間得到月   偶數月/x-2/x-4   奇數月/x-3/x-5  2月以前要到前一年
        String dateformat = "yyyyMM";
        Calendar mCal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(dateformat);
        String today = df.format(mCal.getTime());

        int[] nowTime = new int[6];
        nowTime[0] = Integer.parseInt(today.substring(0, 4));
        nowTime[1] = Integer.parseInt(today.substring(4, 6));

        if (nowTime[1] % 2 == 0) {
            //偶數
            if (nowTime[1] <= 4) {
                if (nowTime[1] <= 2) {
                    nowTime[2] = nowTime[0] - 1912;
                    nowTime[3] = 10;
                    nowTime[4] = nowTime[0] - 1912;
                    nowTime[5] = 12;
                } else {
                    nowTime[2] = nowTime[0] - 1912;
                    nowTime[3] = 12;
                    nowTime[4] = nowTime[0] - 1912;
                    nowTime[5] = 2;
                }
            } else {
                nowTime[2] = nowTime[0] - 1911;
                nowTime[3] = nowTime[1] - 4;
                nowTime[4] = nowTime[0] - 1911;
                nowTime[5] = nowTime[1] - 2;
            }

        } else if (nowTime[1] <= 5) {
            if (nowTime[1] <= 3) {
                if (nowTime[1] <= 1) {
                    nowTime[2] = nowTime[0] - 1912;
                    nowTime[3] = 8;
                    nowTime[4] = nowTime[0] - 1912;
                    nowTime[5] = 10;
                } else {
                    nowTime[2] = nowTime[0] - 1912;
                    nowTime[3] = 10;
                    nowTime[4] = nowTime[0] - 1912;
                    nowTime[5] = 12;
                }

            } else {
                nowTime[2] = nowTime[0] - 1912;
                nowTime[3] = 12;
                nowTime[4] = nowTime[0] - 1911;
                nowTime[5] = 2;
            }
        } else {
            nowTime[2] = nowTime[0] - 1911;
            nowTime[3] = nowTime[1] - 5;
            nowTime[4] = nowTime[0] - 1911;
            nowTime[5] = nowTime[1] - 3;
        }

        return nowTime;
    }


//    public GetInvoiceApi() {
////初始化X509TrustManager中的SSLCoNtext
//        SSLContext sslCoNtext = null;
//
//        try {
//            sslCoNtext = SSLContext.getInstance("TLS");
//            X509TrustManager[] xtmArray = new X509TrustManager[]{xtm};
//            sslCoNtext.init(null, xtmArray, new java.security.SecureRandom());
//        } catch (GeneralSecurityException gse) {
//
//        }
//
////為javax.net.ssl.HttpsURLConnection設置預設的SocketFactory和HostnameVerifier
//        if (sslCoNtext != null) {
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslCoNtext.getSocketFactory());
//        }
//
//        HttpsURLConnection.setDefaultHostnameVerifier(hnv);
//    }

//    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
//        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
//        return new TrustManager[]{
//                new X509TrustManager() {
//                    public X509Certificate[] getAcceptedIssuers() {
//                        return originalTrustManager.getAcceptedIssuers();
//                    }
//
//                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
//                        try {
//                            originalTrustManager.checkClientTrusted(certs, authType);
//                        } catch (CertificateException ignored) {
//                        }
//                    }
//
//                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
//                        try {
//                            originalTrustManager.checkServerTrusted(certs, authType);
//                        } catch (CertificateException ignored) {
//                        }
//                    }
//                }
//        };
//    }

//    private SSLSocketFactory getSSLSocketFactory() {
//        try {
//            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//            InputStream caInput = getResources().openRawResource(R.raw.your_cert);
//            Certificate ca = cf.generateCertificate(caInput);
//            caInput.close();
//
//            KeyStore keyStore = KeyStore.getInstance("BKS");
//            keyStore.load(null, null);
//            keyStore.setCertificateEntry("ca", ca);
//
//            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//            tmf.init(keyStore);
//
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, getWrappedTrustManagers(tmf.getTrustManagers()), null);
//
//            return sslContext.getSocketFactory();
//        } catch (Exception e) {
//            return HttpsURLConnection.getDefaultSSLSocketFactory();
//        }
//    }
}


