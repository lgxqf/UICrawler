package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ma Yi on 2017/4/26.
 */
public class MyLogger {
    public static Logger log = LoggerFactory.getLogger(MyLogger.class);

    public static void info(String str){
        if(null != str){
            log.info(str);
        }
    }

    public static void info(Object obj){
        log.info(String.valueOf(obj));
    }

    public static String getMethodName() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        String methodName = e.getMethodName();
        return "===== Method : " + methodName + "   ";
    }

}
