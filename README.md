# UICrawler
## 基于Appium 1.8.1开发的App UI遍历 & Monkey 工具

![](https://github.com/lgxqf/UICrawler/blob/master/doc/picToMov.gif)

QQ 技术交流群 ： 728183683

环境搭建及基本使用说明： https://testerhome.com/topics/14490  （感谢网友harsayer 倾力之作）


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
* 支持根据关键字和控件类型触发Back key(Android)
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
* 报告中增加每个activity中click失败和成功的次数统计
* 划动半屏，划动一屏
* 根据执行步骤重现bug



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
appium --session-override -p 4723
-p 设定appium server的端口 , 不加参数默认为4723
```

### 5.1 运行元素遍历(必须有yml配置文件)
```aidl
java -jar UICrawler.jar -f config.yml -u udid -t 4723
-u 指定设备udid
-t 指定appium server的端口（此项为可选项，默认值是4723)
```

### 5.2 运行 Monkey功能
```aidl
java -jar UICrawler.jar -f config.yml -u udid -t 4723 -m
```

### 5.3 运行微信小程序测试，需修改 MINI_PROGRAM_NAME的值，并按照下面的值设置 CRITICAL_ELEMENT中相应的值，才会启动微信进入小程序
```
#小程序
MINI_PROGRAM:
  MINI_PROGRAM_NAME: 此处值为待测的小程序的名字
  MINI_PROGRAM_PROCESS: com.tencent.mm:appbrand1

CRITICAL_ELEMENT:
  #Android 微信
  ANDROID_PACKAGE: com.tencent.mm
  ANDROID_MAIN_ACTIVITY: com.tencent.mm.ui.LauncherUI

  #iOS 微信
  IOS_BUNDLE_ID: com.tencent.xin
  IOS_BUNDLE_NAME: 微信
  IOS_IPA_NAME: wechat
  
```


### 查看支持的参数
```aidl
java -jar UICrawler.jar -h

    -a  Android package's main activity
    -b  iOS bundle id
    -c  Maximum click count
    -d  Maximum crawler UI depth
    -e  Record performance data
    -f  Yaml config  file
    -h  Print this usage information
    -i  Ignore crash
    -l  Execution loop count
    -m  Run monkey
    -p  Android package name
    -r  Crawler running time
    -t  Appium port
    -u  Device serial
    -v  Version
    -w  WDA port for ios
    -x  Write data to influxDB

```

### 一些常用命令
```
查看设备udid
Android:
  adb devices
iOS:
  instruments -s  devices
  idevice_id -l
  
Android 查看apk 和 Main activity
  ./aapt dump badging "apk"  | grep launchable-activity
  aapt 通常在android sdk的 build-tools目录下
  windows中将grep换成findstr
  "apk"是apk文件路径
```

## [配置文件介绍](doc/Config.md)

### 配置文件主要可配置项
* 截图数量控制
* 黑名单、白名单
* 限制遍历深度、次数、时间
* 遍历界面元素的xpath
* 自动登录的用户名和密码及相应的UI元素ID 
* 待输入文本列表及待输入的控件类型
* Monkey触发事件类型及比率


## 测试报告 
![](https://github.com/lgxqf/UICrawler/blob/master/doc/Test-Report.png)


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


## 关于如何发挥测试工具的价值
* 用不同的账号运行测试，因为每个账号可能看到的内容不一样
* 用不同的手机系统版本运行测试，尽可能做到iOS 9-11, Android 4-8 都覆盖到，如果资源有限无法同时运行多台设备，每天可以选择一两个系统版的手机去运行测试
* 用不同的手机运行测试，尤其安卓碎片化严重，手机厂商多，有的问题只有特定手机才能发现
* 关于测试运行的频度，只要有代码改动建议至少一天一次，下班时运行，第二天看结果 


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


## Change History
### 2018-06-14  
* 添加 UICrawler运行时间限制 CRAWLER_RUNNING_TIME 

### 2018-06-15
* 更改特殊点坐标的选取为顺序选取(原来是随机选取)
* 为文本查找增加划动查找功能(Android)
* 增加配置项 DISABLE_DOM_DISPLAY 屏蔽/开启 UI DOM Tree在Log中的显示
* 将Monkey和UICrawler时间限制统一合并到参数 -r

### 2018-06-16
* 增加小程序遍历结束判断

### 2018-06-19
* 为小程序测试增加划动查找"小程序"功能（Android）
* 为Monkey功能加入Back键事件支持

### 2018-06-26
* 支持循环执行UI遍历  见参数 -l 

### 2018-06-28
* 加入遍历深度参数支持 -d
* 增加根据文本内容和控件类型来触发Back Key    BACK_KEY_TRIGGER_LIST:
* 修复了有时会点击失败的问题

### 2018-07-04
* Update Java-Client to 1.6.1
* Android通过屏蔽 "bounds"值 解决Xpath不能定位到 权限对话框的问题
* 运行时增加version输出

### 2018-07-05
* Android 当待点击元素的bounds值中包含屏幕的height时，在查找元素的xpath中忽略bounds的值(功能已修改 见2018-07-10)

### 2018-07-10
* Android 当待点击元素的bounds值中包含屏幕的[width,height]时(通常包含这个值的元素在屏幕右下角)，在查找元素的xpath中忽略bounds的值。
* 添加配置项  REMOVE_BOTTOM_BOUND 来开启和关闭该功能
* 注：之所以添加这个功能是为临时解决Appium在用Xpath查找元素时的一个bug. 当元素位于右下角时 xpath查找时如果包含bounds的值，会找不到元素。
* 更改测试报告中图片相关的路径为相对路径，方便Copy到其它目录后查看结果
* 添加性能监控输出，获取内存和CPU的值 参数-e 生成 perf_data.txt 

### 2018-07-12
* 重要更新：  Android 将原来tab只支持单个Resource ID改为可由or拼接的多个Resource ID, 将且要加上@resource-id 关键字
如ANDROID_BOTTOM_TAB_BAR_ID: 'com.xes.jazhanghui.activity:id/bottomBar'  更改变为 ANDROID_BOTTOM_TAB_BAR_ID: '@resource-id="com.xes.jazhanghui.activity:id/bottomBar"'
* 当iOS获取不到XCUIApplication (app name)时，返回config中设备的 IOS_BUNDLE_NAME
* 测试报告中各项加入英文翻译

### 2018-07-17 v2.8
* 添加 -x 参数 支持将性能数据写入到influxDB, -x 要与 -e 结合使用。 （需要安装influxdb https://portal.influxdata.com/downloads)
* 配置文件中添InfluxDB 配置项 INFLUXDB    

### 2018-07-18 v2.9
* 修复生成遍历视频时因为有不同方向截图而导致生成的视频黑屏的问题
* 添加VIDEO_VERTICAL 控制视频的显示方向 true为竖屏 false为横屏
* 命令行输出增加视频生成进度显示

### 2018-07-23 v2.10
* 修复视频生成时只有一半截图的问题

### 2018-08-01 v2.11
* 增加iOS性能监控 -e 生成trace.zip， 性能数据需结合instruments查看， 详情请见 https://appiumpro.com/editions/12

### 2018-08-06 v2.12
* 修改了一些错误提示，找不到设备时会列出当前的设备列表

### 2018-08-17 v2.14
* 修改iOS的截图方式为 idevicescreenshot 提高截图效率

### 2018-08-23 v2.15
* 修改视频生成只能是横屏的bug
* 删掉Appium iOS启动的参数 PLATFORM_VERSION

### 2018-09-05 v2.17
* 在Config中增加DeviceName支持,用于输出的目录名字
```
5f5fa944c51bb0073b91736a6d7828a57f0a1b98:
  DEVICE_NAME: Justin's_iPhone
```

### 2018-09-06 v2.18 有重要更新！！！
* 将登录相关的元素查找信息及操作移到了 LOGIN_ELEMENTS
* 增加了Drag 拖拽操作的支持
* 修改了代码中的一些Warning


### 2018-09-10 v2.20
* 增加自定义输出路径 -o
* 在输出的目录中将udid中的":"替换成"_"
* 解决了Windows上report乱码的问题
* 为Monkey增加自动登录功能 
* 为Monkey增加待点击元素xpath列表


### 2018-09-11 v2.21
* 支持黑名单Xpath ITEM_BLACKLIST
* 加入参数 -s 指定 Appium Server IP
* UI遍历功能加入自动登录检查的间隔以提高运行效率 USER_LOGIN_INTERVVAL

### 2018-11-06 v2.22
* 修改crash后report中crash count显示为0的问题

### 2018-12-07
* 自动登录时，click操作后增加休眠时间设定, 通过VALUE的值设定休眠的时间

### 2018-12-12
* 修改DOM_DISPLAY的注释说明