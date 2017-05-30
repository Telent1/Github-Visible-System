package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;

import org.springframework.stereotype.Repository;

@Repository
public class RequestUtil{
    /**
     * @param requestUrl
     * @param requestMethod (GET or POST)
     * @param param (?=?&?=?)
     * @return sBuffer
     */
    public String request(String requestUrl, String requestMethod, String param, String mediaType, Proxy proxy) throws Exception{
        URL url = new URL(requestUrl);
        InputStream inputStream;
        HttpURLConnection conn;
        if(proxy!=null) {
            System.out.println("using proxy " + proxy.address() + " to request " + requestUrl);
            conn = (HttpURLConnection)url.openConnection(proxy);
        }
        else conn = (HttpURLConnection)url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod(requestMethod);
        conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"); 
        if(null!=param){
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(param);
            writer.flush();
        }
        if(null!=mediaType){
            conn.setRequestProperty("Accept", "application/vnd.github.cloak-preview");
        }
        conn.connect();
        //System.out.println(conn.getResponseCode());
        if(conn.getResponseCode()!=HttpURLConnection.HTTP_OK
        &&conn.getResponseCode()!=HttpURLConnection.HTTP_ACCEPTED
        &&conn.getResponseCode()!=HttpURLConnection.HTTP_CREATED){
            inputStream = conn.getErrorStream();
            throw new Exception(String.valueOf(conn.getResponseCode()));
        }
        else inputStream = conn.getInputStream();
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
        String str = null;
        StringBuffer sBuffer = new StringBuffer();
        while((str = bufferedReader.readLine())!=null){
            sBuffer.append(str + "\r\n");
        }
        bufferedReader.close();
        inputStream.close();
        conn.disconnect();
        return sBuffer.toString();
    }

    public String request(String requestUrl) throws Exception{
        return request(requestUrl,"GET",null,null,null);
    }

    public Proxy getProxy(String ip, int port){
        SocketAddress addr = new InetSocketAddress(ip, port); 
        return new Proxy(Proxy.Type.HTTP, addr);
    }
}