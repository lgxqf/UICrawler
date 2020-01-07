## 配置文件主要可配置项
* 截图数量控制
* 黑名单、白名单
* 限制遍历深度、次数、时间
* 遍历界面元素的xpath
* 自动登录的用户名和密码及相应的UI元素ID 
* 待输入文本列表及待输入的控件类型
* Monkey触发事件类型及比率

  ### 一些默认值，通常不需要修改
  SCREENSHOT_COUNT : 截屏数量  
  MAX_DEPTH : 遍历深度  
  MAX_CLICK_COUNT : 点击次数   
  APPIUM_SERVER_IP : Appium Server地址
  * 通常情况下不需要修改，除非Appium server和手机不在同一台电脑上，需把该值改为Appium Server所在电脑的IP    
  
  DEFAULT_WAIT_SEC: 查找元素的最长时间限制    
  DEFAULT_POLLING_INTERVAL_SEC: 查找元素的时间间隔  

  PORT: Appium Server Port   
  IOS_WDA_PORT: WDA Port(iOS)  
  IGNORE_CRASH :忽略Crash  
  * true 遇到crash会重启App继续遍历
  * false 遇到crash停止遍历
  
  ### 针对不同APP必需修改的值
  Android APK包名和主Activity  
  * ANDROID_PACKAGE: com.xxxx.yyyy
  * ANDROID_MAIN_ACTIVITY: com.xxx.yyy.StartActivity
 
  IOS_BUNDLE_ID: iOS的bundle id com.xxxx.yyy   
  IOS_BUNDLE_NAME: 手机屏幕上显示的app的名字  如下图： 其值为:学而思test  
  ![](https://github.com/lgxqf/UICrawler/blob/master/doc/IOS_BUNDLE_NAME.png)   
  IOS_IPA_NAME: ips文件名前缀 
  * 这个很关键用于判断iOS app是否crash了
  * 可以通过idevicecrashreport ./ 查看ips文件名前缀. 如：AlipayWallet-2018-04-23-125441.ips  其IOS_IPA_NAME值为AlipayWallet


### Monkey功能配置
  MONKEY_RUNNING_TIME:  180      运行时间，以分钟计  
  * 以下各项值总和需为100    
  * SWIPE_RATIO: 10  滑动事件百分比    
  * CLICK_RATIO: 80  点击事件百分比    
  * CLICK_SPECIAL_POINT_RATIO: 8  点击MONKEY_SPECIAL_POINT_LIST中坐标事件百分比。 主要用于触发"返回"功能。     
  * RESTART_APP_RATIO: 1  重启APP事件百分比     
  * HOME_KEY_RATIO: 1  按Home键事件百分比（只支持安卓）     
  * LONG_PRESS_RATIO: 8 特殊坐标长按(10秒)
  * LONG_PRESS_LIST: 根据以下坐标长按10秒   
   '980,560'   
   '100，120'      
  * MONKEY_SPECIAL_POINT_LIST: 提高测试效率，点击以下点(x,y)时会触发"后退"操作，避免在一个页面停留时间太久         
    '80,160'       
    '50,50'      
    
    
  ### 构建XPATH需要的一些内容
  UI底部TabBar
  * ANDROID_BOTTOM_TAB_BAR_ID: com.xxx.yy:id/bottomBar
  * IOS_BOTTOM_TAB_BAR_TYPE: XCUIElementTypeTabBar
  * 当app中有tab bar时需要配置此项，配置后会先遍历tab bar以外的元素，最后再遍历tab bar  
  ![](https://github.com/lgxqf/UICrawler/blob/master/doc/Tabbar.png)

  查找需要点击的元素的关键XPATH内容
  * ANDROID_CLICK_XPATH_HEADER: '@clickable="true" and string-length(@text)<30'
  * IOS_CLICK_XPATH_HEADER: '@visible="true" and string-length(@value)<30'
  * 注意属性值要加引号'''，通常用默认值即可
  
  ### 自动登录时需要的信息
  * 支持两种操作，一种是input 一种是click
  * 目前只支持通过用户和密码登录的app   
  
  * ANDROID_USERNAME:  查找输入用户名控件所需要的信息
    * XPATH: '//*[@resource-id="com.xes.jazhanghui.activity:id/xes_login_username"]'   
    * ACTION: input   
    * VALUE: '123456'      
    
  * ANDROID_PASSWORD:   查找输入密码控件所需要的信息
    * XPATH: '//*[@resource-id="com.xes.jazhanghui.activity:id/xes_login_password"]'  
    * ACTION: input  
    * VALUE: '123456'  
    
  * ANDROID_LOGIN_BUTTON:  查找登录按钮所需要的信息
    * XPATH: '//*[@resource-id="com.xes.jazhanghui.activity:id/xes_login_button"]'  
    * ACTION: click  

  * IOS_USERNAME:
    * XPATH: '//*[@type="XCUIElementTypeTextField"]'
    * ACTION: input
    * VALUE: '1234568789'
  * IOS_PASSWORD:
    * XPATH: '//*[@type="XCUIElementTypeSecureTextField" and @value="请输入登录密码"]'
    * ACTION: input
    * VALUE: '123456'
  * IOS_LOGIN_BUTTON:
    * XPATH: '//*[@type="XCUIElementTypeButton" and @name="登录" and @label="登录"]'
    * ACTION: click

### LIST
  PRESS_BACK_KEY_PACKAGE_LIST: 当跳到转以下app时，触发back键
  - 高德地图
  - com.autonavi.minimap

  
  ANDROID_VALID_PACKAGE_LIST: 当app跳转到以下app时被认为是合法，会继续遍历操作
  - com.miui.securitycenter
  - com.android.server.telecom
  - com.lbe.security.miui
  - gallery
  - packageinstaller

  IOS_VALID_BUNDLE_LIST: 当app跳转到以下app时被认为是合法，会继续遍历操作
  - 照片

  ITEM_WHITE_LIST: 白名单
  - 确定
  - 允许
  - 退出

  ITEM_BLACKLIST: 不点击包含以下文本的控件
  - 客服
  - 电话
  - 不允许
  - 拒绝
  - 拍照
  - 禁止
  - 呼叫

  IOS_EXCLUDE_BAR: 以下类型的元素及子元素不会被点击
  - XCUIElementTypeStatusBar （手机状态栏）
  - XCUIElementTypeKeyboard （键盘）

  IOS_EXCLUDE_TYPE: iOS不点击以下类型的元素
  - XCUIElementTypeOther
  - XCUIElementTypeKey
  - XCUIElementTypeWindow

  ANDROID_EXCLUDE_TYPE:android中不点击以下类型的元素
  - android.widget.FrameLayout

  NODE_NAME_EXCLUDE_LIST:
  - selected
  - instance
  - checked
  - naf
  - content

  STRUCTURE_NODE_NAME_EXCLUDE_LIST:
  - value
  - label
  - name
  - text





