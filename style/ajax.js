let dom = document.getElementById('NextPage');
let dom1 = document.getElementById('href');
var jumped = false;//是否允许自动跳转,false为允许.
document.addEventListener('scroll', function () {
    if (HasFastNextPage) {
        if (dom.offsetTop - dom.offsetHeight < document.body.scrollTop + document.body.clientHeight) {
            document.body.scrollTop = document.body.scrollTop - 10;
        }
        if (dom1.offsetTop - dom1.offsetHeight < document.body.scrollTop + document.body.clientHeight) {
            if (!jumped) {
                if (getCookie('AJMODE') == 'TRUE') {
                    jumped = true;
                    var xhr = new XMLHttpRequest();
                    xhr.onreadystatechange = function () {
                        if (xhr.readyState == 4) {
                            document.getElementById("maintext").innerHTML = document.getElementById("maintext").innerHTML + xhr.responseText;
                            let a = document.getElementById('next').href;
                            //parse
                            let b = a.substr(a.lastIndexOf("/") + 1);
                            let c = b.substr(0, b.lastIndexOf("."));
                            let d = parseInt(c) + 1;
                            let e = d - 2;
                            let f = d - 1;
                            document.getElementById('next').href = a.substr(0, a.lastIndexOf("/") + 1) + d + ".html";
                            document.getElementById('last').href = a.substr(0, a.lastIndexOf("/") + 1) + e + ".html";
                            document.getElementById('poin').setAttribute('now', d - 1);
                            InitPoin(document.getElementById('poin'));
                            history.replaceState(null, null, a.substr(0, a.lastIndexOf("/") + 1) + f + ".html");
                            jumped = false;
                        }
                    }
                    xhr.open("get", document.getElementById('next').href + "?NOUI", true);
                    xhr.send(null);
                } else {
                    window.location.href = document.getElementById('next').href;
                }
            }
        }
    }
    return true;
});
window.onresize = function () {
    InitPoin(document.getElementById('poin'));
    return true;
}