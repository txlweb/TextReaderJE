# 如何编译native版本的可执行文件

## 编译后 - 会得到什么？


### -> 构建exe

会在out/artifacts/c__build_installer下生成setup.exe

### -> 清理构建

会删除out/artifacts/c__build_installer目录




## 编译前 - 环境需求

1. visual studio 2022

不需要特别注意，在安装时 **必须** 勾选 **“使用C++的桌面开发”**

必须安装在默认的位置，否则不能再正确加载vc env，你需要手动加载它再编译（不能自动编译）

3. graalvm-jdk-23.0.2+7.1

这个需要你自己配置位置，也必须把 **项目SDK** 设置的与它一致

## 编译前 - 配置环境

编辑 env.cmd,以下是默认的配置

>set BUILD_JDK="C:\Program Files\Java\graalvm-jdk-23.0.2+7.1\bin"
> 
>set WRK_SPASE="I:\JavaIDE\TextReaderJE\out\artifacts\c__build"


7-zip可以不配置，如果你的vc不是安装再默认位置，需要自己加载环境变量（自己百度去）

在IDEA中运行配置”构建exe“就行了，别直接运行（因为一些谜之问题，直接运行不能正确cd到工作目录，也许你们的环境可以？）

## 编译时 - 大概需要多久？

Epyc7302*2 + 64 Gib Memory ，大概2分钟左右完成编译

