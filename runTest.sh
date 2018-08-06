#!/bin/bash

#Usage
#runTest.sh curl start/stop udid buildNumber 
#runTest.sh appium 5
#runTest.sh kill-appium
#runTest.sh download xes ios
#runTest.sh run xes ios udid 4723 config-xes.yml buildNumber

#Sequqnce for action run only. For other actoin, sequency may change.
ACTION=$1
APP=$2
OS=$3
UDID=$4
APPIUM_PORT=$5
CONFIG=$6
BUILD_NUMBER=$7


JENKINS_ROOT="/data/jenkins/"
APP_ROOT_DIR="${JENKINS_ROOT}/uicrawler/app/"
#TOOL="${JENKINS_ROOT}/uicrawler/uicrawler.jar"

if [ "$APP" == "weclass" ]; then
    UDID=$3
    BUILD_NUMBER=$4
    APP_ROOT_DIR="${JENKINS_ROOT}/uiautomation/app/"
fi

#Linux Agent
#ADB=/root/android_sdk/platform-tools/adb

#Mac Mini
ADB=/Users/mg/Project/AndroidSDK/platform-tools/adb

TIMESTAMP(){
    echo $(date "+%Y-%m-%d %H:%M:%S")
}

proc_num(){
	num=`ps -ef | grep $1 | grep -v grep | wc -l`
	return $num
}

proc_id(){
	pid=`ps -ef | grep $1 | grep -v grep | awk '{print $2}'`
	echo "pid" ${pid}
}

checkProcess(){
	proc_name=startJenkins  #进程名
	file_name=/data/service/monitor/logs/$proc_name.log #日志路径

	while true
	do
		proc_num proc_name
		number=$?
		echo $(TIMESTAMP):  $proc_name" 进程数" ${number} >> $file_name
		if [ $number -eq 0 ]                                    # 判断进程是否存在
		then
			echo '钉钉通知开始' >> $file_name
			curl 'https://oapi.dingtalk.com/robot/send?access_token=98e3b91cc5161833ff2733e672509d6d5a8278deee1d2cfda914a40569fbd966' \
			   -H 'Content-Type: application/json' \
			   -d '
			  {"msgtype": "text",
			    "text": {
			        "content": "警告！！生产环境应用 '$proc_name' 停止运行! 请检查!"
			     }
			  }'
			sleep 5
			echo '钉钉通知结束' >> $file_name
			break
		fi

		sleep 30
	done
}

checkResult(){
    pwd && ls 
    isCrash=`find . -iname report.html | xargs grep "No crash found" | wc -l`

    RET=0
    echo $isCrash

    if [ "$isCrash" -eq 1 ]; then
        echo "No Crash found"
    else
        echo "Crash found!"
        RET=1
    fi

    exit $RET
}

checkWCResult(){
    isCrash=`find . -iname *failure.png | wc -l`

    RET=0
    echo $isCrash

    if [ "$isCrash" -eq 1 ]; then
        echo "failure found"
        RET=1
    else
        echo "no failure found"
    fi

    exit $RET
}

killAppium(){
    ps -ax | grep appium | awk '{print $1}' | xargs kill -9
}

killTask(){
    ps -ax | grep appium | awk '{print $1}' | xargs kill -9
    ps -ax | grep uicrawler.jar | awk '{print $1}' | xargs kill -9
}

killJava(){
    ps -ax | grep uicrawler.jar | awk '{print $1}' | xargs kill -9
}

startAppium(){
    COUNT=$1

    if [ ! $COUNT ]; then
        COUNT=3
        echo "appium instance count is $COUNT"
    fi

    APP_PORT=4721
    WDALOCALPORT=8099
    CHROME_PORT=9514

    for((i=0;i<$COUNT;i++))
    do
        APP_PORT=$[APP_PORT + 2]
        BP_PORT=$[APP_PORT + 1]
        WDALOCALPORT=$[WDALOCALPORT + 1]
        CHROME_PORT=$[CHROME_PORT + 1]

        #echo "start appium at port $APP_PORT, BP_PORT $BP_PORT, WDALOCALPORT $WDALOCALPORT"
        #appium --session-override  -p  $APP_PORT -bp $BP_PORT --webdriveragent-port $WDALOCALPORT &

        #echo "start appium at port $APP_PORT, BP_PORT $BP_PORT, CHROME_PORT $CHROME_PORT"
        appium --session-override  -p  $APP_PORT -bp $BP_PORT --chromedriver-port $CHROME_PORT &

    done
}


downloadApp(){
    APK_URL=""
    BUNDLE_URL=""

    case "$APP" in
        "xes")
            APK_URL="http://10.1.12.3/Android/Apk/current/xesapp.apk"
            APK_URL="http://10.1.12.3/Android/Apk/current/xesapp.apk/app-gamma.apk"
            BUNDLE_URL="http://10.1.12.3/iOS/current/XesApp.ipa"
            ;;

        "weclass")
            echo "weclass"

            ;;

        *)
            echo "default value is xes"
            APK_URL="http://10.1.12.3/Android/Apk/current/xesapp.apk"
            BUNDLE_URL="http://10.1.12.3/iOS/current/XesApp.ipa"
            ;;
    esac

    DOWNLOAD_URL=$APK_URL
    EXT=".apk"

    if [ "$OS" == "ios" ] ; then
        DOWNLOAD_URL=$BUNDLE_URL
        EXT=".ipa"
    fi

    mkdir $APP_ROOT_DIR
    FILE=${APP_ROOT_DIR}${APP}"$EXT"
    echo  $DOWNLOAD_URL  "====>" $FILE

    if [ "$APP" == "xes" ]; then
        curl "$DOWNLOAD_URL" -o $FILE --progress
    fi
}

triggerNightTestingRecord(){
    SERVER_IP=10.33.10.11

    if [ "$1" == "start" ]; then
        curl -d "" http://$SERVER_IP:8081/server/app/night/testing/start/$UDID/$BUILD_NUMBER
        echo "\n night testing start message posted.  http://$SERVER_IP:8081/server/app/night/testing/start/$UDID/$BUILD_NUMBER\n"
    else
        curl -d "" http://$SERVER_IP:8081/server/app/night/testing/end/$UDID/$BUILD_NUMBER
        echo "\n night testing stop  message posted. http://$SERVER_IP:8081/server/app/night/testing/end/$UDID/$BUILD_NUMBER\n"
    fi
}

runTest(){
    triggerNightTestingRecord "start"

    echo "Start testing on device ..." $UDID
    #cd $JENKINS_ROOT/uicrawler
    #java -jar $CRAWLER -f config/$CONFIG -t $APPIUM_PORT  -u $UDID

}


if [ -z "$1" ]; then
    echo "./runTest.sh curl start/stop udid buildNumber"
    echo "./runTest.sh appium 5 --- start 5 appium server"
    echo "./runTest.sh kill-appium --- kill all the appium process"
    echo "./runTest.sh download xes android  --- download android app for xes"
    echo "./runTest.sh run xes ios udid 4723 config_xes.yml buildNumber --- run test for xes on ios with udid and appium port appium_port"
    exit 0
fi

if [ "$ACTION" == "check" ]; then
    checkResult
fi

if [ "$ACTION" == "checkWC" ]; then
    checkWCResult
fi

if [ "$ACTION" == "kill-task" ]; then
    killTask
    exit 0
fi

if [ "$ACTION" == "kill-java" ]; then
    killJava
    exit 0
fi

if [ "$ACTION" == "kill-appium" ]; then
    killAppium
    exit 0
fi

if [ -z "$1" ] || [ -z "$2" ]; then
    echo "Please input valid value for action and app name , appium count or curl operation"
    exit 1
fi

echo $CONFIG
echo "Action  : " $ACTION
echo "App name or Appium count : " $APP


if [ "$ACTION" == "curl" ]; then
    CURL_OPTION=$2
    UDID=$3
    BUILD_NUMBER=$4
    if [ "$CURL_OPTION" == "start" ]; then
        triggerNightTestingRecord "start"
    else
        triggerNightTestingRecord "stop"
    fi

    exit 0
fi


if [ "$ACTION" == "appium" ]; then
    echo "start appium"
    startAppium $2
    exit 0
fi

if [ "$ACTION" == "download" ]; then
    echo "download"
    downloadApp "$APP"
    exit 0
fi

if [ $ACTION == "run" ] || [ $ACTION == "install" ]; then
#    if [ -z "$3" ] || [ -z "$4" ] || [ -z "$5" ] || [ -z "$6" ]; then
#        echo "Please input valid value for udid , os , config file, appium port"
#        exit 1
#    fi
    if [ -z "$2" ] || [ -z "$3" ]; then
        echo "Please input valid value for udid, app "
        exit 1
    fi

    echo "Udid : " $UDID

    echo "installing app..."

    EXT=".apk"

    LEN=${#UDID}

    if [ $LEN -lt 20 ]; then
        echo "Android Device is : "  $UDID
        #if [ "$APP" == "xes" ]; then
        #    echo "Uninstalling app..."
        #    $CMD -s $UDID uninstall com.xes.jazhanghui.activity
        #fi
        echo "Installing app..."
        CMD=$ADB
        $CMD -s $UDID install -r -d ${APP_ROOT_DIR}${APP}"$EXT"
    else
        echo "iOS Device is : "  $UDID
        EXT=".ipa"
        CMD=/usr/local/bin/ios-deploy
        $CMD -d --bundle ${APP_ROOT_DIR}${APP}"$EXT" -i $UDID
    fi

    runTest

    exit 0
fi

exit 0