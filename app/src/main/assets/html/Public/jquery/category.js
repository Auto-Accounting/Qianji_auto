
const isInTimeInner = function (minTime, maxTime,timeHour,timeMinute) {

    let regT = /([01\b]\d|2[0-3]):([0-5]\d)/;
    const t1 = minTime.match(regT);
    const t2 = maxTime.match(regT);
    if(t1==null||t2==null||t1.length<3||t2.length<3){
        return false;
    }
    const h1 = parseInt(t1[1]), h2 =  parseInt(t2[1]), m1 =  parseInt(t1[2]), m2 =  parseInt(t2[2]);
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
/*console.log(isInTimeInner("12:00","23:00",12,40))
console.log(isInTimeInner("10:00","09:00",6,30))*/
