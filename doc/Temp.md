
##待开发功能
* 去除Android7 android:id/statusBarBackground
* 定义遍历优先级
* 判断是不是同一个UIby tolerance
* edittext 文本输入
* 支持滑动等更多动作
* merge macaca monkey
* 重现bug
* 当跳出app时检查是否Crash android & iOS


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
* Android7 必须用Uiautomator2驱动  且用Xpath查找元素时 元素的属性值不能为空不然会查找失败,用Appium Desktop可以安装Uiautomator apk
* Android6 千万不要装Uiautomator2 apk, 当遇到Android 6的机器getPageSource没反应时 1.卸载 uiautomator2 2.重启手机 3.一定要换个appium端口

## 支持的平台
* Android 4-7 Android 4-5 未测试
* IOS 10-11 IOS 10未测试


TODO:
    把uiautomator2 apk放到git上