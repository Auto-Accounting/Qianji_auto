layui.define('form', function (exports) {
/// #!/usr/bin/env node
/// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
/// @Copyright ~2016 ☜Samlv9☞ and other contributors
/// @MIT-LICENSE | 1.0.0 | http://apidev.guless.com/
/// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
///                                              }|
///                                              }|
///                                              }|     　 へ　　　 ／|    
///      _______     _______         ______      }|      /　│　　 ／ ／
///     /  ___  |   |_   __ \      .' ____ '.    }|     │　Z ＿,＜　／　　 /`ヽ
///    |  (__ \_|     | |__) |     | (____) |    }|     │　　　　　ヽ　　 /　　〉
///     '.___`-.      |  __ /      '_.____. |    }|      Y　　　　　`　 /　　/
///    |`\____) |    _| |  \ \_    | \____| |    }|    ｲ●　､　●　　⊂⊃〈　　/
///    |_______.'   |____| |___|    \______,'    }|    ()　 v　　　　|　＼〈
///    |=========================================\|    　>ｰ ､_　 ィ　 │ ／／
///    |> LESS IS MORE                           ||     / へ　　 /　ﾉ＜|＼＼
///    `=========================================/|    ヽ_ﾉ　　(_／　 │／／
///                                              }|     7　　　　　　  |／
///                                              }|     ＞―r￣￣`ｰ―＿`
///                                              }|
///                                              }|
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in all
/// copies or substantial portions of the Software.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
/// THE SOFTWARE.
/// ==============================================================================
/// ==============================================================================
/// 定义编码表和解码表。
    var BASE16_ENCODE_TABLE = [ 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100,
        101, 102];
    var BASE16_DECODE_TABLE = [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7,
        8, 9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1];
    var BASE32_ENCODE_TABLE = [ 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78,
        79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 50, 51, 52, 53, 54, 55];
    var BASE32_DECODE_TABLE = [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29,
        30, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
        11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1];
    var BASE32_HEX_ENCODE_TABLE = [48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67,
        68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86];
    var BASE32_HEX_DECODE_TABLE = [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5,
        6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
        20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1];
    var BASE64_ENCODE_TABLE = [65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78,
        79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103,
        104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119,
        120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47];
    var BASE64_DECODE_TABLE = [ -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57,
        58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
        11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
        -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44,
        45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1];
    var BASE64_URL_ENCODE_TABLE = [65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77,
        78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102,
        103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118,
        119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95];
    var BASE64_URL_DECODE_TABLE = [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 54, 55, 56,
        57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
        10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1,
        63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
        44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1];

/// 定义 BASE32/BASE64 的补位字符。
    var PADCHAR = 61;
    var TAILPAD = /=+$/;

/// Base16 数据编码。
    function base16( input, table ) {
        var output = new Array(input.length << 1 >>> 0);

        for ( var start = 0, offset = 0; start < input.length; ++start ) {
            output[offset++] = table[input[start] >>> 4 & 0x0F];
            output[offset++] = table[input[start] & 0x0F];
        }

        return output;
    }

/// Base16 数据解码。
    function debase16( bytes, table ) {
        if ( bytes.length & 1 ) {
            throw new Error("Wrong size of base16 final chunk.");
        }

        var output = new Array(bytes.length >>> 1);

        for ( var start = 0, offset = 0; start + 2 <= bytes.length; start += 2 ) {
            var a = table[bytes.charCodeAt(start) & 0x7F];
            var b = table[bytes.charCodeAt(start + 1) & 0x7F];

            if ( a < 0 || b < 0 ) {
                throw new Error("Out of base16 character range. [offset=" + start + "]");
            }

            output[offset++] = a << 4 | b;
        }

        return output;
    }

/// Base32 数据编码。
    function base32( input, table ) {
        var output = new Array((Math.ceil(input.length / 5) << 3) >>> 0);

        for ( var start = 0, offset = 0; start + 5 <= input.length; start += 5 ) {
            var a = input[start    ];
            var b = input[start + 1];
            var c = input[start + 2];
            var d = input[start + 3];
            var e = input[start + 4];

            output[offset++] = table[(a >>> 3) & 0x1F];
            output[offset++] = table[((a & 0x07) << 2) | ((b >>> 6) & 0x03)];
            output[offset++] = table[(b >>> 1) & 0x1F];
            output[offset++] = table[((b & 0x01) << 4) | ((c >>> 4) & 0x0F)];

            output[offset++] = table[((c & 0x0F) << 1) | ((d >>> 7) & 0x01)];
            output[offset++] = table[(d >>> 2) & 0x1F];
            output[offset++] = table[((d & 0x03) << 3) | ((e >>> 5) & 0x07)];
            output[offset++] = table[e & 0x1F];
        }

        if ( input.length - start != 0 ) {
            switch( input.length - start ) {
                case (1):
                    output[offset++] = table[(input[start    ] >>> 3) & 0x1F];
                    output[offset++] = table[(input[start    ] & 0x07) << 2];
                    break;

                case (2):
                    output[offset++] = table[(input[start    ] >>> 3) & 0x1F];
                    output[offset++] = table[((input[start    ] & 0x07) << 2) | ((input[start + 1] >>> 6) & 0x03)];
                    output[offset++] = table[(input[start + 1] >>> 1) & 0x1F];
                    output[offset++] = table[(input[start + 1] & 0x01) << 4];
                    break;

                case (3):
                    output[offset++] = table[(input[start    ] >>> 3) & 0x1F];
                    output[offset++] = table[((input[start    ] & 0x07) << 2) | ((input[start + 1] >>> 6) & 0x03)];
                    output[offset++] = table[(input[start + 1] >>> 1) & 0x1F];
                    output[offset++] = table[((input[start + 1] & 0x01) << 4) | ((input[start + 2] >>> 4) & 0x0F)];
                    output[offset++] = table[(input[start + 2] & 0x0F) << 1];
                    break;

                case (4):
                    output[offset++] = table[(input[start    ] >>> 3) & 0x1F];
                    output[offset++] = table[((input[start    ] & 0x07) << 2) | ((input[start + 1] >>> 6) & 0x03)];
                    output[offset++] = table[(input[start + 1] >>> 1) & 0x1F];
                    output[offset++] = table[((input[start + 1] & 0x01) << 4) | ((input[start + 2] >>> 4) & 0x0F)];
                    output[offset++] = table[((input[start + 2] & 0x0F) << 1) | ((input[start + 3] >>> 7) & 0x01)];
                    output[offset++] = table[(input[start + 3] >>> 2) & 0x1F];
                    output[offset++] = table[(input[start + 3] & 0x03) << 3];
                    break;

                default:
                    throw new Error("Wrong size of base32 final chunk.");
            }

            for ( var i = offset; i < output.length; ++i ) {
                output[i] = PADCHAR;
            }
        }

        return output;
    }

/// Base32 数据解码。
    function debase32( bytes, table ) {
        var output = new Array((bytes.length >>> 3) * 5);

        for ( var start = 0, offset = 0; start + 8 <= bytes.length; start += 8) {
            var a = table[bytes.charCodeAt(start    ) & 0x7F];
            var b = table[bytes.charCodeAt(start + 1) & 0x7F];
            var c = table[bytes.charCodeAt(start + 2) & 0x7F];
            var d = table[bytes.charCodeAt(start + 3) & 0x7F];
            var e = table[bytes.charCodeAt(start + 4) & 0x7F];
            var f = table[bytes.charCodeAt(start + 5) & 0x7F];
            var g = table[bytes.charCodeAt(start + 6) & 0x7F];
            var h = table[bytes.charCodeAt(start + 7) & 0x7F];

            if ( a < 0 || b < 0 || c < 0 || d < 0 || e < 0 || f < 0 || g < 0 || h < 0 ) {
                throw new Error("Out of base32 character range. [offset=" + start + "]");
            }

            output[offset++] = (a << 3) | (b >>> 2);
            output[offset++] = ((b & 0x03) << 6) | (c << 1) | (d >>> 4);
            output[offset++] = ((d & 0x0F) << 4) | (e >>> 1);
            output[offset++] = ((e & 0x01) << 7) | (f << 2) | (g >>> 3);
            output[offset++] = ((g & 0x07) << 5) | h;
        }

        if ( bytes.length - start != 0 ) {
            switch( bytes.length - start ) {
                case (2):
                    a = table[bytes.charCodeAt(start    ) & 0x7F];
                    b = table[bytes.charCodeAt(start + 1) & 0x7F];

                    if ( a < 0 || b < 0 ) {
                        throw new Error("Out of base32 character range. [offset=" + (bytes.length - start) + "]");
                    }

                    output[offset++] = (a << 3) | (b >>> 2);
                    break;

                case (4):
                    a = table[bytes.charCodeAt(start    ) & 0x7F];
                    b = table[bytes.charCodeAt(start + 1) & 0x7F];
                    c = table[bytes.charCodeAt(start + 2) & 0x7F];
                    d = table[bytes.charCodeAt(start + 3) & 0x7F];

                    if ( a < 0 || b < 0 || c < 0 || d < 0 ) {
                        throw new Error("Out of base32 character range. [offset=" + (bytes.length - start) + "]");
                    }

                    output[offset++] = (a << 3) | (b >>> 2);;
                    output[offset++] = ((b & 0x03) << 6) | (c << 1) | (d >>> 4);
                    break;

                case (5):
                    a = table[bytes.charCodeAt(start    ) & 0x7F];
                    b = table[bytes.charCodeAt(start + 1) & 0x7F];
                    c = table[bytes.charCodeAt(start + 2) & 0x7F];
                    d = table[bytes.charCodeAt(start + 3) & 0x7F];
                    e = table[bytes.charCodeAt(start + 4) & 0x7F];

                    if ( a < 0 || b < 0 || c < 0 || d < 0 || e < 0 ) {
                        throw new Error("Out of base32 character range. [offset=" + (bytes.length - start) + "]");
                    }

                    output[offset++] = (a << 3) | (b >>> 2);;
                    output[offset++] = ((b & 0x03) << 6) | (c << 1) | (d >>> 4);
                    output[offset++] = ((d & 0x0F) << 4) | (e >>> 1);
                    break;

                case (7):
                    a = table[bytes.charCodeAt(start    ) & 0x7F];
                    b = table[bytes.charCodeAt(start + 1) & 0x7F];
                    c = table[bytes.charCodeAt(start + 2) & 0x7F];
                    d = table[bytes.charCodeAt(start + 3) & 0x7F];
                    e = table[bytes.charCodeAt(start + 4) & 0x7F];
                    f = table[bytes.charCodeAt(start + 5) & 0x7F];
                    g = table[bytes.charCodeAt(start + 6) & 0x7F];

                    if ( a < 0 || b < 0 || c < 0 || d < 0 || e < 0 || f < 0 || g < 0 ) {
                        throw new Error("Out of base32 character range. [offset=" + (bytes.length - start) + "]");
                    }

                    output[offset++] = (a << 3) | (b >>> 2);;
                    output[offset++] = ((b & 0x03) << 6) | (c << 1) | (d >>> 4);
                    output[offset++] = ((d & 0x0F) << 4) | (e >>> 1);
                    output[offset++] = ((e & 0x01) << 7) | (f << 2) | (g >>> 3);
                    break;

                default:
                    throw new Error("Wrong size of base32 final chunk.");
            }
        }

        return output;
    }

/// Base64 数据编码。
    function base64( input, table ) {
        var output = new Array((Math.ceil(input.length / 3) << 2) >>> 0);

        for ( var start = 0, offset = 0; start + 3 <= input.length; start += 3 ) {
            var a = input[start    ];
            var b = input[start + 1];
            var c = input[start + 2];

            output[offset++] = table[(a >>> 2) & 0x3F];
            output[offset++] = table[((a & 0x03) << 4) | ((b >>> 4) & 0x0F)];
            output[offset++] = table[((b & 0x0F) << 2) | ((c >>> 6) & 0x03)];
            output[offset++] = table[c & 0x3F];
        }

        if ( input.length - start != 0 ) {
            switch( input.length - start ) {
                case (1):
                    output[offset++] = table[(input[start    ] >>> 2) & 0x3F];
                    output[offset++] = table[(input[start    ] & 0x03) << 4];
                    break;

                case (2):
                    output[offset++] = table[((input[start    ] >>> 2) & 0x3F)];
                    output[offset++] = table[((input[start    ] & 0x03) << 4) | ((input[start + 1] >>> 4) & 0x0F)];
                    output[offset++] = table[((input[start + 1] & 0x0F) << 2)];
                    break;

                default:
                    throw new Error("Wrong size of base64 final chunk.");
            }

            for ( var i = offset; i < output.length; ++i ) {
                output[i] = PADCHAR;
            }
        }

        return output;
    }

/// Base64 数据解码。
    function debase64( bytes, table ) {
        var output = new Array((bytes.length >>> 2) * 3);

        for ( var start = 0, offset = 0; start + 4 <= bytes.length; start += 4 ) {
            var a = table[bytes.charCodeAt(start    ) & 0x7F];
            var b = table[bytes.charCodeAt(start + 1) & 0x7F];
            var c = table[bytes.charCodeAt(start + 2) & 0x7F];
            var d = table[bytes.charCodeAt(start + 3) & 0x7F];

            if ( a < 0 || b < 0 || c < 0 || d < 0 ) {
                throw new Error("Out of base64 character range. [offset=" + start + "]");
            }

            output[offset++] = (a << 2) | (b >>> 4);
            output[offset++] = ((b & 0x0F) << 4) | (c >>> 2);
            output[offset++] = ((c & 0x03) << 6) | d;
        }

        if ( bytes.length - start != 0 ) {
            switch( bytes.length - start ) {
                case (2):
                    a = table[bytes.charCodeAt(start    ) & 0x7F];
                    b = table[bytes.charCodeAt(start + 1) & 0x7F];

                    if ( a < 0 || b < 1 ) {
                        throw new Error("Out of base64 character range. [offset=" + (bytes.length - start) + "]");
                    }

                    output[offset++] = (a << 2) | (b >>> 4);
                    break;

                case (3):
                    a = table[bytes.charCodeAt(start    ) & 0x7F];
                    b = table[bytes.charCodeAt(start + 1) & 0x7F];
                    c = table[bytes.charCodeAt(start + 2) & 0x7F];

                    if ( a < 0 || b < 0 || c < 0 ) {
                        throw new Error("Out of base64 character range. [offset=" + (bytes.length - start) + "]");
                    }

                    output[offset++] = (a << 2) | (b >>> 4);
                    output[offset++] = ((b & 0x0F) << 4) | (c >>> 2);
                    break;

                default:
                    throw new Error("Wrong size of base64 final chunk.");
            }
        }

        return output;
    }

/// UTF-8 数据编码。
    function utf8( input ) {
        /// 提前计算用于保存编码结果所需的数组长度。
        var size = 0;
        var char = 0;

        for ( var start = 0; start < input.length; ++start ) {
            char  = input.charCodeAt(start);
            size += (char >= 0xD800)  && (char <= 0xDBFF) ? (++start >= input.length ? 0 : 4) : (char <= 0x7F) ? 1 : (char <= 0x7FF ? 2 : 3);
        }

        /// 开始编码 UTF-8 字符串。
        var output = new Array(size);
        for ( var start = 0, offset = 0; start < input.length; ++start ) {
            char  = input.charCodeAt(start);

            if ( (char >= 0xDC00) && (char <= 0xDFFF) ) {
                throw new Error("Encounter an unpaired surrogate. [char=" + char + "]");
            }

            if ( (char >= 0xD800) && (char <= 0xDBFF) ) {
                if ( ++start >= input.length ) {
                    throw new Error("Encounter an unpaired surrogate. [char=" + char + "]");
                }

                var tail = input.charCodeAt(start);

                if ( (tail < 0xDC00) || (tail > 0xDFFF) ) {
                    throw new Error("Encounter an unpaired surrogate. [char=" + char + ", tail=" + tail + "]");
                }

                char = ((char & 0x3FF) << 10 | (tail & 0x3FF)) + 0x10000;
            }

            if ( char <= 0x7F ) {
                output[offset++] = (char);
            }

            else if ( char <= 0x7FF ) {
                output[offset++] = ((char >>>  6) + 0xC0);
                output[offset++] = ((char & 0x3F) + 0x80);
            }

            else if ( char <= 0xFFFF ) {
                output[offset++] = ((char  >>> 12) + 0xE0);
                output[offset++] = (((char >>>  6) & 0x3F) + 0x80);
                output[offset++] = ((char  & 0x3F) + 0x80);
            }

            else {
                output[offset++] = ((char  >>> 18) + 0xF0);
                output[offset++] = (((char >>> 12) & 0x3F) + 0x80);
                output[offset++] = (((char >>>  6) & 0x3F) + 0x80);
                output[offset++] = ((char  & 0x3F) + 0x80);
            }
        }

        return output;
    }

/// UTF-8 数据解码。
    function deutf8( bytes ) {
        /// 提前计算用于保存解码结果所需的数组长度。
        var size = 0;
        var char = 0;

        for ( var start = 0; start < bytes.length; ++start ) {
            char  = bytes[start];
            size += (char <= 0x7F ? 1 :
                (char & 0xF0) == 0xF0 ? ((start += 3) >= bytes.length ? 0 : 2) :
                    (char & 0xE0) == 0xE0 ? ((start += 2) >= bytes.length ? 0 : 1) :
                        (char & 0xC0) == 0xC0 ? ((start += 1) >= bytes.length ? 0 : 1) : 0);
        }

        var output = new Array(size);
        var offset = 0;

        for ( var start = 0, total = 0; start < bytes.length; ++start ) {
            char  = bytes[start];

            if ( char >= 0x80 ) {
                if ( (char < 0xC2) || (char > 0xF4) ) {
                    throw new Error("Invaild utf-8 character. [offset=" + start + ", char=" + char + "]");
                }

                if ( (char & 0xF0) == 0xF0 ) {
                    total = start + 3;
                    char  = char & 0x07;
                }

                else if ( (char & 0xE0) == 0xE0 ) {
                    total = start + 2;
                    char  = char & 0x0F;
                }

                else if ( (char & 0xC0) == 0xC0 ) {
                    total = start + 1;
                    char  = char & 0x1F;
                }

                else {
                    throw new Error("Invaild utf-8 character. [offset=" + start + ", char=" + char + "]");
                }

                if ( total >= bytes.length ) {
                    throw new Error("Encounter an unpaired surrogate. [char=" + char + "]");
                }

                while ( (start + 1) <= total ) {
                    var tail = bytes[++start];

                    if ( (tail < 0x80) || (tail > 0xBF) ) {
                        throw new Error("Invaild utf-8 trialing character. [offset=" + start + ", char=" + char + "]");
                    }

                    char = ((char << 6) | (tail & 0x3F));
                }
            }

            if ( (char >= 0xD800) && (char <= 0xDFFF) ) {
                throw new Error("Encounter an unpaired surrogate. [char=" + char + "]");
            }

            if ( char >= 0x10000 ) {
                output[offset++] = (char >> 10) + 0xD7C0;
                output[offset++] = (char & 0x3FF) + 0xDC00;
            }

            else {
                output[offset++] = char;
            }
        }

        return output;
    }

/// 数组转字符串。
    function arr2str( bytes ) {
        return String.fromCharCode.apply(null, bytes);
    }

/// 删除字符串末尾的补位字符("=")。
    function trimRight( bytes ) {
        return bytes.replace(TAILPAD, "");
    }

    /**
     * 将给定的字符串编码成指定格式的数据。
     *
     * @param {String} string - 指定需要编码的字符串。
     * @param {String} codec - 指定编码类型，可以是以下常量中的一个（不区分大小写）：
     *
     *     - "base16" | "hex"
     *     - "base32"
     *     - "base32-hex" | "base32hex"
     *     - "base64"
     *     - "base64-urlsafe" | "base64urlsafe" | "base64-url" | "base64url"
     *
     * @returns {String} - 返回编码后的数据的字符串表示形式。
     */
    function encode( string, codec ) {
        switch( codec ) {
            case "hex"       :
            case "base16"    : return arr2str(base16(utf8(string), BASE16_ENCODE_TABLE));
            case "base32"    : return arr2str(base32(utf8(string), BASE32_ENCODE_TABLE));
            case "base32hex" :
            case "base32-hex": return arr2str(base32(utf8(string), BASE32_HEX_ENCODE_TABLE));
            case "base64"    : return arr2str(base64(utf8(string), BASE64_ENCODE_TABLE));
            case "base64-urlsafe":
            case "base64urlsafe" :
            case "base64-url":
            case "base64url" : return arr2str(base64(utf8(string), BASE64_URL_ENCODE_TABLE));

            default:
                throw new Error("Unknow codec algorithms.");
        }
    }

    /**
     * 将给定的字符串解码成指定格式的数据。
     *
     * @param {String} string - 指定需要解码的字符串。
     * @param {String} codec - 指定解码类型，可以是以下常量中的一个（不区分大小写）：
     *
     *     - "base16" | "hex"
     *     - "base32"
     *     - "base32-hex" | "base32hex"
     *     - "base64"
     *     - "base64-urlsafe" | "base64urlsafe" | "base64-url" | "base64url"
     *
     * @returns {String} - 返回解码后的数据的字符串表示形式。
     */
    function decode( string, codec ) {
        switch( codec.toLowerCase() ) {
            case "hex"       :
            case "base16"    : return arr2str(deutf8(debase16(string, BASE16_DECODE_TABLE)));
            case "base32"    : return arr2str(deutf8(debase32(trimRight(string), BASE32_DECODE_TABLE)));
            case "base32hex" :
            case "base32-hex": return arr2str(deutf8(debase32(trimRight(string), BASE32_HEX_DECODE_TABLE)));
            case "base64"    : return arr2str(deutf8(debase64(trimRight(string), BASE64_DECODE_TABLE)));
            case "base64-urlsafe":
            case "base64urlsafe" :
            case "base64-url":
            case "base64url" : return arr2str(deutf8(debase64(trimRight(string), BASE64_URL_DECODE_TABLE)));

            default:
                throw new Error("Unknow codec algorithms.");
        }
    }

/// ==============================================================================
/// ==============================================================================

    const obj={
        encode:encode,decode:decode
    }
    exports('crypto', obj);
});