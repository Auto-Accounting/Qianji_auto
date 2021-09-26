function htmlspecialchars(str){
    str = str.replace(/</g, '&lt;');
    str = str.replace(/>/gi,'&gt;');
    return str;
}
function StringAs(string) { return  string.replace(/([\\"\n\r\t'])/g, "\\$1");}
const isInTimeInner = function (minTime, maxTime,timeHour,timeMinute){
    function getNextDate(date, day) {
        let dd;
        if(date==null){
            dd = new Date();
        } else{
            dd = new Date(date);
        }
        dd.setDate(dd.getDate() + day);
        const y = dd.getFullYear();
        const m = dd.getMonth() + 1 < 10 ? "0" + (dd.getMonth() + 1) : dd.getMonth() + 1;
        const d = dd.getDate() < 10 ? "0" + dd.getDate() : dd.getDate();
        return y + "-" + m + "-" + d;
    }


    const currentTime = new Date(getNextDate(null, 0) + " " + timeHour + ":" + timeMinute);


    const endTime1 = new Date(getNextDate(null, 1) + " " + maxTime);
    const beginTime1 = new Date(getNextDate(null, 0) + " " + minTime);

    const endTime2 = new Date(getNextDate(null, 0) + " " + maxTime);
    const beginTime2 = new Date(getNextDate(null, -1) + " " + minTime);



    if(endTime2<beginTime1){
        return ( beginTime1<=currentTime&&endTime1>=currentTime)||( beginTime2<=currentTime&&endTime2>=currentTime);

    }
    else{
        return ( beginTime1<=currentTime&&endTime2>=currentTime);

    }
};
function getQueryVariable(variable)
{
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        const pair = vars[i].split("=");
        if(pair[0] === variable){return pair[1];}
    }
    return undefined;
}
String.format = function () {
    let s = arguments[0];
    for (let i = 0; i < arguments.length - 1; i++) {
        const reg = new RegExp("%s", "m");
        s = s.replace(reg, arguments[i + 1]);
    }
    return s;
};
function Toast(msg, duration) {
    duration = isNaN(duration) ? 3000 : duration;
    var m = document.createElement('div');
    m.innerHTML = msg;
    m.style.cssText = "max-width:60%;min-width: 150px;padding:0 14px;height: 40px;color: rgb(255, 255, 255);line-height: 40px;text-align: center;border-radius: 4px;position: fixed;top: 50%;left: 50%;transform: translate(-50%, -50%);z-index: 999999;background: rgba(0, 0, 0,.7);font-size: 16px;";
    document.body.appendChild(m);
    setTimeout(function () {
        var d = 0.5;
        m.style.webkitTransition = '-webkit-transform ' + d + 's ease-in, opacity ' + d + 's ease-in';
        m.style.opacity = '0';
        setTimeout(function () {
            document.body.removeChild(m)
        }, d * 1000);
    }, duration);
}
