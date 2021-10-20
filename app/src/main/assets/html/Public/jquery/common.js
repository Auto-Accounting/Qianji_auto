function htmlspecialchars(str) {
    str = str.replace(/</g, '&lt;');
    str = str.replace(/>/gi, '&gt;');
    return str;
}

function StringAs(string) {
    return string.replace(/([\\"\n\r\t'])/g, "\\$1");
}

const isInTimeInner = function (minTime, maxTime, timeHour, timeMinute) {
    let regT = /([01\b]\d|2[0-3]):([0-5]\d)/;
    const t1 = minTime.match(regT);
    const t2 = maxTime.match(regT);
    if (t1 == null || t2 == null || t1.length < 3 || t2.length < 3) {
        return false;
    }
    const h1 = parseInt(t1[1], 10), h2 = parseInt(t2[1], 10), m1 = parseInt(t1[2], 10), m2 = parseInt(t2[2], 10);
    if (h1 > h2)
        return (timeHour === h1 && timeMinute >= m1) || timeHour > h1 || timeHour < h2 || timeHour === h2 && timeMinute <= m2;
    else if (h1 < h2)
        return (timeHour === h1 && timeMinute >= m1) || (timeHour > h1 && timeHour < h2) || timeHour === h2 && timeMinute <= m2;
    else if (h1 === h2) {
        if (m1 < m2)
            return timeHour === h1 && timeMinute >= m1 && timeMinute <= m2;
        else if (m1 > m2)
            return (timeHour === h1 && timeMinute >= m1 || timeMinute <= m2) || (timeHour !== h1);
        else
            return m1 === m2 && timeMinute === m1 && timeHour === h1;
    }
};

function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        const pair = vars[i].split("=");
        if (pair[0] === variable) {
            return pair[1];
        }
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
