# UICrawler（基于Appium的App UI遍历工具）
![](https://github.com/lgxqf/UICrawler/blob/master/doc/demo.gif)

## 1.0 版 功能描述 
* 支持Andoird/iOS
* 支持Monkey功能及以下事件
    * 随机点击
    * 特殊位置点击(需在文件中进行配置)
    * 任意方向及长度的滑动
    * 触发Home键(Android Only)
    * 重启app
* 基于深度优先的原则，点击UI上的元素。当发现Crash时会提供操作步骤截图及相应的Log.(Android提供logcat的log. iOS提供系统log及crash相应的ips文件)
* 元素遍历结束或按下Ctrl + C键会生成HTML测试报告。测试报告中汇集了测试统计、log及截图的相应信息  
* 同一个元素只会点击一次(白名单中的元素可能会被多次点击)
* 每次点击都会生一个一截图，截图中被点击的位置会用红点标注，方便查找点击位置
![](https://github.com/lgxqf/UICrawler/blob/master/doc/click-point.png)

## 待开发功能 1.1版 预计6下旬月release

* 支持滑动等更多动作
* 根据执行步骤重现bug

## 配置文件可配置项
* 截图数量控制
* 黑名单
* 白名单
* 限制遍历深度
* 限制点击次数
* 遍历界面元素的xpath
* 自动登录的用户名和密码及相应的UI元素ID 

### 下载Jar包
[UICrawler.jar](https://pan.baidu.com/s/12cCTp1nQ6DSk9OPuFt_uEw)
### 下载配置文件
[config.yml](https://github.com/lgxqf/UICrawler/blob/master/config.yml) 
### 根据待测试App修改配置文件中下列各项的值，其它值用默认值即可 [详情见 Config.md 配置文件介绍](doc/Config.md)
  #### Android
  * ANDROID_PACKAGE
  * ANDROID_MAIN_ACTIVITY
  #### iOS
  * IOS_BUNDLE_ID
  * IOS_BUNDLE_NAME
  * IOS_IPA_NAME
### Monkey配置项可选， 请见 [Monkey配置](https://github.com/lgxqf/UICrawler/blob/master/doc/Config.md#monkey%E5%8A%9F%E8%83%BD%E9%85%8D%E7%BD%AE)  
### 启动appium
```bash
appium --session-override -p 4723
-p 设定appium server的端口 , 不加参数默认为4723
```

### 运行UICrawler元素遍历(必须有yml配置文件)
```aidl
java -jar UICrawler.jar -f config.yml -u udid -t 4723
-u 指定设备udid
-t 指定appium server的端口（此项为可选项，默认值是4723)
```

### 运行UICrawler Monkey
```aidl
java -jar UICrawler.jar -f config.yml -u udid -t 4723 -m
```

### 查看支持的参数
```aidl
java -jar UICrawler.jar -h
```

### 一些常用命令
```
#查看设备udid
Android:
  adb devices
iOS:
  instruments -s  devices
  idevice_id -l
  
#Android 查看apk 和 Main activity
  ./aapt dump badging "apk"  | grep launchable-activity
  aapt 通常在android sdk的 build-tools目录下
  windows中将grep换成findstr
  "apk"是apk文件路径
```

## 注意事项
* 同时运行多台ios设备时要每台设备要设置不同的IOS_WDA_PORT ： 8001-8888
* Android7的手机必须安装Uiautomator2 server 的两个apk(安装deskstop版appium,初次连接appium会自动安装), 也可进入到apk目录下通过adb安装
* Android6及以下的手机不要安装Uiautomator2 server的APK

## Known issue
* iOS 不支持wkwebview元素获取 https://github.com/appium/appium/issues/9408


## 参考内容
* Yaml 文件格式 https://blog.csdn.net/michaelhan3/article/details/69664932 
* Android API level 与version对应关系 http://www.cnblogs.com/jinglecode/p/7753107.html  
    CMD: adb -s uuid shell getprop | grep version.sdk
* iPhone分辨率与坐标系 https://www.paintcodeapp.com/news/ultimate-guide-to-iphone-resolutions
* IOS 分析ips文件  
    * https://blog.csdn.net/rainbowfactory/article/details/73332735 
    * https://jingyan.baidu.com/article/e6c8503c4a0182e54f1a1804.html 
    * https://www.cnblogs.com/someonelikeyou/p/6379861.html
* https://github.com/baozhida/libimobiledevice
        

# 更多细节
* [配置文件介绍](doc/Config.md)
* [环境搭建](doc/Environment.md)

# 测试报告 
![](https://github.com/lgxqf/UICrawler/blob/master/doc/Test-Report.png)