package util;

import org.apache.commons.collections.map.ListOrderedMap;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class ReportUtil {
    public static org.slf4j.Logger log = LoggerFactory.getLogger(ReportUtil.class);

    public static void setSummaryMap(Map<String, String> summaryMap) {
        ReportUtil.summaryMap = summaryMap;
    }

    @SuppressWarnings("unchecked")
    private static Map<String,String> summaryMap = new ListOrderedMap();

    public static void setDetailedList(List<ArrayList<String>> detailedList) {
        ReportUtil.detailedList = detailedList;
    }

    public static void setClickedList(List<ArrayList<String>> clickedList) {
        ReportUtil.clickedList = clickedList;
    }

    private static List<ArrayList<String>> clickedList = new ArrayList<>();
    private static List<ArrayList<String>> detailedList = new ArrayList<>();
    private static StringBuilder builder;

    public static void generateReport(File file){
        builder = new StringBuilder();
        String meta = "utf-8";
        if(Util.isWin()){
            meta = "gbk";
        }

        builder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n" + "<html>\n" + "<head>\n" );
        builder.append("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset="+ meta +"\" />\n");
        builder.append("    <title>UICrawler Report</title>\n" );
        addCSS();
        builder.append("</head>\n" + "<body>\n" + "<br/>\n");
        builder.append("<h1 align=\"center\">执行结果信息汇总 Testing result summary</h1>\n");

        addSummaryTable();
        addDetailedTable();

        if(Util.isAndroid()) {
            addClickedTable();
        }

        builder.append("</body>\n" + "</html>\n");
        Util.writeFile(file,builder.toString());
    }

    private static void addCSS(){
        builder.append("    <style>\n" +
                "        td.left{\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "    </style>\n");
    }

    private static void addSummaryTable(){
        builder.append("<br/><h2>基本信息 - Basic information</h2>\n");
        builder.append("<table width=\"100%\" border=\"1\" align=\"center\" cellspacing=\"1\">\n");
        builder.append(" <tbody>\n");
        for(String key: summaryMap.keySet()) {
            builder.append(
                    "    <tr>\n" +
                            "        <td class=\"left\" width=\"50%\">"+key+"</td>\n" +
                            "        <td width=\"50%\">"+summaryMap.get(key)+"</td>\n" +
                            "    </tr>\n");
        }
        builder.append(" </tbody>\n</table>\n");
    }

    private static void addDetailedTable(){
        generateTable("Crash信息 - Crash information",detailedList,"No crash found during testing.");

    }

    private static void addClickedTable(){
        generateTable("Activity Clicked List",clickedList,"No Activity is clicked.");
    }

    private static void generateTable(String header,List<ArrayList<String>> rowList, String info ){
        builder.append("<br/>\n<h2>" +header +"</h2>\n");
        builder.append("<table width=\"100%\" border=\"1\" align=\"center\" cellspacing=\"1\">\n");
        builder.append(" <tbody>\n");

        for(ArrayList<String> list : rowList){
            builder.append("    <tr align=\"left\">\n" );
            if(list.get(0).equals("HEAD")){
                list.remove("HEAD");
                for(String str : list){
                    builder.append("        <th class=\"left\" >"+str+"</th>\n" );
                }
            }else{
                for(String str : list){
                    builder.append("        <td>"+str+"</td>\n");
                }
            }
            builder.append("    </tr>\n");
        }

        if(rowList.size()== 0){
            builder.append("        <td class=\"left\">"+info+"</td>\n");
        }

        builder.append(" </tbody>\n</table>\n");
    }

}
