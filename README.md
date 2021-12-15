# UICrawler
## 基于Appium的App UI遍历 & Monkey 工具


* 针对个人和公司提供有偿UI自动化技术、接口自动化技术、接口mock技术等培训及测试工具定制开发
  * QQ 技术交流群 ： 728183683
  
* 环境搭建及基本使用说明
  * 环境搭建(感谢网友harsayer倾力之作)： https://testerhome.com/topics/14490  
  * JDK 1.8: https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
  * Appium v1.20.2: http://appium.io/
  * Java-client 7.5.1 : https://search.maven.org/search?q=g:io.appium%20AND%20a:java-client
  
![](https://github.com/lgxqf/UICrawler/blob/master/doc/pic/picToMov.gif)

## 关于如何发挥测试工具的价值
* 用不同的账号登录app运行测试，因为每个账号可能看到的内容不一样
* 用不同的手机系统版本运行测试，尽可能做到iOS 9-11, Android 4-8 都覆盖到，如果资源有限无法同时运行多台设备，每天可以选择一两个系统版的手机去运行测试
* 用不同的手机运行测试，尤其安卓碎片化严重，手机厂商多，有的问题只有特定手机才能发现
* 关于测试运行的频度，只要有代码改动建议至少一天一次，下班时运行，第二天看结果 


## 2.0 版 功能描述 

### 1.UI遍历及以下功能 Android/iOS 
* 基于深度优先的原则，点击UI上的元素。当发现Crash时会提供操作步骤截图及相应的Log.
*
       Android提供logcat的log. 
       iOS提供系统log及crash相应的ips文件
* 元素遍历结束或按下Ctrl + C键会生成HTML测试报告。测试报告中汇集了测试统计、log及截图的相应信息  
* 同一个元素只会点击一次(白名单中的元素可能会被多次点击)
* 支持对输入框的文本输入(需在文件中进行配置 INPUT_TEXT_LIST)
* 统计每个Activity点击数量(Android)
* 支持滑动动作
* 支持根据关键字、包名、Activity的名字、控件类型触发Back key(Android)
* 支持自动登录时的自定义操作：点击、拖拽、文本输入。 详见Config.yml中LOGIN_ELEMENTS部分内容
* 黑名单支持XPath

### 2.Monkey功能及以下事件 Android/iOS 
* 随机位置点击
* 通过黑名单控制不想点击的区域
* 特殊位置点击(需在文件中进行配置)
* 特殊位置长按10秒(需在文件中进行配置)
* 任意方向及长度的滑动
* 触发Home键(Android Only)
* 触发Back键(Android Only)
* 重启app
* 及以下手势操作(位置随机)
*
         双击
         双指放大
         双指缩小
         拖拽


### 3.微信小程序
* 微信小程序 Monkey (iOS & Android)
* 微信小程序 UI遍历  (Android only)


### 4.其它功能
* 运行时间限制
* 每次点击都会生一个一截图，截图中被点击的位置会用红点标注，方便查找点击位置
* 当检查到Crash时，为每个Crash提供单独的操作步骤截图和mp4格式的视频文件
* 生成整体操作步骤视频，方便重现发现的问题
* 性能数据采集，执行时添加-e参数
*        Android : 每秒采集一次CPU和Memory数据 生成perf_data.txt并写放到influxDB（需单添加-x参数，且influxDB要单独安装）
*        iOS: 要求以appium --session-override --relaxed-security 启动appium, 之后会生成XCode instrument能直接读取的性能数据 详见 https://appiumpro.com/editions/12


### 5.待开发功能
* 优化执行速度，如截图生成，点击等
* 优化报告显示
* 优化视频生成
* 为配置项默认值，无配置文件也能运行
* 更改demo为Alipay
* 报告中增加每个activity中click失败和成功的次数统计



## 运行工具

### 1.下载Jar包
[UICrawler.jar](https://pan.baidu.com/s/1mNci6SWNHPuLj_mvrfgIbg)

### 2.下载配置文件
[config.yml](https://github.com/lgxqf/UICrawler/blob/master/config.yml) 

### 3.根据待测试App修改配置文件中下列各项的值 [详情见 Config.md](doc/Config.md)
  #### Android
  * ANDROID_PACKAGE
  * ANDROID_MAIN_ACTIVITY
  #### iOS
  * IOS_BUNDLE_ID
  * IOS_BUNDLE_NAME
  * IOS_IPA_NAME
  #### Monkey配置项可选， 详情见 [Monkey配置](https://github.com/lgxqf/UICrawler/blob/master/doc/Config.md#monkey%E5%8A%9F%E8%83%BD%E9%85%8D%E7%BD%AE)  

### 4.启动appium
```bash
appium --session-override
-p 设定appium server的端口 , 不加参数默认为4723
```

### 5.1 运行 UI遍历
```aidl
java -jar UICrawler.jar -u udid -f config.yml
-u 指定设备udid
-t 指定appium server的端口（此项为可选项，默认值是4723)
-f 指定yml配置文件 若无此参数 默认为config.yml 
```

### 5.2 运行 Monkey
```aidl
java -jar UICrawler.jar -u udid -m
```

### 5.3 运行微信小程序测试 -w 启动后会通过微信进入小程序
```
CRITICAL_ELEMENT:
  MINI_PROGRAM_NAME: 此处值为待测的小程序的名字

java -jar UICrawler.jar -u udid -w
```


### 查看支持的参数
```aidl
java -jar UICrawler.jar -h

    -a  Android package's main activity
    -b  iOS bundle id
    -e  Record performance data
    -f  Yaml config  file
    -h  Print this usage information
    -i  Ignore crash
    -m  Run monkey
    -p  Android package name
    -t  Appium port
    -u  Device serial
    -v  Version
    -z  WDA port for ios
    -x  Write data to influxDB
    -w  Run in wechat mode
```

### 一些常用命令
```
查看设备udid
Android:
  adb devices
  
iOS:
  instruments -s  devices
  idevice_id -l
  idevicecrashreport -e .
  
Android 查看app包名 和 Main activity
  Linux/Mac
    ./aapt dump badging "apk"  | grep launchable-activity
  Windows
    aapt dump badging "apk"  | findstr launchable-activity
    
  aapt 通常在android sdk的 build-tools目录下, "apk"是apk文件路径

Android查看当前activity
    adb shell dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp'
```

## 测试报告 
![](https://github.com/lgxqf/UICrawler/blob/master/doc/pic/Test-Report.png)


## 注意事项
* iOS设备一定要打开"开发者选项"里的“Enable UI Automation” https://www.jianshu.com/p/a1d075b3472c
* iOS测试包必须是debug版的
* 同时运行多台ios设备时要每台设备要设置不同的IOS_WDA_PORT ： 8001-8888
* Android7及以上的手机必须安装Uiautomator2 server 的两个apk(安装deskstop版appium,初次连接appium会自动安装), 也可进入到[apk](https://github.com/lgxqf/UICrawler/tree/master/apk)目录下通过adb安装


## 依赖的工具
* Grafana http://docs.grafana.org/installation/mac/
* InfluxDB https://portal.influxdata.com/downloads


## Known issue
* iOS不支持WKWebview元素获取 https://github.com/appium/appium/issues/9408
* Android中bounds=[device width, device height]时xpath不能定位到元素.（appium bug)


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
* 微信小程序自动化测试 https://testerhome.com/topics/12003?order_by=like&
* 手势 https://www.jianshu.com/p/095e81f21e07
* XpathTester https://www.freeformatter.com/xpath-tester.html
* Appium并发测试 https://www.cnblogs.com/testway/p/6140594.html
* Android 性能采集 https://blog.csdn.net/bigconvience/article/details/35553983


## 一些技术文档
* Appium Java-Client API http://appium.github.io/java-client/
* iOS多机远程控制技术 https://mp.weixin.qq.com/s/rN2xcO9gNIAoeY71NX_HZw
* http://appium.io/docs/en/commands/mobile-command/
* https://appiumpro.com/editions/12
* SpringAOP https://blog.csdn.net/zknxx/article/details/53240959
* Appium inspector 用法 https://blog.csdn.net/weixin_45314192/article/details/120743367

## Known issue
* Android SDK build-tools  30.0.0 需要配合Java 12才能使用否则会报错 "appium-uiautomator2-server-v4.20.0.apk'. Original error: Error: A JNI error has occurred"
详情参考
  * https://ceshiren.com/t/topic/2329
  * https://blog.csdn.net/weixin_46055113/article/details/111193255

## [CHANGE HISTORY](https://github.com/lgxqf/UICrawler/blob/master/doc/ChangeHistory.md)
