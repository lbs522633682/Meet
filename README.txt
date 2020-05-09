开发日志 思考每一个遇到的问题
-----------------------------------20200509---------------------------
## 自定义View的拖拽验证码实现
    1. 绘制View背景
    2. 绘制View空白快
    3. 将VIew 的部分图抠出
    4. View的触摸事件处理
    5. View的接口 将成功失败传递出去
-----------------------------------20200323---------------------------
## Bmob
    Application ID:a3fc2edca9eccc01110110970f1b4091
### 增删改查

    增加Bmob的测试代码，完成增加 删除，查询
-----------------------------------20200321---------------------------
## 完成内容
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
    Bmob Key：f8efae5debf319071b44339cf51153fc
    融云 Key：k51hidwqk4yeb
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
