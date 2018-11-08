package util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;


public class DBUtil {
    public static Logger log = LoggerFactory.getLogger(DBUtil.class);

    private static String serverIP ="localhost";
    private static String port ="8086";
    private static String host ;
    private static String query;
    private static String write;
    private static String dbName = "";

    public static void initialize(){
        port = ConfigUtil.getStringValue(ConfigUtil.DB_PORT);
        serverIP = ConfigUtil.getStringValue(ConfigUtil.DB_IP);

        host = "http://"+ serverIP + ":"+port;
        query = host+ "/query";
        write = host + "/write?db=";

        if(port == null){
            port = "8086";
        }

        if(serverIP == null){
            serverIP = "localhost";
        }

        dbName = "db_" + ConfigUtil.getUdid();
        createDB(dbName);
        log.info("influx db host is " + host);
    }

    public static void createDB(String name){
        log.info("Creating database " + name);
        //curl -i -XPOST http://localhost:8086/query --data-urlencode "q=CREATE DATABASE mydb"
        sendPost(query,"q=CREATE DATABASE " + name);
        dbName = name;
    }

    public static void writeData(String name,String data){
        //curl -XPOST "http://localhost:8086/write?db=mydb" -d 'cpu,host=server01,region=uswest load=42 1434055562000000000'
        sendPost(write+name,data);
    }

    public static void writeData(String data){
        //curl -XPOST "http://localhost:8086/write?db=mydb" -d 'cpu,host=server01,region=uswest load=42 1434055562000000000'
        sendPost(write+dbName,data);
    }

    public static void sendPost( String url, String data) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            String urlParameters = data;

            // Send post request
            con.setDoOutput(true);
//            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//            wr.writeBytes(urlParameters);
//            wr.flush();
//            wr.close();
            con.setDoInput(true);

            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(con.getOutputStream());
            // 发送请求参数
            printWriter.write(data);//post的参数 xx=xx&yy=yy
            // flush输出流的缓冲
            printWriter.flush();

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if(ConfigUtil.isDbLogEnabled()) {
                log.info("\nSending 'POST' request to URL : " + url + "\nPost parameters : " + urlParameters + "\nResponse Code : " + responseCode);
                log.info(response.toString());
            }
        }catch (Exception e){
            if(ConfigUtil.isDbLogEnabled()) {
                log.error("Fail to send request to DB");
                e.printStackTrace();
            }
        }
    }
}
