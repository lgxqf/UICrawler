import io.appium.java_client.AppiumDriver;
import org.apache.commons.cli.*;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.*;

import java.io.File;
import java.util.*;


public class Crawler {
    public static Logger log = LoggerFactory.getLogger(Crawler.class);
    private static Date beginTime = new Date();
    private static String logName;
    private static Map<String, String> summaryMap;
    private static boolean isMonkey = false;
    private static List<String> crashFileList;
    private static boolean isReported = false;
    private static String udid;

    private static class CtrlCHandler extends Thread {
        @Override
        public void run() {
            log.info("Pressing Ctrl + C...\n");
            PerfUtil.closeDataFile();

            if (!isMonkeyMode()) {
                XPathUtil.showFailure();
            }

            if (!isReported) {
                log.info("Handling Ctrl + C shut down event...");
                executeTask();
                log.info("Everything is done. Both video and report are generated.");
            }
        }
    }

    private static void executeTask() {
        generateReport();

        if (ConfigUtil.isGenerateVideo()) {
            generateVideo();
        }

        isReported = true;
    }

    private static void generateVideo() {
        log.info("Method : generateVideo()");

        List<String> fullList = Util.getFileList(ConfigUtil.getScreenShotDir(), ".png", true);

        //Generate full video
        try {
            log.info("Generating full video file, please wait...");
            if (fullList != null) {
                PictureUtil.picToVideo(ConfigUtil.getRootDir() + File.separator + "testing_steps.mp4", fullList);
            }
        } catch (Exception e) {
            log.error("Fail to generate full.mp4 file");
            e.printStackTrace();
        }

        //Generate crash video
        try {
            if (crashFileList.size() > 0) {

                log.info("Generating " + crashFileList.size() + " crash video files...");
                List<String> fullListWithoutPath = Util.getFileList(ConfigUtil.getScreenShotDir(), ".png", false);
                log.info("Generating crash video file, please wait...");
                int beginIndex = 0;

                int size = 0;
                if (fullListWithoutPath != null) {
                    size = fullListWithoutPath.size();
                }
                for (String crashStep : crashFileList) {
                    int endIndex = Objects.requireNonNull(fullListWithoutPath).indexOf(crashStep);
                    //显示一张crash后的照片
                    if (-1 != endIndex && endIndex <= size) {
                        if (endIndex + 1 < size) {
                            endIndex++;
                        }
                        String fileName = ConfigUtil.getRootDir() + File.separator + "crash" + File.separator + fullListWithoutPath.get(endIndex - 1).replace(".png", ".mp4");
                        if (fullList != null) {
                            PictureUtil.picToVideo(fileName, fullList.subList(beginIndex, endIndex + 1));
                        }
                        beginIndex = endIndex + 1;
                    }
                }
            }

        } catch (Exception e) {
            log.error("Fail to generate crash.mp4 file");
            e.printStackTrace();
        }

        log.info("Complete generating video file!");
    }

    private static void initReport() {
        summaryMap.put("手机系统 - Mobile operating system", Driver.getPlatformName());
        summaryMap.put("系统版本 - OS version", Driver.getPlatformVersion());
        summaryMap.put("设备UUID - Device UUID", udid);
        summaryMap.put("测试开始时间 - Testing start time", Util.getTimeString(beginTime));

        if (Util.isAndroid()) {
            summaryMap.put("包名 - Package name", ConfigUtil.getPackageName());
            summaryMap.put("主活动 - Main Activity", ConfigUtil.getActivityName());
        } else {
            summaryMap.put("Bundle", ConfigUtil.getBundleId());
            summaryMap.put("Bundle Name", ConfigUtil.getIOSBundleName());
            summaryMap.put("Bundle IPA Name", ConfigUtil.getIPAName());
        }
    }

    private static void generateReport() {
        log.info("Method : generateReport()");

        int index = 0;
        List<ArrayList<String>> detailedList = new ArrayList<>();
        List<ArrayList<String>> clickedList = new ArrayList<>();
        String crashDir = ConfigUtil.getRootDir() + File.separator + "crash" + File.separator;
        log.info("Crash dir is " + crashDir);
        //String crashDir = "./crash" + File.separator;
        crashFileList = Util.getFileList(crashDir);
        int crashCount = crashFileList.size();

        summaryMap.put("总执行时间 - Total running time", Util.timeDifference(beginTime.getTime(), new Date().getTime()));
        if (!isMonkey) {
            summaryMap.put("元素点击数量 - Element clicked count", String.valueOf(XPathUtil.getClickCount()));
        }
        summaryMap.put("系统日志 - System log", "<a href=\"" + logName + "\">" + logName + "</a>");
        summaryMap.put("Crash数量 - Crash count", String.valueOf(crashCount));

        if (isMonkey) {
            summaryMap.put("测试类型 - Test type", "Monkey随机测试");
        } else {
            summaryMap.put("测试类型 - Test type", "UI元素遍历");
        }

        if (crashCount > 0) {
            log.info("Crash count is : " + crashCount);
            int picCount = (int) ConfigUtil.getLongValue(ConfigUtil.CRASH_PIC_COUNT);
            ArrayList<String> headerRow = new ArrayList<>();
            headerRow.add("HEAD");
            headerRow.add("NO");

            for (int i = 1; i < picCount; i++) {
                headerRow.add("Step " + i);
            }
            headerRow.add("Crash");
            headerRow.add("Video");

            detailedList.add(headerRow);
        }

        for (String item : crashFileList) {
            ArrayList<String> row = new ArrayList<>();
            index++;
            row.add("<img width=\"100px\">" + index + "</img>");
            List<String> crashStepList = getCrashSteps(item);

            for (String step : crashStepList) {
                row.add("<a href=\"" + crashDir + step + "\">"
                        + " <img width=\"50%\" src=\"" + crashDir + step + "\"/>"
                        + "</a>");
                String dest = crashDir + step;
                String src = ConfigUtil.getRootDir() + File.separator + ConfigUtil.SCREEN_SHOT + File.separator + step;
                Util.copyFile(new File(src), new File(dest));
            }

            item = item.replace(".png", ".mp4");
            row.add("<a href=\"" + crashDir + item + "\"/>" + item + "</a>");
            detailedList.add(row);
        }

        int clickedActivityCount = XPathUtil.getClickedActivityMap().size();

        if (clickedActivityCount > 0) {
            log.info("Clicked Activity count is " + clickedActivityCount);

            ArrayList<String> headerRow = new ArrayList<>();
            headerRow.add("HEAD");
            headerRow.add("Activity");
            headerRow.add("Click Count");
            clickedList.add(headerRow);

            Map<String, Long> map = XPathUtil.getClickedActivityMap();
            for (String activity : map.keySet()) {
                ArrayList<String> row = new ArrayList<>();
                row.add(activity);
                row.add(map.get(activity).toString());
                clickedList.add(row);
            }
        }

        int monkeyClickCount = XPathUtil.getMonkeyClickedMap().size();

        if (monkeyClickCount > 0) {
            Map<String, Map<String, Long>> monkeyMap = XPathUtil.getMonkeyClickedMap();

            ArrayList<String> headerRow = new ArrayList<>();
            headerRow.add("HEAD");
            headerRow.add("Activity");
            headerRow.add("Detail");

            clickedList.add(headerRow);

            for (String newActivity : monkeyMap.keySet()) {
                ArrayList<String> row = new ArrayList<>();
                row.add(newActivity);
                row.add(monkeyMap.get(newActivity).toString());
                clickedList.add(row);
            }
        }

        String reportName = ConfigUtil.getRootDir() + File.separator + "report.html";
        File report = new File(reportName);

        ReportUtil.setSummaryMap(summaryMap);
        ReportUtil.setDetailedList(detailedList);
        ReportUtil.setClickedActivityList(clickedList);
        ReportUtil.setClickedElementMap(XPathUtil.getClickedElementMap());
        ReportUtil.generateReport(report);
        log.info("\n\n------------------------------Test report : " + reportName + "\n\n");
    }

    private static List<String> getCrashSteps(String crashName) {
        int index = 0;
        int picCount = (int) ConfigUtil.getLongValue(ConfigUtil.CRASH_PIC_COUNT);
        List<String> stepList = new ArrayList<>();
        List<String> screenshotList = Util.getFileList(ConfigUtil.getScreenShotDir(), ".png", false);

        if (screenshotList != null) {
            index = screenshotList.indexOf(crashName);
        }

        if (-1 == index) {
            log.error("Fail to find crash file " + crashName + " in screenshot folder");
            return stepList;
        }

        int length = 0;
        if (screenshotList != null) {
            length = screenshotList.size();
        }

        int startIndex = index - picCount + 2;
        int endIndex = index + 2;

        log.info("Init StartIndex " + startIndex + " Init EndIndex " + endIndex);

        if (startIndex < 0) {
            while (startIndex != 0) {
                startIndex++;
            }
        }

        if (endIndex >= length) {
            endIndex = index + 1;
        }

        log.info("StartIndex " + startIndex + " EndIndex " + endIndex);
        if (screenshotList != null) {
            stepList = screenshotList.subList(startIndex, endIndex);
        }

        log.info(stepList.toString());

        return stepList;
    }

    private static boolean isMonkeyMode() {
        return isMonkey;
    }

    //    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        String version = "2.3 ---Jan/07/2020";

        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("h", "help", false, "Print this usage information");
        options.addOption("a", "activity", true, "Android package's main activity");
        options.addOption("p", "package", true, "Android package name");
        options.addOption("b", "ios_bundle_id", true, "iOS bundle id");
        options.addOption("n", "ios_bundle_name", false, "ios bundle");
        options.addOption("z", "wda_port", true, "wda port for ios");


        options.addOption("e", "performance", false, "record performance data");
        options.addOption("f", "config", true, "Yaml config  file");
        options.addOption("i", "ignore crash", false, "Ignore crash");
        options.addOption("m", "run monkey", false, "run in monkey mode");
        options.addOption("o", "output_dir", true, "output directory");
        options.addOption("s", "server_ip", true, "appium server ip");
        options.addOption("t", "port", true, "appium port");
        options.addOption("u", "udid", true, "device serial");
        options.addOption("v", "version", false, "build version with date");
        options.addOption("w", "wechat_mode", false, "run in wechat mode");
        options.addOption("x", "write_to_db", false, "write performance data to influxDB");

        //options.addOption("c", "run_count", true, "Maximum click count");
        //options.addOption("d", "crawler_ui_depth", true, "Maximum Crawler UI Depth");
        //options.addOption("l", "loop count", true, "Crawler loop count");
        //options.addOption("r", "crawler_running_time", true, "minutes of running crawler ");

        CommandLine commandLine = parser.parse(options, args);
        String configFile;

        if (commandLine.hasOption('h')) {
            log.info(
                    "\n" +
                            "    -a  Android package's main activity\n" +
                            "    -b  iOS bundle ID\n" +
                            //"    -c  Maximum click count \n" +
                            //"    -d  Maximum crawler UI depth \n" +
                            "    -e  Record performance data \n" +
                            "    -f  Yaml config  file\n" +
                            "    -h  Print this usage information\n" +
                            "    -i  Ignore crash\n" +
                            //"    -l  Execution loop count\n" +
                            "    -m  Run monkey\n" +
                            "    -o  Output directory" +
                            "    -p  Android package name\n" +
                            //"    -r  Crawler running time\n" +
                            "    -s  Appium server ip\n" +
                            "    -t  Appium port\n" +
                            "    -u  Device serial\n" +
                            "    -v  Version\n" +
                            "    -z  WDA port for ios\n" +
                            "    -x  Write data to influx db\n" +
                            "    -w  run in wechat mode"
            );
            return;
        }

        if (commandLine.hasOption("u")) {
            udid = commandLine.getOptionValue('u');
        } else {
            log.info("Please provide device serial");
            return;
        }

        if (commandLine.hasOption("w")) {
            ConfigUtil.setRunInWechatMode(true);
        }

        if (commandLine.hasOption("f")) {
            configFile = System.getProperty("user.dir") + File.separator + commandLine.getOptionValue('f');
            configFile = configFile.trim();
            log.info(configFile);
        } else {
            configFile = System.getProperty("user.dir") + File.separator + "config.yml";
            log.info("using default config file : config.yml");
        }

        configFile = configFile.trim();

        if (commandLine.hasOption("v")) {
            log.info(version);
            //log.info("PC platform : " +  System.getProperty("os.name"));
            log.info("System File Encoding: " + System.getProperty("file.encoding"));
            return;
        }

        if (commandLine.hasOption("o")) {
            String outputDir = commandLine.getOptionValue('o').trim();

            if (Util.isDir(outputDir)) {
                ConfigUtil.setOutputDir(outputDir);
            } else {
                log.info("output directory " + outputDir + " is not a directory or doesn't exist");
                return;
            }
        }

        int loopCount = 1;

        if (commandLine.hasOption("l")) {
            try {
                loopCount = Integer.parseInt(commandLine.getOptionValue("l"));
            } catch (Exception e) {
                log.error("Fail to get loop count, set loop count to 1");
            }
        }

        log.info("Crawler loop count is " + loopCount);

        //根据设定的次数 开始循环遍历
        for (int i = 0; i < loopCount; i++) {

            log.info("Crawler loop No is " + (i + 1));

            summaryMap = new ListOrderedMap<>();
            isReported = false;
            beginTime = new Date();

            //初始化配置文件
            ConfigUtil.initialize(configFile, udid);

            if (commandLine.hasOption("s")) {
                ConfigUtil.setServerIp(commandLine.getOptionValue('s').trim());
            }

            if (commandLine.hasOption("a")) {
                ConfigUtil.setActivityName(commandLine.getOptionValue('a'));
            }

            if (commandLine.hasOption("b")) {
                ConfigUtil.setBundleId(commandLine.getOptionValue('b'));
            }

            if (commandLine.hasOption("c")) {
                ConfigUtil.setClickCount(Long.parseLong(commandLine.getOptionValue('c')));
            }

            if (commandLine.hasOption("d")) {
                ConfigUtil.setCrawlerRunningTime(commandLine.getOptionValue('d'));
            }

            boolean writeToDB = false;

            if (commandLine.hasOption("x")) {
                writeToDB = true;
                DBUtil.initialize();
            }

            if (commandLine.hasOption("i")) {
                ConfigUtil.setIgnoreCrash(true);
            }

            if (commandLine.hasOption("p")) {
                ConfigUtil.setPackageName(commandLine.getOptionValue('p'));
            }

            if (commandLine.hasOption("n")) {
                ConfigUtil.setIOSBundleName(commandLine.getOptionValue('n'));
            }

            //下面的值会修改配置文件初始化后得到的默认值
            if (commandLine.hasOption("r")) {
                ConfigUtil.setCrawlerRunningTime(commandLine.getOptionValue('r'));
            }

            if (commandLine.hasOption("t")) {
                ConfigUtil.setPort(commandLine.getOptionValue('t'));
            }

            if (commandLine.hasOption("z")) {
                ConfigUtil.setWdaPort(commandLine.getOptionValue('z'));
            }
            Util.createDir(ConfigUtil.getRootDir());

            AppiumDriver<WebElement> appiumDriver;

            //启动Appium
            if (Util.isAndroid(udid)) {
                appiumDriver = Driver.prepareForAppiumAndroid(ConfigUtil.getPackageName(), ConfigUtil.getActivityName(), ConfigUtil.getUdid(), ConfigUtil.getPort());
            } else {
                appiumDriver = Driver.prepareForAppiumIOS(ConfigUtil.getBundleId(), ConfigUtil.getUdid(), ConfigUtil.getPort(), ConfigUtil.getWdaPort());
                Util.cleanCrashData(udid, ConfigUtil.getStringValue(ConfigUtil.IOS_IPA_NAME));
            }

            if (appiumDriver == null) {
                log.error("Fail to start appium server!");
                return;
            }

            logName = Driver.startLogRecord();

            initReport();
            Runtime.getRuntime().addShutdownHook(new CtrlCHandler());

            try {
                //等待App完全启动,否则遍历不到元素
                Driver.sleep(15);

                if (commandLine.hasOption("e") && Util.isAndroid()) {
                    PerfUtil.writeDataToFileAsync(writeToDB);
                }

                if (commandLine.hasOption("e") && !Util.isAndroid()) {
                    Driver.startPerfRecordiOS();
                }

                //初始化Xpath内容
                XPathUtil.initialize(udid);

                String pageSource = Driver.getPageSource();

                if (commandLine.hasOption("m")) {
                    //开始Monkey测试
                    log.info("----------------Run in monkey mode-----------");
                    isMonkey = true;
                    XPathUtil.monkey(pageSource);
                } else {
                    //开始遍历UI
                    log.info("------------Run in crawler mode----------------");
                    XPathUtil.getNodesFromFile(pageSource, 0);
                    //Driver.getPageSource();
                    //String xpath = "//android.widget.Button[@text=\"允许\" and @scrollable=\"false\" and @resource-id=\"android:id/button1\" and @password=\"false\" and @package=\"com.lbe.security.miui\" and @long-clickable=\"false\" and @index=\"1\" and @focused=\"false\" and @focusable=\"true\" and @enabled=\"true\" and @clickable=\"true\" and @class=\"android.widget.Button\" and @checkable=\"false\"]";
                    //Driver.findElement(By.xpath(xpath));
                }

                log.info("------------------------------Complete testing. Please refer to report.html for detailed information.----------------");
                log.info("------------------------------Press Ctrl + C to generate video file and report.----------------");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("------------------------------ERROR. Testing stopped unexpectedly!!!!----------------");
            }

            Driver.sleep(5);

            PerfUtil.stop();

            if (commandLine.hasOption("e") && !Util.isAndroid()) {
                Driver.stopPerfRecordiOS();
            }

            executeTask();

            if (isMonkeyMode()) {
                log.info("Complete testing in monkey mode");
                break;
            }

            Driver.driver.quit();
        }
    }
}


//    private static void showClickedItems(){
//        HashSet<String> clickedSet = XPathUtil.getSet();
//        log.info(clickedSet.size() + " elements are clicked");
//
//        for(String str: clickedSet){
//            log.info(str);
//        }
//
//        log.info("==============list end==========");
//    }
