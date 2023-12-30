# 关于TeipReader JavaEdition
这是一款由IDlike制作的小说阅读器，使用java编写服务端。<br>
其他的内容还在开发，莫急
## 作者在GitHub上
作者[https://github.com/txlweb](https://github.com/txlweb)

这个仓库[https://github.com/txlweb/TextReaderJE/](https://github.com/txlweb/TextReaderJE/)

## 一些建议
建议使用DEBUG版本,因为这个版本不会强制审查style,从而可以使用插件.

建议使用V2格式的小说或使用epub格式文件直接导入,不要再制作V1小说了!

关于共享,建议不要在公共网环境打开,内网可以互传小说.

不建议使用PDF文件,因为会生成很多图片,占用空间.



## 一些帮助
* 使用以下命令可以启动服务
> java -jar TextReaderJavaEdition.jar
* 默认的服务端口
> 8080 和 8090
* 修改配置文件
> config.ini<br>
>> [settings]<br>
>> MainPath=./rom<br>
>> Port=8080 #端口<br>
>> LogRank=1 #日志等级 0=禁止日志 1=用户级日志 2=调试级日志<br>
>> UseShare=enable #启用分享服务(启用这个会在默认8090端口分享你本机的小说)<br>

> config_share.ini<br>
>> [settings]<br>
>> Port=8090 #端口<br>
>> LogRank=1 #日志等级 0=禁止日志 1=用户级日志 2=调试级日志<br>

> resource.ini (小说信息)<br>
>> [conf]<br>
>> icon=icon.jpg<br>
>> title=测试V2<br>
>> by=测试作者2<br>
>> ot=测试简介2 测试简介 测试简介 测试简介 测试简介<br>
* 命令行帮助
> java -jar TextReaderJavaEdition.jar
>> 导入txt文件 -m, -make txt文件名 保存的压缩包 小说标题 小说图片 切章规则(.*第.**章.* * )别复制,自己手打<br>
> 导出txt文件 -o, -out 小说名 导出的txt名<br>
> 导入pdf文件 -p, -pdfmake pdf文件名 索引(-为自动索引) 保存的压缩包 标题 图片 作者 简介 (共计8个参数)<br>
> 导入epub文件 -b, -epubmake epub文件名 (共计2个参数)<br>
> 导入teip文件(V1&V2) -a, -add 文件名<br>
> 更改设置 -c, -config 键 值 (与上文配置文件一致)<br>
* 加入小说
> 可以在设置页里打开文件选择器来安装一个打包好的文件 或 从一个URL获取列表并下载安装 或 使用切章器操作txt.
* 关于共享
> 默认打开共享,如果您有能力的话可以创建一个共享节点,并加入到/style/API_list.json中,作者每次编译都会检查这里面的链接是否有效,有效的则会被编译进程序.

## 常见问题
* 打开白屏/不正确显示<br>
权限不足，无法创建文件和目录。
* 无法启动报错<br>
由于端口被占用或在没有root权限时端口受限，修改配置文件就行了
* 安装小说文件<br>
可以在设置页里打开文件选择器来安装一个打包好的文件 或 从一个URL获取列表并下载安装 或 使用切章器操作txt.