# 关于TeipReader JavaEdition

这是一款由IDlike制作的小说阅读器，使用java编写服务端。<br>
其他的内容还在开发，莫急

## 作者在GitHub上

作者[https://github.com/txlweb](https://github.com/txlweb)

这个仓库[https://github.com/txlweb/TextReaderJE/](https://github.com/txlweb/TextReaderJE/)

Android版本仓库[https://github.com/txlweb/TextReader-AndroidEdition](https://github.com/txlweb/TextReader-AndroidEdition)

## 一些建议

建议使用V2格式的小说或使用epub格式文件直接导入,不要再制作V1小说了!

关于共享,建议不要在公共网环境打开,内网可以互传小说(现在这个功能已经被防火墙取代,可以选择是否非本机访问(默认不允许)).

不建议使用PDF文件,因为会生成很多图片,占用空间.

关于AI朗读:现在已经加入了双缓存,可以不卡的阅读,目前可以用get接口,post类接口不能用
## 一些帮助
* 新东西:AI总结

目前不完善，目前可用的：本地部署的ollama模型，可以自己配置，其他详见“命令行帮助”部分

* 新东西:插件

目前只是作为一个测试,我已经写过了几个插件在plugin目录下,可以借鉴一下.同时他的安装和管理都还没有写好.

* 新东西:NextUI测试

这个测试是集成仅进最新版的程序里的,可以通过安装一个插件来启用这个测试功能,这个NextUI还有诸多问题,而且部分内容并不稳定(主要因为ajax请求速度不一致导致章节错乱)
这只是一次测试,如果发现问题或能够帮助我们改进,请尽量提出.NextUI与插件暂时不兼容(NextUI本身只是调用了原UI的接口)

> NAME:插件名
> 
>  BY:作者
> 
>  @JS-START
> 
> 这里写JavaScript代码
> 
> @JS-END

* 使用以下命令可以启动服务

> java -jar TextReaderJavaEdition.jar

* 默认的服务端口

> 8080 和 8090

* 修改配置文件

> config.ini<br>
>> [settings]<br>
> > MainPath=./rom<br>
> > Port=8080 #端口<br>
> > LogRank=1 #日志等级 0=禁止日志 1=用户级日志 2=调试级日志<br>
> > UseShare=enable #启用分享服务(启用这个会在默认8090端口分享你本机的小说)<br>

> config_share.ini<br>
>> [settings]<br>
> > Port=8090 #端口<br>
> > LogRank=1 #日志等级 0=禁止日志 1=用户级日志 2=调试级日志<br>

> resource.ini (小说信息)<br>
>> [conf]<br>
> > icon=icon.jpg<br>
> > title=测试V2<br>
> > by=测试作者2<br>
> > ot=测试简介2 测试简介 测试简介 测试简介 测试简介<br>

* 命令行帮助

> java -jar TextReaderJavaEdition.jar
>> 导入txt文件 -m, -make txt文件名 保存的压缩包 小说标题 小说图片 切章规则(.*第.**章.* * )别复制,自己手打<br>
> 导出txt文件 -o, -out 小说名 导出的txt名<br>
> 导入pdf文件 -p, -pdfmake pdf文件名 索引(-为自动索引) 保存的压缩包 标题 图片 作者 简介 (共计8个参数)<br>
> 导入epub文件 -b, -epubmake epub文件名 (共计2个参数)<br>
> 导入teip文件(V1&V2) -a, -add 文件名<br>
> 清理临时目录(可能修复问题) -r, -clear (共计1个参数)<br>
> AI总结小说(BETA)        -s, -summary 小说名 模型配置 导出的txt名 (共计4个参数)<br>
> 如果你不知道模型配置应该填什么，那就填ollama::deepseek-r1:1.5b。<br>
> 更改设置 -c, -config 键 值 (与上文配置文件一致)<br>
> 制作文件/编码索引(提速) -e, -encode (共计1个参数)<br>


* AI总结

目前API写死在代码里了，已经接入了本地ollama的接口，openai的接口可能可用（未测试，没有可用的api）



* 加入小说

> 可以在设置页里打开文件选择器来安装一个打包好的文件 或 从一个URL获取列表并下载安装 或 使用切章器操作txt.

* 关于共享

> 默认打开共享,如果您有能力的话可以创建一个共享节点,并加入到/style/API_list.json中,作者每次编译都会检查这里面的链接是否有效,有效的则会被编译进程序.

## 常见问题

* 打开白屏/不正确显示

  权限不足，无法创建文件和目录。
* 无法启动报错

  由于端口被占用或在没有su权限时端口受限（linux/android），修改配置文件就行了
* 安装小说文件

  可以在设置页里打开文件选择器来安装一个打包好的文件 或 从一个URL获取列表并下载安装 或 使用切章器操作txt.
* 怎么安装ollama

 在官网 https://ollama.com/ 上下载安装，并安装。
 
在命令行中执行：
> ollama run deeepseek-r1:1.5b

这个几乎所有电脑都能用，但是你的电脑够好可以用更好的。

# AI总结开启，使用方案

安装ollama

在官网 https://ollama.com/ 上下载安装，并安装。

在命令行中执行：
> ollama run deeepseek-r1:1.5b

运行本程序，配置参数，等待输出

> java -jar TextReaderJavaEdition.jar -s 小说名 ollama::deepseek-r1:1.5b 输出文件名

如果不出意外，一段时间后（根据文章长度，大概30分钟？）打开文件，查看结果。