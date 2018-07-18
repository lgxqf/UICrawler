# UICrawler
## 基于Appium 1.8.1开发，Java版 App UI遍历工具

![](https://github.com/lgxqf/UICrawler/blob/master/doc/picToMov.gif)

QQ 技术交流群 ： 728183683

环境搭建及基本使用说明： https://testerhome.com/topics/14490  （感谢网友harsayer 倾力之作）


## 2.0 版 功能描述 
### 1.UI遍历功能 Android/iOS 
### 2.Monkey功能 Android/iOS 
### 3.微信小程序 


### UI遍历及以下功能 
* 基于深度优先的原则，点击UI上的元素。当发现Crash时会提供操作步骤截图及相应的Log.(Android提供logcat的log. iOS提供系统log及crash相应的ips文件)
* 元素遍历结束或按下Ctrl + C键会生成HTML测试报告。测试报告中汇集了测试统计、log及截图的相应信息  
* 同一个元素只会点击一次(白名单中的元素可能会被多次点击)
* 支持对输入框的文本输入(需在文件中进行配置 INPUT_TEXT_LIST)
* 统计每个Activity点击数量(Android)
* 支持滑动动作
* 支持根据关键字和控件类型触发Back key(Android)
* 性能数据收集(内存和CPU) 生成perf_data.txt 


### Monkey功能及以下事件
* 随机点击
* 通过黑名单控制不想点击的区域
* 特殊位置点击(需在文件中进行配置)
* 特殊位置长按10秒(需在文件中进行配置)
* 任意方向及长度的滑动
* 触发Home键(Android Only)
* 触发Back键(Android Only)
* 重启app
* 及以下手势操作(位置随机)
*      双击
*      双指放大
*      双指缩小
*      拖拽


### 小程序测试
* Monkey (iOS & Monkey)
* UI遍历 Android only


### 4.其它功能
* 运行时间限制
* 每次点击都会生一个一截图，截图中被点击的位置会用红点标注，方便查找点击位置
* 当检查到Crash时，为每个Crash提供单独的操作步骤截图和mp4格式的视频文件
* 生成整体操作步骤视频，方便重现发现的问题




## 待开发功能 1.1版 预计6下旬月release
* 将性能数据通过grafana显示 https://www.cnblogs.com/yyhh/p/5990228.html
* 支持遍历顺序控制
* 根据执行步骤重现bug


## 配置文件可配置项
* 截图数量控制
* 黑名单
* 白名单
* 限制遍历深度
* 限制点击次数
* 遍历界面元素的xpath
* 自动登录的用户名和密码及相应的UI元素ID 
* 待输入文本列表及控件类型


### 下载Jar包
[UICrawler.jar](https://pan.baidu.com/s/1mNci6SWNHPuLj_mvrfgIbg)
### 下载配置文件
[config.yml](https://github.com/lgxqf/UICrawler/blob/master/config.yml) 
### 根据待测试App修改配置文件中下列各项的值，其它值用默认值即可 [详情见 Config.md](doc/Config.md)
  #### Android
  * ANDROID_PACKAGE
  * ANDROID_MAIN_ACTIVITY
  #### iOS
  * IOS_BUNDLE_ID
  * IOS_BUNDLE_NAME
  * IOS_IPA_NAME
### Monkey配置项可选， 详情见 [Monkey配置](https://github.com/lgxqf/UICrawler/blob/master/doc/Config.md#monkey%E5%8A%9F%E8%83%BD%E9%85%8D%E7%BD%AE)  
### 启动appium
```bash
appium --session-override -p 4723
-p 设定appium server的端口 , 不加参数默认为4723
```

### 运行UICrawler元素遍历(必须有yml配置文件), 运行前请先阅读[注意事项](https://github.com/lgxqf/UICrawler#%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9)
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

## 注意事项
* iOS设备一定要打开"开发者选项"里的“Enable UI Automation” https://www.jianshu.com/p/a1d075b3472c
* iOS测试包必须是debug版的
* 同时运行多台ios设备时要每台设备要设置不同的IOS_WDA_PORT ： 8001-8888
* Android7的手机必须安装Uiautomator2 server 的两个apk(安装deskstop版appium,初次连接appium会自动安装), 也可进入到[apk](https://github.com/lgxqf/UICrawler/tree/master/apk)目录下通过adb安装
* Android6及以下的手机不要安装Uiautomator2 server的APK

## Known issue
* iOS 不支持wkwebview元素获取 https://github.com/appium/appium/issues/9408
* 当有横屏和竖屏截图混合时 生成的mp4内容无效
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


## 依赖的工具
* Grafana http://docs.grafana.org/installation/mac/
* InfluxDB https://portal.influxdata.com/downloads

## 一些技术文档
* Appium Java-Client API http://appium.github.io/java-client/
* iOS多机远程控制技术 https://mp.weixin.qq.com/s/rN2xcO9gNIAoeY71NX_HZw
* http://appium.io/docs/en/commands/mobile-command/
* https://appiumpro.com/editions/12

# 更多细节
* [配置文件介绍](doc/Config.md)


# 测试报告 
![](https://github.com/lgxqf/UICrawler/blob/master/doc/Test-Report.png)

# 关于如何发挥测试工具的价值
* 用不同的账号运行测试，因为每个账号可能看到的内容不一样
* 用不同的手机系统版本运行测试，尽可能做到iOS 9-11, Android 4-8 都覆盖到，如果资源有限无法同时运行多台设备，每天可以选择一两个系统版的手机去运行测试
* 用不同的手机运行测试，尤其安卓碎片化严重，手机厂商多，有的问题只有特定手机才能发现
* 关于测试运行的频度，只要有代码改动建议至少一天一次，下班时运行，第二天看结果 


# Chnage History
## 2018-06-14  
* 添加 UICrawler运行时间限制 CRAWLER_RUNNING_TIME 

## 2018-06-15
* 更改特殊点坐标的选取为顺序选取(原来是随机选取)
* 为文本查找增加划动查找功能(Android)
* 增加配置项 DISABLE_DOM_DISPLAY 屏蔽/开启 UI DOM Tree在Log中的显示
* 将Monkey和UICrawler时间限制统一合并到参数 -r

## 2018-06-16
* 增加小程序遍历结束判断

## 2018-06-19
* 为小程序测试增加划动查找"小程序"功能（Android）
* 为Monkey功能加入Back键事件支持

## 2018-06-26
* 支持循环执行UI遍历  见参数 -l 

## 2018-06-28
* 加入遍历深度参数支持 -d
* 增加根据文本内容和控件类型来触发Back Key    BACK_KEY_TRIGGER_LIST:
* 修复了有时会点击失败的问题

## 2018-07-04
* Update Java-Client to 1.6.1
* Android通过屏蔽 "bounds"值 解决Xpath不能定位到 权限对话框的问题
* 运行时增加version输出

## 2018-07-05
* Android 当待点击元素的bounds值中包含屏幕的height时，在查找元素的xpath中忽略bounds的值(功能已修改 见2018-07-10)

## 2018-07-10
* Android 当待点击元素的bounds值中包含屏幕的[width,height]时(通常包含这个值的元素在屏幕右下角)，在查找元素的xpath中忽略bounds的值。
* 添加配置项  REMOVE_BOTTOM_BOUND 来开启和关闭该功能
* 注：之所以添加这个功能是为临时解决Appium在用Xpath查找元素时的一个bug. 当元素位于右下角时 xpath查找时如果包含bounds的值，会找不到元素。
* 更改测试报告中图片相关的路径为相对路径，方便Copy到其它目录后查看结果
* 添加性能监控输出，获取内存和CPU的值 参数-e 生成 perf_data.txt 

## 2018-07-12
* 重要更新：  Android 将原来tab只支持单个Resource ID改为可由or拼接的多个Resource ID, 将且要加上@resource-id 关键字
如ANDROID_BOTTOM_TAB_BAR_ID: 'com.xes.jazhanghui.activity:id/bottomBar'  更改变为 ANDROID_BOTTOM_TAB_BAR_ID: '@resource-id="com.xes.jazhanghui.activity:id/bottomBar"'
* 当iOS获取不到XCUIApplication (app name)时，返回config中设备的 IOS_BUNDLE_NAME
* 测试报告中各项加入英文翻译

## 2018-07-17 v2.8
* 添加 -x 参数 支持将性能数据写入到influxDB, -x 要与 -e 结合使用。 （需要安装influxdb https://portal.influxdata.com/downloads)
* 配置文件中添InfluxDB 配置项 INFLUXDB    

## 2018-07-18
* 修复生成遍历视频时因为有不同方向截图而导致生成的视频黑屏的问题
* 添加VIDEO_VERTICAL 控制视频的显示方向 true为竖屏 false为横屏
* 命令行输出增加视频生成进度显示
