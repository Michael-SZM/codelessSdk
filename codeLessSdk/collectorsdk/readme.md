工程分为3个模块：net,file和自动采集事件的功能，均为内部模块，
可以通过NetFacade来上报用户的信息，和首次获取SessionId和limit配置，用户的操作上报内部自动进行


接入步骤：
在application的onCreate中调用出如下方法
// 需要提供一些必要的参数
CodeLessSdk.codeLessSdk.init(this,必要参数)
// 设置是否开启日志打印
CodeLessSdk.codeLessSdk.needPrintLog(false)
// 注册activity生命周期监听，用于触发上报时机
registerActivityLifecycleCallbacks(CodeLessSdk.callback)
// 添加自定义的采集策略
CodeLessSdk.codeLessSdk.registerStrategy(TabLayout::class.java,TablayoutStragety())

直接继承com.iyx.codeless.BaseActivity或在自己的BaseActivity中添加如下代码：
```
open class BaseActivity :AppCompatActivity(){

    private val codeLessFacade = CodeLessFacade()


    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            codeLessFacade.handleWindow(window)
            ...
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
            super.setSupportActionBar(toolbar)
            codeLessFacade.handleWindow(window)
    }

    override fun setContentView(layoutResID: Int) {
            setContentView(codeLessFacade.wrapView(this,layoutResID,null))
    }

    override fun getLayoutInflater(): LayoutInflater {
        return codeLessFacade.wrapInflater(super.getLayoutInflater())
    }

}
```

在依赖工程中新建com.example.plugin_test.DialogHandle类，添加如下方法：

public static NewDialog createDialog(Context context, View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        CodeLessFacade.handleDialog(dialog.getWindow());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return new NewDialog(dialog);
    }


依赖插件apply plugin: 'com.iyx.codelesslogic_connector' ,处理yxdnaui中的createDialog方法，把window提供给sdk并且LayoutInflater包装一下
在build.gradle中添加maven仓库路径
```
maven {
            url "http://172.17.82.214:8081/repository/codeless_logic_connector/"
        }
```

添加依赖:
```
dependencies {
        ...
        classpath 'com.iyx.codeless_logic_connector:logicConnector:1.0.0'
    }
```

在工程中引用插件：
```
plugins {
    ...
    id 'com.iyx.codelesslogic_connector'
}
```


针对项目中的自定义dialog,需要单独处理一下，主要是把window提供给sdk并且LayoutInflater包装一下。方法如下：
```
// 把window交给sdk处理
CodeLessFacade.handleDialog(getWindow());
// 把LayoutInflater交给sdk包装一下
CodeLessFacade.wrapInflaterStatic(inflater);
```

针对项目中的PopWindow需要单独处理一下，主要是把window提供给sdk并且LayoutInflater包装一下。方法如下：
```
CodeLessFacade.wrapPopWindow(this)  // 需要确保PopWindow的contentView不为null
CodeLessFacade.wrapInflaterStatic(inflater)
```


开启日志打印   CodeLessSdk.codeLessSdk.needPrintLog(true)   运行工程,过滤 error  关键字 kk--  
点击界面上的元素，可以看到类似  MainActivity_activity_main_btn_01_Hello World!_  的日志，，，该日志是对应的事件的唯一ID  这里暂且称作  eventId
在工程的assets目录下新建codeless.cfg文件，点击想要埋点的地方，记录eventId，和事件名字。这里由于列表的item都是几乎一样的处理位置，所以可以将eventId改为正则表达式。
格式如下：
```
{"configs":[
    {
        "key":"MainActivity_fragment_student_home_homeRecycler_\\d+__\\d+_",
        "event":"首页banner点击事件"
    },
    {
        "key":"MainActivity_fragment_student_studentMineTab_我的_",
        "event":"点击首页底部的我的tab"
    },
    {
        "key":"MainActivity_fragment_order_lesson_appointmentTv_",
        "event":"点击约课界面立即约课"
    },
    {
        "key":"MainActivity_fragment_growth_percentTv_上周没有上课哦，赶快联系排课吧~_",
        "event":"点击成长界面的联系排课文字"
    }

]}
```

该文件完成后记得关闭打印日志 CodeLessSdk.codeLessSdk.needPrintLog(false)



#### net模块中提供了三个方法：
###### NetFacade  业务逻辑
fetchSessionId【获取sessionId和配置】 方法需要在应用起来的时候调用
uploadUserData【上报用户数据】 需要在用户发生改变的时候调用
uploadActionData【上报操作数据】自动完成调用

#### file模块
该模块外界需要关心，主要是完成日志的缓存以及读取，和上报策略实现



后期可能会加上业务数据的采集，目前的想法是预先定义一些字段（可以在配置文件中自定义），通过反射获取