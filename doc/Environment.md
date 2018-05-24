# 环境搭建
请参考https://www.cnblogs.com/yuer011/p/8067650.html

## 安装Java 8 JDK

## 安装node
* https://nodejs.org/
```aidl
#安装appium 1.8.0
http://appium.io/

#检查appium环境正确性
appium-doctor
```


## iOS
### 安装XCODE 9
### Install Brew https://brew.sh
```
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```
### Install carthage
```
brew install carthage
```
### 安装 libimobile
* https://www.jianshu.com/p/6423610d3293
```aidl
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

## Android
### 安装UiAutomator2 apk
### 安装Android SDK




