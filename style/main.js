let HasFastNextPage = true;
let body, sw, sr_b, sr_r, timex;
let clock1;
let info;
let configs;
//启动项
window.onload = function () {
    Init();
    updateScrollProgress();
}

//window.onload =  function (){inline_init();}


function Init() {
    //加载配置文件
    var xhrx = new XMLHttpRequest();
    xhrx.onreadystatechange = function () {
        if (xhrx.readyState == 4) {
            configs = JSON.parse(xhrx.responseText);
        }
    }
    xhrx.open("get", "/config.json", true);
    xhrx.send(null);
    //开始初始化
    let childrens;
    body = document.getElementById('body');
    sw = document.getElementById('settingwindow');
    sr_b = document.getElementById('shb');
    sr_r = document.getElementById('shr');
    timex = document.getElementById('time');
    //if (getCookie('bs') !== 'TRUE') {
        //alert('为确保您能流畅的使用本软件,建议您使用Chrome浏览器.如果您正在使用chrome浏览器则忽视这则消息.这则消息仅会显示一次.');
        //setCookie('bs', 'TRUE', 60 * 60 * 24 * 365);
    //}
    info = function () {
        console.log('TextReader BETA', "\n    _..,----,.._\n .-;'-.,____,.-';\n(( |            |\n `))            ; \n  ` \          /\n  .-' `,.____.,' '-.\n(     '------'     )\n `-=..________..-\n 要来一杯Java吗? [Dog]");
        console.log('TextReader by.IDSOFT@IDlike');
        console.log('JavaEdition(WebCore) Vre.2.7.0-240202 update at 2024/2/2');
        console.log('本软件仅供学习交流使用,且在github上开源! https://github.com/txlweb/TextReaderJE');
    }
    info()
    const poinmain = document.getElementById('poin');
    console.log(poinmain)
    if (!poinmain) {
        var chagepage = document.getElementById('chagepagebar');
        chagepage.innerHTML = '';
        HasFastNextPage = false;
        //取数据
        let innerHtmlContent = document.getElementById('textbar');
        childrens = innerHtmlContent.children;
        for (let i = 0; i < childrens.length; i++) {
            if (childrens[i].tagName === 'H1' || childrens[i].tagName === 'h1') {
                //总共就1个h1标签表示标题,需要去除" 的章节列表"
                if (parseInt(getCookie(childrens[i].innerHTML)) !== parseInt(getCookie('nanxspwss'))) {
                    if (getUrlParam('idx') == null && window.location.href.slice(-2) !== 'VH' && getCookie(childrens[i].innerHTML)) {
                        //调整IDX,直接回到上回浏览位置
                        changeURLStatic('idx', parseInt(getCookie(childrens[i].innerHTML)));
                    }
                }
                //break;
            }
        }
    } else {
        //存记录
        let innerHtmlContent = document.getElementById('textbar');
        childrens = innerHtmlContent.children;
        for (let i = 0; i < childrens.length; i++) {
            if (childrens[i].tagName === 'H1' || childrens[i].tagName === 'h1') {
                //总共就1个h1标签表示标题
                setCookie(childrens[i].innerHTML, parseInt(document.getElementById('poin').getAttribute('now')), 60 * 60 * 24 * 365);
                break;
            }
        }
    }
    SetFontSize();
    InitMark();
    load();
    //Load Cookie Image
    document.getElementById('image').value = getCookie('BGImage');
    document.getElementById('bg').style.backgroundImage = 'url(' + getCookie('BGImage') + ')';
    //document.getElementById('maintext') //这个是正文框
    if (getUrlParam('idx') != null) {
        var t = setTimeout("IndexChageIdx()", 500); //目录页便捷索引,延时因为他太早加载不出来

    }
    InitPoin(poinmain); //加载进度条document.getElementById('poin')
    //init setting window style
    setInterval("viewchage();", 100);
    REGmonitor();
    var btn = document.getElementById('LRMODECHAGE');
    if (getCookie('LRMODE') === 'TRUE') {
        btn.innerHTML = 'ON';
        document.getElementById('LP').style.display = '';
        document.getElementById('NP').style.display = '';
        clock1 = setInterval(function () {
            document.body.parentNode.style.overflow = "hidden";
        }, 100);
    } else {
        btn.innerHTML = 'OFF';
        document.getElementById('LP').style.display = 'none';
        document.getElementById('NP').style.display = 'none';
    }
    btn = document.getElementById('AJMODECHAGE');
    if (getCookie('AJMODE') === 'FALSE') {
        btn.innerHTML = 'OFF';
    } else {
        setCookie('AJMODE', 'TRUE', 60 * 60 * 24 * 365);
        btn.innerHTML = 'ON';
    }
    btn = document.getElementById('AISPEECH');
    if (getCookie('AISPEECH') === 'FALSE') {
        btn.innerHTML = 'FALSE';
        document.getElementById("AISPEECH_VIEW").style.display="none"
    } else {
        btn.innerHTML = 'TRUE';
    }

    if (!getCookie("MarginTopPxs"))
        setCookie("MarginTopPxs", 20, 60 * 60 * 24 * 365);
    //处理黑夜模式
    if (getCookie('black_mode') === 'YES') {
        document.getElementById('black_mode').innerHTML = "白日模式";
        document.getElementById('settingwindow').style.backgroundColor = "#212121";
        document.getElementById('settingwindow').style.color = "#c2c2c2";
        document.getElementById('textbar').style.backgroundColor = "#212121";
        document.getElementById('textbar').style.color = "#c2c2c2";
        document.body.style.backgroundColor = "#030303";

        const childrens = document.getElementById('textbar').children;
        for (let i = 0; i < childrens.length; i++) {
            if (childrens[i].className==="book_list") {
                childrens[i].className="book_list black_mode"
            }
        }
    }
    SetBrs();
    setInterval("clock_up();", 500);
    inline_init();
    //补丁-AI总结
    t=document.getElementById("title");
    if(t){
        t.innerHTML="章节目录 <br><a onclick='aisumm(this)'>点击用AI总结本文！</a>"
    }
}
function aisumm(e){
    e.onclick = null;
    e.innerHTML="<iframe src='' id='fffppp' style='width: 100%'>"
    ixx=0
    document.getElementById("fffppp").onload = function (){
        document.getElementById("fffppp").src="./summary.html?pp="+ixx
        ixx++
    }
    document.getElementById("fffppp").src='./summary.html?deepseek::sk-3fc57eba21ea4eb18b1b59ac6bdae1d6'
}
function clock_up() {
    let time = new Date();
    timex.innerHTML = time.toLocaleString();
    let innerHtmlContent = document.getElementById('textbar');
    const childrens = innerHtmlContent.children;
    for (let i = 0; i < childrens.length; i++) {
        if (childrens[i].tagName === 'A' || childrens[i].tagName === 'a') {
            if (childrens[i].children[0]) {
                if(childrens[i].children[0].getAttribute('loaded') !== "yes"){
                    childrens[i].children[0].src = childrens[i].children[0].getAttribute('res');
                    childrens[i].children[0].setAttribute("loaded","yes");
                }

            }
        }
    }
}

function NextPage() {
    document.body.scrollTop = document.body.scrollTop + (window.screen.availHeight - 575);
}

function LastPage() {
    document.body.scrollTop = document.body.scrollTop - (window.screen.availHeight - 575);
}

document.addEventListener("keyup", function (event) {
    // 判断释放的键是否是回车键（键码为13）
    if (event.keyCode === 38 || event.keyCode === 37) {
        LastPage();
    }
    if (event.keyCode === 39 || event.keyCode === 40) {
        NextPage();
    }
});

function LRMODE(btn) {
    if (getCookie('LRMODE') === 'TRUE') {
        setCookie('LRMODE', 'FALSE', 60 * 60 * 24 * 365);
        btn.innerHTML = 'OFF';
        location.reload(false);//不需要重读
    } else {
        setCookie('LRMODE', 'TRUE', 60 * 60 * 24 * 365);
        btn.innerHTML = 'ON';
        location.reload(false);//不需要重读
    }
}

function AJMODE(btn) {
    if (getCookie('AJMODE') === 'TRUE') {
        setCookie('AJMODE', 'FALSE', 60 * 60 * 24 * 365);
        btn.innerHTML = 'OFF';
    } else {
        setCookie('AJMODE', 'TRUE', 60 * 60 * 24 * 365);
        btn.innerHTML = 'ON';
    }
}
function AISPEECH(btn){
    if (getCookie('AISPEECH') === 'TRUE') {
        setCookie('AISPEECH', 'FALSE', 60 * 60 * 24 * 365);
        btn.innerHTML = 'FALSE';
    } else {
        setCookie('AISPEECH', 'TRUE', 60 * 60 * 24 * 365);
        btn.innerHTML = 'TRUE';
    }
    window.location.href=window.location.href
}
function viewchage() {
    //位置调整
    sr_b.style.left = document.body.scrollWidth / 2 - sr_b.scrollWidth / 2;
    sr_r.style.left = document.body.scrollWidth / 2 - sr_r.scrollWidth / 2 - 10;
    sr_r.style.width = sr_b.scrollWidth;
    if (sw.scrollWidth !== 0) { //检测display的
        sw.style.left = document.body.scrollWidth / 2 - sw.scrollWidth / 2;
        sw.style.top = document.body.clientHeight / 2 - sw.scrollHeight / 2;

    } else {
        sw.style.left = document.body.scrollWidth / 2 - 200
        sw.style.top = -100
    }
    //文本颜色
    body.style.color = getCookie('TextColor');
}

function sreach(innervalue) {
    var element = document.getElementById('load');
    element.className = 'load';
    sr_r.className = 'shr';
    document.getElementById('shrd').className = '';
    sr_r.innerHTML = '';
    let innerHtmlContent = document.getElementById('pagelist');
    const childrens = innerHtmlContent.children;
    for (let i = 0; i < childrens.length; i++) {
        if (childrens[i].tagName === 'A' || childrens[i].tagName === 'a') {
            if (childrens[i].innerHTML.indexOf(innervalue) !== -1) {
                sr_r.innerHTML = sr_r.innerHTML + '<a href=' + childrens[i].href + '>' + childrens[i].innerHTML + '</a><hr>';
            }
        }
    }
    load();
    element.className = 'displaynone';
}

function ChageTextColor(Color) {
    setCookie('TextColor', Color, 60 * 60 * 24 * 365);
    location.reload(false);//不需要重读,只是改文本颜色而已.
}

function REGmonitor() {
    //监听器

}

function spcolor() {
    var color = prompt("自定义颜色码(示例:#f5aec0)");
    if (color === '') return;
    setCookie('bgc', color, 60 * 60 * 24 * 365);
    setCookie('style', 'NOTHING', 60 * 60 * 24 * 365);
    load();
    load();
}

function vwlist(URL) {
    URL = URL + '?NOUI';
    const pl = document.getElementById('pl');
    const list = document.getElementById('pagelist');
    if (pl.hidden === true) {
        document.body.parentNode.style.overflow = "hidden";
        if (list.innerHTML === '') {
            load();
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    list.innerHTML = xhr.responseText;
                    changeURLStatic('idx', parseInt(document.getElementById('poin').getAttribute('now')));
                    IndexChageIdx_inpage();
                    load();
                }
            }
            xhr.open("get", URL, true);
            xhr.send(null);
            pl.hidden = false;
        } else {
            load();
            pl.hidden = false;
            load();
        }
    } else {
        document.body.parentNode.style.overflow = "auto";
        list.innerHTML = ''
        load();
        pl.hidden = true;
        //地址栏去除?后标志,防止返回空页面
        history.replaceState(null, null, window.location.href.split("?")[0]);
        load();
    }
}

function SetBrs() {
    let ps = document.getElementsByTagName("P");//[1].style.marginTop
    for (let i = 0; i < ps.length; i++)
        ps[i].style.marginTop = getCookie("MarginTopPxs") + 'px';
    document.getElementById('psize').innerHTML = getCookie("MarginTopPxs") + 'px';
}

function MarginSizeAdd() {
    setCookie("MarginTopPxs", parseInt(getCookie("MarginTopPxs")) + 1, 60 * 60 * 24 * 365);
    SetBrs();
}

function MarginSizeRemove() {
    setCookie("MarginTopPxs", parseInt(getCookie("MarginTopPxs")) - 1, 60 * 60 * 24 * 365);
    SetBrs();
}

function IndexChageIdx() {
    const idxx = getUrlParam('idx');
    let innerHtmlContent = document.getElementById('textbar');
    const childrens = innerHtmlContent.children;
    for (let i = 0; i < childrens.length; i++) {
        if (childrens[i].tagName === 'A' || childrens[i].tagName === 'a') {
            if (parseInt(childrens[i].getAttribute('idx')) == idxx) {

                document.body.scrollTop = childrens[i].offsetTop - 100;
                childrens[i].style.backgroundColor = '#fe620d';
                childrens[i].style.color = 'white';
                changeURLStatic('idx', "");
                return childrens[i].offsetTop; //防卡,直接跳出循环.
            }
        }
    }

}

function IndexChageIdx_inpage() {
    const idxx = getUrlParam('idx');
    let innerHtmlContent = document.getElementById('pagelist');
    const childrens = innerHtmlContent.children;
    for (let i = 0; i < childrens.length; i++) {
        if (childrens[i].tagName === 'A' || childrens[i].tagName === 'a') {
            if (parseInt(childrens[i].getAttribute('idx')) == idxx) {//千万不能是===,否则这个函数没效果!!!
                innerHtmlContent.scrollTop = childrens[i].offsetTop - 100;
                childrens[i].style.backgroundColor = '#fe620d';
                childrens[i].style.color = 'white';
                return childrens[i].offsetTop; //防卡,直接跳出循环.
            }
        }
    }
}

function InitPoin(main) {
    if (!main) return;
    const max = parseInt(main.getAttribute('max')); //获取自定义参数-max(最大值)
    const now = parseInt(main.getAttribute('now')); //获取自定义参数-now(当前值)
    main.innerHTML = '<div class="poinmain" id="poinmain"></div>'; //向自定义标签内插入进度条(图1)
    const poinbody = document.getElementById('poinmain'); //获取进度条对象
    poinbody.style.width = main.clientWidth * (now / max) + "px"; //设置进度条宽度
    const poins = now / max * 100; //计算百分比
    poinbody.innerHTML = poins.toFixed(2) + '%'; //显示进度
    main.innerHTML = main.innerHTML + '<t class="pointext">' + now + '/' + max + '</t>';
}

function load() {
    var element = document.getElementById('load');
    if (getCookie('style') === 'NOTHING') {
        document.getElementById('body').style.backgroundColor = getCookie('bgc')
    }
    document.getElementById('textbar').className = 'texter ' + getCookie('style');
    if (element.className === 'load') {
        element.className = 'load displaynone';
    } else {
        element.className = 'load';
    }
}
function BM(){
    if (getCookie('black_mode') === 'YES') {
        setCookie('black_mode', 'NO', 60 * 60 * 24 * 365);
    }else{
        setCookie('black_mode', 'YES', 60 * 60 * 24 * 365);
    }
    window.location.href=window.location.href;
}
function chagestyle(goew) {
    setCookie('style', goew, 60 * 60 * 24 * 365);
    document.getElementById('textbar').className = 'texter ' + getCookie('style');
}

function DisplaySetting() { //è?????
    const element = document.getElementById('setting');
    const button = document.getElementById('set_btn');
    if (element.hidden === true) {
        document.body.parentNode.style.overflow = "hidden";
        s_view(element);
        element.hidden = false;
        button.className = 'hw hover book_block';
    } else {
        document.body.parentNode.style.overflow = "auto";
        s_display(element);
        element.hidden = true;
        button.className = 'hw book_block';
    }
}

function s_view(el) {
    el.style.display = 'block';
}

function s_display(el) {
    el.style.display = 'none';
}

function InitMark() {
    const mark = document.getElementById('mark');
    const mark_ = document.getElementById('mark_off');
    if (getCookie(window.location.href) === 'MARKED.') {
        mark.hidden = true;
        mark_.hidden = false;
    }
}

function ChageBGImage() {
    var ImageSRC = document.getElementById('image');
    setCookie('BGImage', ImageSRC.value, 60 * 60 * 24 * 365);
    window.location.href = window.location.href;
}

function CenterMark() { //êé??
    var mark = document.getElementById('mark');
    var mark_ = document.getElementById('mark_off');
    if (mark_.hidden === true) {
        mark.hidden = true;
        mark_.hidden = false;
        setCookie(window.location.href, 'MARKED.', 60 * 60 * 24 * 365);

    } else {
        mark.hidden = false;
        mark_.hidden = true;
        setCookie(window.location.href, 'NOMARKED.', 60 * 60 * 24 * 365);
        clearCookie(window.location.href);
    }
}

function ViewFontSize() {
    load();
    setTimeout("load()", 10);
    const view = document.getElementById('size');
    view.innerHTML = parseInt(getCookie("FontSize")) + 'px';
    if (view.innerHTML === 'NaNpx') {
        setCookie("FontSize", 22, 60 * 60 * 24 * 365);
        SetFontSize();
    }
}

function FontSizeAdd() {
    const dp = document.getElementById('textbar');
    dp.style.fontSize = parseInt(getCookie("FontSize")) + 1;
    setCookie("FontSize", parseInt(dp.style.fontSize), 60 * 60 * 24 * 365);
    ViewFontSize();
}

function FontSizeRemove() {
    const dp = document.getElementById('textbar');
    dp.style.fontSize = parseInt(getCookie("FontSize")) - 1;
    setCookie("FontSize", parseInt(dp.style.fontSize), 60 * 60 * 24 * 365);
    ViewFontSize();
}

function SetFontSize() {
    var dp = document.getElementById('textbar');
    dp.style.fontSize = parseInt(getCookie("FontSize"));
    setCookie("FontSize", parseInt(dp.style.fontSize), 60 * 60 * 24 * 365);
    ViewFontSize();
}

//Lib_cookie do
function getCookie(name) {
    const nameEQ = name + '=';
    const ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1, c.length)
        }
        if (c.indexOf(nameEQ) === 0) {
            return unescape(c.substring(nameEQ.length, c.length))
        }
    }
    return false
}

function clearCookie(name) {
    setCookie(name, "", -1);
}

function setCookie(name, value, seconds) {
    seconds = seconds || 0;
    let expires = "";
    if (seconds !== 0) {
        var date = new Date();
        date.setTime(date.getTime() + (seconds * 1000));
        expires = "; expires=" + date.toGMTString();
    }
    document.cookie = name + "=" + escape(value) + expires + "; path=/";
}

function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg); //匹配目标参数
    if (r != null) return decodeURI(r[2]);
    return null; //返回参数值
}

function changeURLStatic(name, value) {
    let url = changeURLParam(location.href, name, value); // 修改 URL 参数
    history.replaceState(null, null, url); // 替换地址栏
}

function changeURLParam(url, name, value) {
    if (typeof value === 'string') {
        value = value.toString().replace(/(^\s*)|(\s*$)/, ""); // 移除首尾空格
    }
    let url2;
    if (!value) { // remove
        let reg = eval('/(([\?|&])' + name + '=[^&]*)(&)?/i');
        let res = url.match(reg);
        if (res) {
            if (res[2] && res[2] === '?') { // before has ?
                if (res[3]) { // after has &
                    url2 = url.replace(reg, '?');
                } else {
                    url2 = url.replace(reg, '');
                }
            } else {
                url2 = url.replace(reg, '$3');
            }
        }
    } else {
        let reg = eval('/([\?|&]' + name + '=)[^&]*/i');
        if (url.match(reg)) { // edit
            url2 = url.replace(reg, '$1' + value);
        } else { // add
            let reg = /([?](\w+=?)?)[^&]*/i;
            let res = url.match(reg);
            url2 = url;
            if (res) {
                if (res[0] !== '?') {
                    url2 += '&';
                }
            } else {
                url2 += '?';
            }
            url2 += name + '=' + value;
        }
    }
    return url2;
}

function TextGetLeft(obj, kw) {
    const index = obj.indexOf(kw);
    obj = obj.substring(0, index);
    return obj;
}

//插件-搜索
function inline_s() {
    window.location.href = "?" + document.getElementById("search_in").value;
}

function inline_init() {
    const xhr = new XMLHttpRequest();
    const fast_tag = document.getElementById("fast-tag");
    if (!fast_tag) return;
    fast_tag.innerHTML = "";
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            const obj = JSON.parse(xhr.responseText);
            if (obj.code !== "0") {
                alert("E:没有配置文件,请参考手册配置.");
                return;
            }
            for (let i = 0; i < obj.conf_tags.length; i++) {
                if (obj.conf_tags[i].name !== "") {
                    fast_tag.innerHTML = fast_tag.innerHTML + "<a class='book_block' href='?" + obj.conf_tags[i].key + "'>" + obj.conf_tags[i].name + "</a>";
                }
            }
        }
    }
    xhr.open('get', "/config.json");
    xhr.send(null);
}
var AIurls;
var paly_id = 0;
var paly_clock;
function AI_speak(){
    if(paly_id!==0){
        paly_id = 0;
        window.clearInterval(paly_clock);
        document.getElementById("AI_isOpen").style.display="none";
        aud.pause();
        return;
    }
    //获取ajax数据
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            AIurls = JSON.parse(xhr.responseText);
            var ad = document.getElementById("ai_audio");
            paly_id = 0;
            var xurl = configs.conf_AI.API_URL_L+AIurls.data[paly_id+1]+configs.conf_AI.API_URL_R;
            aud = new Audio(xurl);
            var xurl = configs.conf_AI.API_URL_L+AIurls.data[paly_id+2]+configs.conf_AI.API_URL_R;
            auda = new Audio(xurl);
            aud.play();
            paly_id+=3;
            paly_clock=setInterval(function (){
                if(aud.ended){
                    var xurl = configs.conf_AI.API_URL_L+AIurls.data[paly_id]+configs.conf_AI.API_URL_R;
                    aud = new Audio(xurl);
                    auda.play();
                    paly_id+=1;
                    document.getElementById("AI_isOpen").style.display="none";
                }else {
                    document.getElementById("AI_isOpen").style.display="block";
                }
                if(auda.ended) {
                    var xurl = configs.conf_AI.API_URL_L+AIurls.data[paly_id]+configs.conf_AI.API_URL_R;
                    auda = new Audio(xurl);
                    paly_id+=1;
                    aud.play()
                }
            }, 200);

            document.getElementById("AI_isOpen").style.display="block"
        }
    }

    xhr.open("get", document.getElementById('next').href + "?SPLIT", true);
    xhr.send(null);
}
var aud;
var auda;
var load_clock;
function updateScrollProgress() {
    const main = document.getElementById('poin');
    let max = 0;
    let now = 0;
    //与章节进度联动
    if(main){
        max = parseInt(main.getAttribute('max')); //获取自定义参数-max(最大值)
        now = parseInt(main.getAttribute('now')); //获取自定义参数-now(当前值)
    }


    scrollProgresss = document.getElementById('scroll-progress');
    scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
    scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight;
    if(!main) {
        var progresss = ((scrollTop / (scrollHeight - window.innerHeight))) * 100;
    }else{
        var progresss = (window.innerWidth * (now / max)+(scrollTop / ((scrollHeight - window.innerHeight) * (window.innerWidth / max))))/window.innerWidth*100;
    }
    if(main){
        scrollProgresss.style.width = window.innerWidth * (now / max)+(scrollTop / ((scrollHeight - window.innerHeight) * (window.innerWidth / max))) + "px"
    }else{
        scrollProgresss.style.width = progresss + '%';
    }
    scrollProgresss.style.backgroundColor = "rgb(" + progresss * 2.5 + ",0," + (255 - progresss * 2.5) + ")";

}

function scrollHandler() {
    updateScrollProgress()
}

window.addEventListener('scroll', scrollHandler);