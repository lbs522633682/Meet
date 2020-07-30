
开发日志 思考每一个遇到的问题
-----------------------------------20200730---------------------------
## Rxjava 的使用
### 引用
    io.reactivex.rxjava2:rxjava:2.2.2
    io.reactivex.rxjava2:rxandroid:2.1.0"

### 举例

    disposable = Observable.create(new ObservableOnSubscribe<String>() { // 创建一个发射器，发射器的返回类型是 String的

        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception { // 在发射器中执行请求过程
            String json = HttpManager.getInstance().postCloudToken(map);

            emitter.onNext(json); // 使用发射器将  结果 发射出去
            emitter.onComplete(); // 告知发射器 执行完成
        }
    }).subscribeOn(Schedulers.newThread()) // 订阅者 子线程
            .observeOn(AndroidSchedulers.mainThread()) // 观察者 主线程
            .subscribe(new Consumer<String>() { 使用接收器 接收结果
                @Override
                public void accept(String s) throws Exception {
                    // {"code":200,"userId":"b757a3c83d","token":"Edr2bmMy5wa59uh+UAR0Oa3dR1vFJod+bYKsvjVGsBI=@yby3.cn.rongnav.com;yby3.cn.rongcfg.com"}
                    LogUtils.i("createToken s = " + s);
                    parsingToken(s);
                }
            });

## 获取融云的token
    功能：即时通讯 跟音视频

## 融云参数 自己的
    账号： 15967153155 li666666
    appkey：25wehl3u20a0w
    appSecrect: SRlG9pAHfH

## android 7.0 的明文访问
    cleartextTrafficPermitted
-----------------------------------20200728---------------------------
## 封装万能的RecycleView适配器
    主要是相关的 泛型的使用

## 自定义的头部拉伸ScrollView
    1.思路 触摸事件 放大、回弹效果

### 步骤
    1.在onFinishInflate中获取到 mZoomView

    2.处理触摸移动事件

    3.计算移动的距离

    4.根据移动的距离 计算缩放的比例

    5.使用LayoutParams 对view进行缩放

    6.缩放的过程中 使用setMargins 对view设置间距

    7.在松开手指后处理回弹事件 使用属性动画，将view从当前 移动回 原始的view

## 从通讯录匹配好友
    1.获取联系人
    2.查询联系人是否在后台已注册
    3.显示查询到的联系人在界面上
-----------------------------------20200727---------------------------
## 从通讯录匹配好友
    1.获取联系人
-----------------------------------20200522---------------------------
## 搜索好友与推荐
### Bmob模拟用户数据
### Bmob根据条件查询好友
    
    手机号码：12510603000
### Bmob推荐好友

### ActionBar跟Layout之间有一条阴影
    解决：
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // 设置阴影
            getSupportActionBar().setElevation(0);
        }

-----------------------------------20200515---------------------------
## 头像上传 与 FileProvider2

### Bmob更新用户资料
    上传头像失败
    原因：Bmob需要使用独立域名设置  设置 - 应用配置 - 独立域名 100/年
    免费的方法，使用老师的key：f8efae5debf319071b44339cf51153fc
    如果有数据冲突，类名不一致即可

    问题：老师的key的短信条数可能用完，改为账号密码

    账号：15967153155 pwd：123456  objID = objIde3d14f363c nickName = 老李
-----------------------------------20200514---------------------------
## 头像上传 与 FileProvider

### Android7.0 FileProvider

### 从相册获取头像

#### 圆形头像裁剪库
    https://github.com/hdodenhof/CircleImageView

    依赖：implementation 'de.hdodenhof:circleimageview:3.1.0'

### 解决URI获取不到真实地址的问题

    问题：跳转到相册，返回之后 通过data.getData().getPath(); 图片地址为：/external/images/media/134216，并非SD卡根目录地址
    解决：通过返回的uri 去系统查询真实地址  /storage/emulated/0/MagazineUnlock/magazine-unlock-01-2.3.4332-_035B5B61EAFA5DB2B9982DCA612BA53C.jpg
        /**
     * 根据Uri获取真实的图片地址
     * @param context
     * @param uri
     * @return
     */
    public String getRealImgPathFromUri(Context context, Uri uri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(context, uri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }



### android 7.0 明文访问

-----------------------------------20200513---------------------------
## 3D星球的实现
    tagcloud ： com.moxun:tagcloudlib:1.2.0

circleimageview?

## Fragment 优化与切换
### Fragment三个包
    1.android.app.Fragment
    2.android.app.v4.Fragment 向下兼容
    3.androidx.fragment.app.Fragment

### Fragment 关键类
    1.FragmentManager 碎片化的管理类
    2.FragmentTransation 操作Fragment做一些事务

### FragMent 切换方式
    1.replace 会重新走生命周期 用的较少
    2.show/hide
### Fragment 优化
    onAttachFragment 此方法防止重叠
    场景：当应用内存紧张的时候，系统会回收掉 Frament对象；
    再次进入的时候会重新创建Fragment，并非原来的对象，我们无法控制，导致重叠

## APP启动优化
### 启动类型
    1.冷启动 第一次开始  或是 重装启动
    2.热启动 第二次或之后启动
    3.温启动 退到后台，由于某些原因被杀死，但整体数据还保存

### Shell命令 检测启动时间
    1. shell命令
    adb shell am start -S -W [packageName]/[packageName.MainActivity]
        1. thisTime 最后一个Act的启动耗时
        2. TotalTime 启动一连串Act的总耗时
        3. WaitTime 应用创建时间 + TotalTime
        4. 应用创建时间 WaitTime - TotalTime

    2. Log打印

        Android 4.4 开始 ActivityManager增加了 Log TAG= displayed

        05-13 10:48:54.758 376-407/? I/ActivityManager: Displayed com.mumu.launcher/.Launcher: +1s494ms
        05-13 11:02:45.471 376-407/? I/ActivityManager: Displayed plat.skytv.client.education/plat.skytv.client.qh.cm.activity.SplashActivity: +2s606ms
        05-13 11:02:51.985 376-407/? I/ActivityManager: Displayed plat.skytv.client.education/plat.skytv.main.activity.MainActivity: +346ms

### 启动优化手段 
    1.视图优化 黑屏白屏  治标不治本
        1.1 设置主题透明 style中设置 windowIsTranslucent  值为true
        1.2 设置启动图片  style中 windowBackground 加一张图片  windowDrawSystemBarBackgrounds 为false
    2.代码优化
        2.1 优化Application

            --必要的组件在程序主页去初始化，不要在Application中

            --如果一定在Application中初始化，尽可能延时 handler.postdelay

            --必要的组件，子线程中初始化 new Thread().start
        2.2 不需要繁琐的布局
        2.3 阻塞UI线程的操作
        2.4 加载BItmap/ 大图
        2.5 其他一些占用主线程的操作

#### 冷启动经过的步骤
    1.第一次安装，加载应用程序并启动
    2.启动后显示一个空白的窗口
    3.启动、创建了应用进程

#### APP 内部
    1. 创建APP 对象、Application对象
    2. 启动主线程（Main/UI Thread）
    3. 创建应用入口/ LAUNCHER
    4. 填充VIewGroup中的View
    5. 绘制view measure -> layout-> draw
-----------------------------------20200512---------------------------
## 动态权限与窗口权限
### 权限分类 普通 危险 特殊
### 判断权限是否请求
### 处理请求权限的响应
### 窗口权限
    可显示在其他应用之上的权限

## 自定义LoadingView

## 自定义DialogView


## Bmob的短信验证 and 用户注册

### 注册支持的方式 
    1.账号
    2.邮箱
    3.电话
    4.第三方

### 发送短信验证码

### 短信验证码登录

### 获取本地对象

    15967153155 objectID= b757a3c83d


-----------------------------------20200322---------------------------
## Bmob
    使用github账号关联Bmob账号

### 增删改查

    主要操作的是ObjectID（Bmob 用户的唯一标识）

### 导入文档
    http://doc.bmob.cn/data/android/index.html#androidstudio

### 应用秘钥
    Application ID：a3fc2edca9eccc01110110970f1b4091
    REST API KEY：035be8e708594acbf688ea7ba25d3f11
    Secret KEY:e764231d99ff17b2
    MasterKey:0908e532d8f5c00e136b85816ec3b67f
-----------------------------------20200321---------------------------
## 自定义View 拖拽验证码
    1.获取图片
    2.创建一个空的Bitmap
    3.将图片绘制到空的Bitmap上
    4.将bgBitmap绘制到view上

## 完成内容 GuideAct
    /**
     * 1.viewpage:适配器 | 帧动画
     * 2.小圆点的逻辑
     * 3.歌曲的播放
     * 4.属性动画的旋转
     * 5.跳转
     */

## MediaPlayerManager 播放raw文件

    // 针对7.0及以上
    setDataSource(@NonNull AssetFileDescriptor afd)
    // 都可用
    setDataSource(FileDescriptor fd, long offset, long length)

## ViewPagerAdapter 封装

## ViewPager 预加载
    没必要一次性将所有item加载出来

    mViewPager.setOffscreenPageLimit(mPageList.size());

## 引导页 帧动画
### 帧动画播放
    1.获取view上设置的帧动画

    2.调用start方法即可

### 帧动画设置
    res/drawable/xxx.xml
    <animation-list xmlns:android="http://schemas.android.com/apk/res/android"
        android:oneshot="false">

        <-- oneshot 是否只显示一次，false：永久循环 -->

        <item android:drawable="@drawable/img_guide_smile_1" android:duration="2000"/>
        <item android:drawable="@drawable/img_guide_smile_2" android:duration="2000"/>
        <item android:drawable="@drawable/img_guide_smile_3" android:duration="2000"/>
        <item android:drawable="@drawable/img_guide_smile_4" android:duration="2000"/>
        ...
    </animation-list>

-----------------------------------20200320---------------------------
## Git忽略某些不重要的文件

    1.在Git工作区的根目录下创建一个特殊的.gitignore文件，

    2.通用的 配置 Git已经为我们考虑到了：https://github.com/github/gitignore

## Git将远程仓库的代码导入到本地文件夹
    1.新建一个文件夹，右键Git Bash Here到这个文件夹

    2. git clone XXXX (XXXX为项目在gihub上的http地址)


## Git 修改代码之后再次上传

    1.右键Git Bash Here到项目文件夹

    2.git add . 添加新增文件

    3.git commit -m "XXXX" (XXX为注释 必须填)

    4.git push origin master
        输入用户名（github昵称），密码即可

    参考：1.https://www.cnblogs.com/zhaomeizi/p/9268987.html

## Git 上传本地仓库到Git （安装 （略过））
    1.新建一个文件夹，右键Git Bash Here到这个文件夹，git init命令初始化 变成 Git可管理的仓库（本地）

    2.将项目代码粘贴到本文件夹，可通过git status 来查看本地仓库当前状态

    3.通过git add .  将此文件夹 所有 文件 添加到本地仓库，期间可一直通过 git status查询本地仓库状态

    4.使用git commit -m "上传日志"， 此处上传日志必须填写，否则会有问题

    5.创建SSH KEY，如果C盘用户目录下有没有.ssh目录，有的话看下里面有没有id_rsa和id_rsa.pub这两个文件，如果没有
    使用 ssh-keygen -t rsa -C "youremail@example.com"  然后一路回车就会在.ssh目录下生成这两个文件

    6.登录Github,找到右上角的图标，打开点进里面的Settings，再选中里面的SSH and GPG KEYS，点击右上角的New SSH key，
    然后Title里面随便填，再把刚才id_rsa.pub里面的内容复制到Title下面的Key内容框里面，最后点击Add SSH key，这样就完成了SSH Key的加密

    7.在Github上创建一个Git仓库，createRepository，（注：不要勾选Initialize this repository with a README）

    8.远程仓库创建完成之后，与本地仓库进行关联（后面是远程仓库的地址）
    git remote add origin https://github.com/guyibang/TEST2.git

    9.关联成功之后，就是把本地代码推送到远程端
        9.1 如果远程仓库为空（首次上传），使用 git push -u origin master
        9.2 如果远程仓库不为空，git push origin master

        等待项目上传完成，显示：
            Counting objects: 100% (90/90), done.
            Delta compression using up to 4 threads
            Compressing objects: 100% (71/71), done.
            Writing objects: 100% (90/90), 1.35 MiB | 264.00 KiB/s, done.
            Total 90 (delta 4), reused 0 (delta 0)
            remote: Resolving deltas: 100% (4/4), done.
            To https://github.com/******/TestGit.git
             * [new branch]      master -> master
            Branch 'master' set up to track remote branch 'master' from 'origin'.

    参考：https://blog.csdn.net/zamamiro/article/details/70172900


-----------------------------------20200319---------------------------

## AndroidStudio代码生成利器－LayoutCreator

    https://www.jianshu.com/p/46d0f191178f

## 引导页 与 帧动画

## implementation 与 api

    implementation 只限于本应用使用

    api 外部模块也可引用到

## App适配刘海屏
    OPPO: https://open.oppomobile.com/wiki/doc#id=10159
    VIVO: https://dev.vivo.com.cn/documentCenter/doc/103
    小米：https://dev.mi.com/console/doc/detail?pld=1160
    小米水滴屏幕:
    htts://dev.mi.com/console/doc/detail?pld=1293

    华为..

    可翻看相关手机的开发者文档

-----------------------------------20200318---------------------------

## MediaPlayerManager完成
    // 仅支持7.0 及以上
    1.setDataSource(AssetFileDescriptor afd)

    2.自定义进度的回调监听

## 增加 闪屏 登录 引导页

-----------------------------------20200317---------------------------
## MediaPlayer 使用
### 两种初始化方法
    MediaPlayer.create();
    new MediaPlayer();

### 公开接口
    1.setOnCompletionListener 流媒体播放完毕的回调
    2.setOnErrorListener 播放中发生错误的回调
    3.setOnPreparedListener 装载流媒体完成的回调
    4.setOnSeekCompleteListener 使用seekTo()结束时的回调

## 沉浸式状态栏 基于anroid5.0开发适配
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {}

### SYSTEM_UI_FLAG_FULLSCREEN  全屏 无系统状态栏（时间 电量一类的）
    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

### View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE  全屏 无系统状态栏 且无标题（下拉显示）

### View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 全屏 无系统状态栏(下拉显示) 且无标题，无虚拟按键 --适合游戏

### View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE

    getWindow().setStatusBarColor(Color.TRANSPARENT); // 非全屏 有标题 有系统状态栏 跟主页颜色一致

### getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

                getWindow().setStatusBarColor(Color.TRANSPARENT);

                getSupportActionBar().hide(); // 全屏 沉浸式 无标题 有系统状态栏 跟主页颜色一致

### getSupportActionBar().hide(); 也可在theme中设置 noActionBar主题


## git仓库
    https://git.imooc.com/coding-390/Meet

## 课程中所使用到的第三方平台的Key,只作为参考,建议自行申请
    Bmob Key：f8efae5debf319071b44339cf51153fc  用户系统
    融云 Key：k51hidwqk4yeb 音视频
    融云 Secret：os83U32SrAG
    高德地图 Key：abde3c5f58d7dd9a762019906cef613e
    高德地图 Web Key：389bc08b815e3146bfd1e45fd7f47fc5
    讯飞 Key：5b18db70

-----------------------------------20200316---------------------------
## 自定义Gradle 配置文件
1.自定义Gradle配置文件（一个文件修改所有的Moudle）
    1.统一性
    2.便于管理
    3.版本管理

2.引入自定义的属性

3.配置gradle的常量

buildTypes {
        debug{
            // 自定义gradle常量
            buildConfigField("boolean", "LOG_DEBUG", "true")
            buildConfigField("String", "LOG_TAG", "\"Meet\"")
        }
        release {
            buildConfigField("boolean", "LOG_DEBUG", "false")
        }
    }

## 提升Gradle的构建速度
Gradle 构建的三个性能指标
    --全量编译
    --代码增量编译 修改代码增删改
    --资源增量编译 修改res下
1.避免激活旧的Multidex 方法数64k 如果超过自动启动

    api<21时解决了这个问题，但并没有优化，之后的版本做了优化

2.禁止Multidex APK构建
    发布到应用市场 多渠道打包/全部打包 但是Debug时候，不需要这么多

    setting-build-complier  -PdevBuild

3.最小化资源打包
    defaultConfig {
            // 调试，release要删除
            resConfigs("zh", "xxhdpi")
        }

4.禁用PNG压缩（开发阶段不需要使用）
    aaptOptions.cruncherEnabled = false

5.PNG 转WebP 文件

6.instant run 热启动 已经自动集成

7.不适用动态版本标识库（加号） "appcompat": 'androidx.appcompat:appcompat:1.1.0+'

8.Gradle内存调优 默认分配1.5g 如果更大的项目 可调高
    gradle.properties->org.gradle.jvmargs=-Xmx1536m

9.开启Gradle的构建缓存
    gradle.properties->org.gradle.caching=true

10.使用最新的Gradle插件

## Bmob 数据 短信 即时通讯

    文档：http://doc.bmob.cn/data/android/develop_doc/#2

## 融云的即时通讯

    文档：https://www.rongcloud.cn/docs/quick_start.html

## 融云的音视频模块
    --CallKit 界面组件（融云封装的）
    --CallLib api库
    --RTCLib 基于信令 的 数据交换 最底层

## 单例封装通用工具
    --创建Framework Moudle 隔离工具类和逻辑代码

    -- 静态封装Log日志

    -- 时间转换类

## 问题：
    1.时间转换类为什么少了8个小时?

      没有考虑到时区的问题，使用中国时区的话这个差值应该是28800000ms，所以在计算小时的实现需要加上少了的时区

      long hours = ((ms + 28800000) % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
