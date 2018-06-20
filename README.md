# UICrawler
## 基于Appium 1.8.0开发，Java版 App UI遍历工具

![](https://github.com/lgxqf/UICrawler/blob/master/doc/demo.gif)

QQ 技术交流群 ： 728183683

环境搭建及基本使用说明： https://testerhome.com/topics/14490  （感谢网友harsayer 倾力之作）

## 1.0 版 功能描述 
### 支持Monkey功能及以下事件
* 随机点击
* 通过黑名单控制不想点击的区域
* 特殊位置点击(需在文件中进行配置)
* 特殊位置长按10秒(需在文件中进行配置)
* 任意方向及长度的滑动
* 触发Home键(Android Only)
* 重启app
* 及以下手势操作(位置随机)
*      双击
*      双指放大
*      双指缩小
*      拖拽


### 支持UI遍历及以下功能    
* 基于深度优先的原则，点击UI上的元素。当发现Crash时会提供操作步骤截图及相应的Log.(Android提供logcat的log. iOS提供系统log及crash相应的ips文件)
* 元素遍历结束或按下Ctrl + C键会生成HTML测试报告。测试报告中汇集了测试统计、log及截图的相应信息  
* 同一个元素只会点击一次(白名单中的元素可能会被多次点击)
* 支持对输入框的文本输入(需在文件中进行配置 INPUT_TEXT_LIST)
* 统计每个Activity点击数量(Android)
* 支持滑动动作


### 其它功能
* 以上两个功能同时支持Android/iOS
* 以上两个功能同时支持运行时间限制
* 每次点击都会生一个一截图，截图中被点击的位置会用红点标注，方便查找点击位置
* 当检查到Crash时，为每个Crash提供单独的操作步骤截图和mp4格式的视频文件
* 生成整体操作步骤视频，方便重现发现的问题(见下图)

![](https://github.com/lgxqf/UICrawler/blob/master/doc/picToMov.gif)


## 待开发功能 1.1版 预计6下旬月release
* 支持小程序测试(Android Only)
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


# 更多细节
* [配置文件介绍](doc/Config.md)
* [环境搭建](doc/Environment.md)

# 测试报告 
![](https://github.com/lgxqf/UICrawler/blob/master/doc/Test-Report.png)

# 关于如何发挥测试工具的价值
* 用不同的账号运行测试，因为每个账号可能看到的内容不一样
* 用不同的手机系统版本运行测试，尽可能做到iOS 9-11, Android 4-8 都覆盖到，如果资源有限无法同时运行多台设备，每天可以选择一两个系统版的手机去运行测试
* 用不同的手机运行测试，尤其安卓碎片化严重，手机厂商多，有的问题只有特定手机才能发现
* 关于测试运行的频度，只要有代码改动建议至少一天一次，下班时运行，第二天看结果 


#Chnage History
##2018-06-14  
* 添加 UICrawler运行时间限制 CRAWLER_RUNNING_TIME 
##2018-06-15
* 更改特殊点坐标的选取为顺序选取(原来是随机选取)
* 为文本查找增加划动查找功能(Android)
* 增加配置项 DISABLE_DOM_DISPLAY 屏蔽/开启 UI DOM Tree在Log中的显示
* 将Monkey和UICrawler时间限制统一合并到参数 -r