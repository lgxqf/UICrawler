# CHANGE HISTORY
### 2020-01-07
* 升级Java-client为7.3.0
* 支持Appium 1.61.0
* 去除不常用的一些参数如 -c, -d, -l, -r
* 优化配置文件项
* 添加 -w 支持小程序遍历

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

### 2019-02-21 v2.25
* 处理Device ID中有空格的情况
* 升级Java-Client到7.0 支持Appium 1.10.1


### 2019-02-23 v2.26
* 更新config.yml文档结构及部分注释
* 新增配置 PRESS_BACK_ACTIVITY_LIST 和 RUN_IN_WECHAT_MINI_PROGRAM_MODE
* 将Change history移到单独的文档
* 处理在运行小程序测试时 PRESS_BACK_KEY_PACKAGE_LIST 中包含微信包名的情况
* 根据PRESS_BACK_ACTIVITY_LIST 中配置的activity的名字来触发返回键