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
CRAWLER="${JENKINS_ROOT}/uicrawler/uicrawler.jar"
#ADB=/Users/tal/Project/AndroidSDK/platform-tools/adb
ADB=/root/android_sdk/platform-tools/adb


checkResult(){
    pwd && ls 
    isCrash=`find . -iname report.html | xargs grep "No crash found" | wc -l`

    echo $isCrash
    if [ "$isCrash" -eq 1 ]; then
        echo "equal"
        exit 0
    else
        echo "not equal"
        exit 1
    fi
}

killAppium(){
    ps -ax | grep appium | awk '{print $1}' | xargs kill -9
}

killTask(){
    ps -ax | grep appium | awk '{print $1}' | xargs kill -9
    ps -ax | grep uicrawler.jar | awk '{print $1}' | xargs kill -9
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

if [ "$ACTION" == "kill-task" ]; then
    killTask
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

startAppium(){
    COUNT=$1

    if [ ! $COUNT ]; then
        COUNT=3
        echo "appium instance count is $COUNT"
    fi

    APP_PORT=4721
    WDALOCALPORT=8099

    for((i=0;i<$COUNT;i++))
    do
        APP_PORT=$[APP_PORT + 2]
        BP_PORT=$[APP_PORT + 1]
        WDALOCALPORT=$[WDALOCALPORT + 1]
        echo "start appium at port $APP_PORT, BP_PORT $BP_PORT, WDALOCALPORT $WDALOCALPORT"
        appium --session-override  -p  $APP_PORT -bp $BP_PORT --webdriveragent-port $WDALOCALPORT &
    done
}



downloadApp(){
    APK_URL=""
    BUNDLE_URL=""

    case "$APP" in
        "xes")
            APK_URL="http://10.1.12.3/Android/Apk/current/xesapp.apk"
            BUNDLE_URL="http://10.1.12.3/iOS/current/XesApp.ipa"
            ;;

        "new")
            echo "new"
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

    curl "$DOWNLOAD_URL" -o $FILE --progress
}

triggerNightTestingRecord(){
    if [ "$1" == "start" ]; then
        echo ""
        curl -d "" http://10.1.12.206:8081/server/app/night/testing/start/$UDID/$BUILD_NUMBER
        echo "\n night testing start message posted.  http://10.1.12.206:8081/server/app/night/testing/start/$UDID/$BUILD_NUMBER\n"
    else
        curl -d "" http://10.1.12.206:8081/server/app/night/testing/end/$UDID/$BUILD_NUMBER
        echo "\n night testing stop  message posted. http://10.1.12.206:8081/server/app/night/testing/end/$UDID/$BUILD_NUMBER\n"
    fi
}

runTest(){
    triggerNightTestingRecord "start"

    echo "Start testing on device " $UDID
    #cd $JENKINS_ROOT/uicrawler
    #java -jar $CRAWLER -f config/$CONFIG -t $APPIUM_PORT  -u $UDID

}

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

if [ $ACTION == "run" ]; then
    if [ -z "$3" ] || [ -z "$4" ] || [ -z "$5" ] || [ -z "$6" ]; then
        echo "Please input valid value for udid , os , config file, appium port"
        exit 1
    fi

    APPNAME=

    echo "Udid : " $UDID
    echo "OS : " $OS

    echo "installing app..."

    CMD=$ADB
    EXT=".apk"

    if [ "$OS" == "android" ] ; then
        echo "Android Device is : "  $UDID
        $CMD -s $UDID install -r ${APP_ROOT_DIR}${APP}"$EXT"
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