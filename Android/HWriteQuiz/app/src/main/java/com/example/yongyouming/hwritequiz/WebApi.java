package com.example.yongyouming.hwritequiz;

import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 409LAB00 on 2017/11/10.
 */

public class WebApi {


    public final String target="http://"+ConfigFile.IP+"/api/";//Controller/Method


    public String POST(String urlString,String Paras)//paras = para1=xxx&para2=yyy.....(in a string)
    {
        String data =Paras;
        int httpCode;
        HttpURLConnection con=null;
        String url = target+urlString;
        try{
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
            OutputStream wr = con.getOutputStream();
            wr.write(data.getBytes());
            wr.flush();
            wr.close();
            httpCode=con.getResponseCode();
            if(con.getResponseCode()==200)
            {
                InputStream inputStream     = con.getInputStream();
                BufferedReader bufferedReader  = new BufferedReader( new InputStreamReader(inputStream) );

                String tempStr;
                StringBuffer stringBuffer = new StringBuffer();
                while( ( tempStr = bufferedReader.readLine() ) != null ) {
                    stringBuffer.append( tempStr );
                }

                bufferedReader.close();
                inputStream.close();



                // 網頁內容字串
                String responseString = stringBuffer.toString();
                System.out.print(responseString);
                return responseString.replace("\"","");
            }
            else{return httpCode+"";}
        }catch(Exception e)
        {e.printStackTrace();
            return "Error";
        }
        finally {
            if(con!=null)
            {
                con.disconnect();
            }

        }

    }

    public String GET(String urlString)
    {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(target+urlString);

            connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
            // 設定開啟自動轉址
            connection.setInstanceFollowRedirects(true);

            if( connection.getResponseCode() == 200 ){
                InputStream inputStream     = connection.getInputStream();
                BufferedReader bufferedReader  = new BufferedReader( new InputStreamReader(inputStream) );

                String tempStr;
                StringBuilder stringBuilder = new StringBuilder();

                while( ( tempStr = bufferedReader.readLine() ) != null ) {
                    stringBuilder.append( tempStr );
                }

                bufferedReader.close();
                inputStream.close();

                return  stringBuilder.toString();

            }
            else
                return "Error Code : " +connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return "Unknown Error";
        }
        finally {
            if(connection!=null)
            {
                connection.disconnect();
            }

        }
    }

    public static void startInternet(){
        StrictMode
                .setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        //.detectDiskReads()
                        //.detectDiskWrites()
                        //.detectNetwork()   // or .detectAll() for all detectable problems
                        //.penaltyLog()
                        .build());

    }
}
