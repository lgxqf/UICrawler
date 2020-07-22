package util;

import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class PerfUtil {
    public static org.slf4j.Logger log = LoggerFactory.getLogger(PerfUtil.class);
    private static volatile boolean  isRunning = true;
    private static final String nativeKey = "Native Heap";
    private static final String dalKey = "Dalvik Heap";
    private static final String totalKey = "TOTAL";
    private static final String tab = "    ";
    private static String packageName;
    private static String udid;
    private static String grep;
    private static String name;
    private static boolean enableCmdOutput = false;
    private static BufferedOutputStream bos = null;

    public static void closeDataFile(){
        isRunning = false;

        try {
            if (bos != null) {
                bos.flush();
                bos.close();
                bos = null;
            }
        }catch (Exception e){
            log.error("Fail to write performance data.");
        }
    }

    public static void stop(){
        isRunning = false;
    }

    public static void initialize(){
        packageName = ConfigUtil.getPackageName();
        udid = ConfigUtil.getUdid();
        grep = Util.getGrep();

        name = packageName;

        if(packageName.length() > 14){
            name = packageName.substring(0,14);
        }
    }

    private static Map<String,String> getMemInfo(){

        Map<String,String> map = new HashMap<>();

        String res = Util.exeCmd("adb -s " + udid + " shell dumpsys meminfo " + packageName , enableCmdOutput);

        if(res.contains("No process found for")){
            log.error("process " + ConfigUtil.getPackageName() + " is not running. Get no memeory info");
            return map;
        }

        String nativeMem = getMemString(res,nativeKey);

        String dalvikMem = getMemString(res,dalKey);

        String totalMem = getMemString(res,totalKey);

        map.put(nativeKey,nativeMem);
        map.put(dalKey,dalvikMem);
        map.put(totalKey,totalMem);

        return map;
    }

    private static String getMemString(String output,String key){
        int index = output.indexOf(key) + key.length();

        return output.substring(index , index + 9).trim();
    }

    private static String getCPUInfo(){
        String info;
        //String cmd = "adb -s " + udid + " shell dumpsys cpuinfo " + packageName + " | " + grep + " "+ packageName;
        String cmd = "adb -s " + udid + "  shell top -n 1 | " + grep + " " + name;

        String res = Util.exeCmd(cmd, enableCmdOutput);

        if(res.isEmpty()){
            log.error("Process " + packageName + " has no cpu info");
            return "";
        }

        String []val = res.split(" ");

        int CPU_INDEX = 9;
        int index = 0;

        for( int i = 0; i < val.length ; i ++){

            if(!val[i].isEmpty()){
                index ++;
            }

            if(index == CPU_INDEX){
                index = i;
                break;
            }
        }

        info = val[index];

        //log.info("cpu :" + info);
        return info;
    }

    private static void writeDataToFile(boolean writeToDb){
        initialize();

        String filePath = ConfigUtil.getRootDir() + File.separator + "perf_data.txt";

        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            bos = new BufferedOutputStream(fos);
            String header =  "TIME" + tab + tab+tab + nativeKey + tab + dalKey + tab + totalKey + tab + "CPU \n";
            bos.write(header.getBytes(), 0, header.getBytes().length);
            String tableName = "tb_"+ String.valueOf(String.valueOf(System.currentTimeMillis()));

            while (isRunning) {
                //String time = String.valueOf(System.currentTimeMillis());
                Long timeStamp = System.currentTimeMillis();  //获取当前时间戳
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date(Long.parseLong(String.valueOf(timeStamp))));

                Map<String,String> memInfo = getMemInfo();
                String cpuInfo = getCPUInfo();
                memInfo.put("cpu",cpuInfo);

                if(memInfo.size() < 2){
                    continue;
                }

                String info = time + tab + memInfo.get(nativeKey) + tab + memInfo.get(dalKey) + tab + memInfo.get(totalKey) + tab + cpuInfo + "\n";

                StringBuilder dbRecord = new StringBuilder();

                for(String key:memInfo.keySet()){
                    dbRecord.append(key.replace(" ","_"));
                    dbRecord.append("=");
                    dbRecord.append(memInfo.get(key));
                    dbRecord.append(",");
                }
                dbRecord.append(time);

                String record = dbRecord.toString();
                record = record.replace("," + time, " "+time);
                record = tableName + " " + record;

                if(writeToDb){
                    DBUtil.writeData(record);
                }

                if(ConfigUtil.isPerLogEnabled()){
                    if(writeToDb){
                        log.info("DB Record : " + record);
                    }else {
                        log.info("Performance Info === "+ info);
                    }
                }

                bos.write(info.getBytes(), 0, info.getBytes().length);
                Driver.sleep(0.5);
                bos.flush();
            }
        }catch (Exception e){
            if(ConfigUtil.isPerLogEnabled()){
                log.error("Error found while writing performance data.");
                e.printStackTrace();
            }
        }finally {
            closeDataFile();
        }
    }

    public static Thread writeDataToFileAsync(boolean writeToDb){
        Runnable newRunnable = () -> {
            writeDataToFile(writeToDb);
        };

        Thread thread = new Thread(newRunnable);
        //thread.setDaemon(true);
        thread.start();

        return thread;
    }

    public static void main(String args[]){
        String file = System.getProperty("user.dir") + File.separator + "config/config.yml";
        ConfigUtil.initialize(file,"SJE0217B29005225");

        writeDataToFileAsync(false);

        //String res = "5912 u0_a229      10 -10 1.9G 193M 120M S  6.8   5.1  27:47.97 com.xes.jazhang+";
        int i = 10;

        while (i > 0){
            i--;
            Driver.sleep(1);
        }

        isRunning = false;
    }
}



        /*
        int indexEnd = res.indexOf(packageName) - 1;
        info = res.substring(0,indexEnd);
        info = info.split(" ")[0];
        info = res.substring(indexEnd - 30 , indexEnd);

        indexEnd = info.indexOf("%");ad
        info = info.substring(0,indexEnd + 1);
        int indexBegin = info.indexOf("\n");
        info = info.substring(indexBegin + 1 , indexEnd).trim();
        */