/*

*/
let Base64 = {
    encode(str) {
        // first we use encodeURIComponent to get percent-encoded UTF-8,
        // then we convert the percent encodings into raw bytes which
        // can be fed into btoa.
        return encode(str,"base16");
    },
    decode(str) {
        // Going backwards: from bytestream, to percent-encoding, to original string.
        return decode(str,"base16");
    }
};
let Html = {
     stringToEntity(str,radix){
        let arr=str.split('')
        radix=radix||0
        let tmp=arr.map(item=>
            `&#${(radix?'x'+item.charCodeAt(0).toString(16):item.charCodeAt(0))};`).join('')
        console.log(`'${str}' 转实体为 '${tmp}'`)
        return tmp
    },
     entityToString(entity){
        let entities=entity.split(';')
        entities.pop()
        let tmp=entities.map(item=>String.fromCharCode(
            item[2]==='x'?parseInt(item.slice(3),16):parseInt(item.slice(2)))).join('')
        console.log(`'${entity}' 转字符串为 '${tmp}'`)
        return tmp
    }
}
//let encoded = Base64.encode("哈ha"); // "5ZOIaGE="
//let decoded = Base64.decode(encoded); // "哈ha"
class DataUtils{
    jsData={};
    put(key,value){
        this.jsData[key]=encodeURIComponent(value);
     }

     get(key){
        console.log(this.jsData[key])
         return decodeURIComponent(this.jsData[key])
     }
     parse(str){

         var vars = str.split("&");
       //  console.log(vars)
         for (var i=1;i<vars.length;i++) {
             var index=vars[i].indexOf("=");
            // console.log(vars[i].substr(0,index),vars[i].substr(index+1))
             this.jsData[vars[i].substr(0,index)]=Base64.decode(decodeURIComponent(vars[i].substr(index+1)));
             //if(pair[0] === variable){return pair[1];}
         }

    }
    toString(){
        let str1 = "data://string?";
        for(const i in this.jsData) {
            str1+="&"+i+"="+(Base64.encode(decodeURIComponent(this.jsData[i])));
       }
        return str1;
    }
}
