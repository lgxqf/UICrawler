##TODO


##待开发功能
* 去除Android7 android:id/statusBarBackground
* 定义遍历优先级
* 判断是不是同一个UI by tolerance



## 优化
* 提高执行速度  把一些xpath换成iosPredicate & uiautomator
* 截图
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

## 遇到的问题(坑)
* Xcode 9 运行 iOS11 没问题
* Appium iOS左划问题


## 支持的平台
* Android 4-7 Android 4-5 未测试
* IOS 10-11 IOS 10未测试


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

