@echo off
setlocal enabledelayedexpansion  
:menu
cls
color 24
echo TextReaderJE ��������� by.IDlike
echo.
echo Դվ:github   ������������������(����ʱ��ʱ�ͳ�粻������)
color 2F
echo.
echo ����1 ���� "�Ƽ��ײ�"
echo �������: 
echo #1 SFD �Ի�С˵�Ķ������
echo #2 FANYI ���������
echo #3 SCROLLBARFIX �����޸�!!
echo #4 PLUGINMGR ���������
echo.
echo.
echo ����2 ���� "�����ײ�"
echo �������: 
echo #1 use_only_one_page_progress ʹ�ý�"��ǰҳ"�Ķ�������
echo.
echo.
echo ����3 ���� "�����ײ�"
echo �������: 
echo #1 DEBUGGER ���һ������̨
echo.
echo.
echo.
echo ����������ѡ�� (1: �Ƽ��ײ�, 2: �����ײ�, 3: �����ײ�, U: ���²���б�, Q: �˳�):



set /p choice=

if "%choice%"=="1" (



    echo ��������:   SFD �Ի�С˵�Ķ������ 
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/SFD.pluginJS SFD.pluginJS
    if exist "SFD.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "SFD.pluginJS" ".\plugin\SFD.pluginJS"
        echo ������ : SFD.pluginJS
    ) else (
        echo ����ʧ��!�볢�ԹҸ���������!
    )

    echo ��������:   FANYI ���������
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/fanyi.pluginJS fanyi.pluginJS
    if exist "fanyi.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "fanyi.pluginJS" ".\plugin\fanyi.pluginJS"
        echo ������ : fanyi.pluginJS
    ) else (
        echo ����ʧ��!�볢�ԹҸ���������!
    )

    echo ��������:   SCROLLBARFIX �����޸�!!
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/scrollbar_fix.pluginJS scrollbar_fix.pluginJS
    if exist "scrollbar_fix.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "scrollbar_fix.pluginJS" ".\plugin\scrollbar_fix.pluginJS"
        echo ������ : scrollbar_fix.pluginJS
    ) else (
        echo ����ʧ��!�볢�ԹҸ���������!
    )

    echo ��������:   PLUGINMGR ���������
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/plugin_mgr.pluginJS plugin_mgr.pluginJS
    if exist "plugin_mgr.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "plugin_mgr.pluginJS" ".\plugin\plugin_mgr.pluginJS"
        echo ������ : plugin_mgr.pluginJS
    ) else (
        echo ����ʧ��!�볢�ԹҸ���������!
    )

    echo �����Ѿ����,���������Ŀ�д���û����ɵ���Ŀ,�볢�Ը������绷�����Թ���Ա�������.


) else if "%choice%"=="2" (

    echo ��������:   use_only_one_page_progress ʹ�ý�"��ǰҳ"�Ķ�������
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/use_only_one_page_progress.pluginJS use_only_one_page_progress.pluginJS
    if exist "use_only_one_page_progress.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "use_only_one_page_progress.pluginJS" ".\plugin\use_only_one_page_progress.pluginJS"
        echo ������ : use_only_one_page_progress.pluginJS
    ) else (
        echo ����ʧ��!�볢�ԹҸ���������!
    )
    echo �����Ѿ����,���������Ŀ�д���û����ɵ���Ŀ,�볢�Ը������绷�����Թ���Ա�������.


) else if "%choice%"=="3" (

    echo ��������:   DEBUGGER ���һ������̨
    certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin/plugin_debugger.pluginJS plugin_debugger.pluginJS
    if exist "plugin_debugger.pluginJS" (
        if not exist ".\plugin" mkdir ".\plugin"
        copy "plugin_debugger.pluginJS" ".\plugin\plugin_debugger.pluginJS"
        echo ������ : plugin_debugger.pluginJS
    ) else (
        echo ����ʧ��!�볢�ԹҸ���������!
    )
    echo �����Ѿ����,���������Ŀ�д���û����ɵ���Ŀ,�볢�Ը������绷�����Թ���Ա�������.


) else if "%choice%"=="Q" (
    echo �˳�����  
    exit /b  
) else if "%choice%"=="Q" (
     echo �������ظ���
     certutil -urlcache -split -f https://raw.githubusercontent.com/txlweb/TextReaderJE/master/plugin_download.cmd plugin_download.cmd
     start plugin_download.cmd
     exit /b
 ) else (

    goto menu  
)  
pause  
exit /b