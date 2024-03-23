@echo off
setlocal enabledelayedexpansion  
:menu
cls
color 24
echo TextReaderJE 插件下载器 by.IDlike
echo.
echo 源站:github   如果不能下载请挂梯子(国内时不时就抽风不能下载)
color 2F
echo.
echo 输入1 下载 "推荐套餐"
echo 包含插件: 
echo #1 SFD 对话小说阅读器组件
echo #2 FANYI 点击翻译插件
echo #3 SCROLLBARFIX 滚动修复!!
echo #4 PLUGINMGR 插件管理器
echo.
echo.
echo 输入2 下载 "个性套餐"
echo 包含插件: 
echo #1 use_only_one_page_progress 使用仅"当前页"阅读进度条
echo.
echo.
echo 输入3 下载 "开发套餐"
echo 包含插件: 
echo #1 DEBUGGER 添加一个控制台
echo.
echo.
echo 输入4 下载 "内部测试"
echo 包含插件:
echo #1 newUI 新UI(测试版)
echo.
echo.
echo.
echo 请输入您的选择 (1: 推荐套餐, 2: 个性套餐, 3: 开发套餐, 4:内部测试, U: 更新插件列表, Q: 退出):



set /p choice=

if "%choice%"=="1" (



    echo 正在下载:   SFD 对话小说阅读器组件 
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/SFD.pluginJS SFD.pluginJS
    if exist "SFD.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "SFD.pluginJS" ".\plugin\SFD.pluginJS"
        echo 已下载 : SFD.pluginJS
    ) else (
        echo 下载失败!请尝试挂个梯子再下!
    )

    echo 正在下载:   FANYI 点击翻译插件
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/fanyi.pluginJS fanyi.pluginJS
    if exist "fanyi.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "fanyi.pluginJS" ".\plugin\fanyi.pluginJS"
        echo 已下载 : fanyi.pluginJS
    ) else (
        echo 下载失败!请尝试挂个梯子再下!
    )

    echo 正在下载:   SCROLLBARFIX 滚动修复!!
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/scrollbar_fix.pluginJS scrollbar_fix.pluginJS
    if exist "scrollbar_fix.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "scrollbar_fix.pluginJS" ".\plugin\scrollbar_fix.pluginJS"
        echo 已下载 : scrollbar_fix.pluginJS
    ) else (
        echo 下载失败!请尝试挂个梯子再下!
    )

    echo 正在下载:   PLUGINMGR 插件管理器
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/plugin_mgr.pluginJS plugin_mgr.pluginJS
    if exist "plugin_mgr.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "plugin_mgr.pluginJS" ".\plugin\plugin_mgr.pluginJS"
        echo 已下载 : plugin_mgr.pluginJS
    ) else (
        echo 下载失败!请尝试挂个梯子再下!
    )

    echo 下载已经完成,如果上述项目中存在没有完成的项目,请尝试更换网络环境或以管理员身份运行.


) else if "%choice%"=="2" (

    echo 正在下载:   use_only_one_page_progress 使用仅"当前页"阅读进度条
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/use_only_one_page_progress.pluginJS use_only_one_page_progress.pluginJS
    if exist "use_only_one_page_progress.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "use_only_one_page_progress.pluginJS" ".\plugin\use_only_one_page_progress.pluginJS"
        echo 已下载 : use_only_one_page_progress.pluginJS
    ) else (
        echo 下载失败!请尝试挂个梯子再下!
    )
    echo 下载已经完成,如果上述项目中存在没有完成的项目,请尝试更换网络环境或以管理员身份运行.


) else if "%choice%"=="3" (

    echo 正在下载:   DEBUGGER 添加一个控制台
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/plugin_debugger.pluginJS plugin_debugger.pluginJS
    if exist "plugin_debugger.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "plugin_debugger.pluginJS" ".\plugin\plugin_debugger.pluginJS"
        echo 已下载 : plugin_debugger.pluginJS
    ) else (
        echo 下载失败!请尝试挂个梯子再下!
    )
    echo 下载已经完成,如果上述项目中存在没有完成的项目,请尝试更换网络环境或以管理员身份运行.


) else if "%choice%"=="3" (

     echo 正在下载:   DEBUGGER 添加一个控制台
     certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/newUI.pluginJS plugin_debugger.pluginJS
     if exist "newUI.pluginJS" (
         if not exist ".\plugin" mkdir ".\plugin"
         copy "newUI.pluginJS" ".\plugin\newUI.pluginJS"
         echo 已下载 : newUI.pluginJS
     ) else (
         echo 下载失败!请尝试挂个梯子再下!
     )
     echo 下载已经完成,如果上述项目中存在没有完成的项目,请尝试更换网络环境或以管理员身份运行.


 ) else if "%choice%"=="Q" (
    echo 退出程序  
    exit /b  
) else if "%choice%"=="U" (
     cls
     echo 正在下载更新...
     del /f plugin_download_base64.txt
     certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin_download_base64.txt plugin_download_base64.txt
     if exist "plugin_download_base64.txt" (
         echo del /f plugin_download.cmd > t.cmd
         echo certutil -decode plugin_download_base64.txt plugin_download.cmd >> t.cmd
         start t.cmd
     ) else (
         echo 更新失败!请检查网络环境!
     )
     echo 下载完成,按任意键重启
     pause
     start plugin_download.cmd
     exit /b
 ) else (

    goto menu  
)  
pause  
exit /b