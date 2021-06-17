#TODO 


##待开发功能
* 去除Android7 android:id/statusBarBackground
* 判断是不是同一个UI by tolerance


## 优化
* 提高执行速度  把一些xpath换成 iosPredicate & uiautomator
* 提高截图速度
```
    if(UDID.contains("-")) {
        try {
            Process pp = Runtime.getRuntime().exec("xcrun simctl io booted screenshot --type=jpeg " + screenshotPath);
            pp.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } else {
        try {
            Runtime.getRuntime().exec("idevicescreenshot -u " + UDID + " " + screenshotPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```


# 环境搭建
请参考https://www.cnblogs.com/yuer011/p/8067650.html

## 安装Java 8 JDK
https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
配置JAVA_HOME环境变量
## 安装appium

```aidl
appium (桌面版)
http://appium.io/

appium环境检查工具
appium-doctor
```

## Android
### 安装Android SDK 
### 配置ANDROID_HOME环境变量

## iOS
### Install XCode
### Install Brew 
```
https://brew.sh
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```
### Install Carthage
```
brew install carthage
```

### 安装 libimobile
```aidl
https://www.jianshu.com/p/6423610d3293
brew uninstall ideviceinstaller
brew uninstall libimobiledevice
brew install --HEAD libimobiledevice
brew link --overwrite libimobiledevice
brew install ideviceinstaller
brew link --overwrite ideviceinstaller
```
### 安装ios-deploy
```
npm install -g ios-deploy
```



