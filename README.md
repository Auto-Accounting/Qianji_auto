8. 监控端中点击开启服务跳转到辅助功能中开启服务
92
9. 开启服务后返回v免签点击检测监听权限
93
10. 如果显示监听权限正常，至此安装完毕，如果只收到通知栏推送的测试通知，则系统不兼容无法正常监听
94
11. 如果显示监听权限正常，还是无法正常运行，那么请确定微信是否关注 “微信支付” 和 “微信收款助手” 这两个公众号
95
​
96
**注意事项**
97
​
98
 - 本作品基于[SpeedPHP4](https://github.com/dreamncn/SpeedPHP)开发，采用MVC单一入口程序开发规范
99
​
100
 - 由服务器中间件实现伪静态，故本程序必须放在域名根目录才能正常运行。
101
​
102
​
103
### 测试
104
​
105
1、启用内置商城
106
​
107
  - 开启方式
108
    1. 登录后台
109
    2. 点击`系统设置`
110
    3. 将`内置商城`选项设为开启，并刷新页面即可
111
  - 使用方法
112
    1. 打开网站首页进行支付测试
113
    
114
2、使用SDK测试
115
    打开SDK路径阅读README，修改对应的文件，具体操作可以参考[开发文档](https://doc.dreamn.cn/doc/Vpay%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/#/%E4%BD%BF%E7%94%A8%E6%B5%8B%E8%AF%95)
116
​
117
## 版本内容更新
118
​
119
[Changelog](CHANGELOG.md)
120
​
121
## 鸣谢
122
​
123
- [V免签](https://github.com/szvone/Vmq)
124
- [SpeedPHP框架](https://github.com/SpeedPHP/manual)
125
​
126
​
127
## 版权声明
128
​
129
V免签 - Dreamn修改版遵循 [GPL-3.0 License](/LICENSE) 开源协议发布，并提供免费使用，请勿用于非法用途。
130
​
131
版权所有Copyright © 2020 by dreamn (https://dreamn.cn)
132
​
133
All rights reserved。
134
​
135
​


# Vpay PHP版

![](https://img.shields.io/github/v/release/dreamncn/VPay.svg)
![](https://img.shields.io/github/issues/dreamncn/VPay)
![](https://img.shields.io/badge/PoweredBy-Dreamn-f39f37)
![](https://img.shields.io/github/license/dreamncn/VPay)
![](https://img.shields.io/github/stars/dreamncn/VPay.svg?label=Stars&style=social)


## 项目简介&功能特性

Vpay 是基于SpeedPHP4 + mysql 实现的一套免签支付程序，主要包含以下特色：

```
    1. 收款即时到账，无需进入第三方账户，收款更安全
    2. 提供示例代码（demo）简单接入# 自动记账

# ![](https://img.shields.io/github/issues/dreamncn/Qianji_auto)
![](https://img.shields.io/badge/PoweredBy-Dreamn-f39f37)
![](https://img.shields.io/github/license/dreamncn/VPay)
![](https://img.shields.io/github/stars/dreamncn/Qianji_auto.svg?label=Stars&style=social)


## 项目简介&功能特性

> 一个基于xposed的自动记账小工具，可以通过第三方记账接口实现强大的记账功能。

框架适配：

『太极』『Xposed』『EdXposed』

账本App已支持：

『钱迹』

记账App已支持：

『支付宝』（人脸识别支付与免密支付可能无法自动记账）

支付支持：转账、红包、淘宝等对接了支付宝支付sdk的app

收款支持：转账、红包（需要点一下红包）、二维码收款

『微信』『京东』暂不支持，后期有适配计划，敬请期待。

> 太极用户须知：
>
> 每次更新后，都必须到太极模块中【更新适配引擎】并强行停止支付宝才能生效。

## 开源原因

由于我要全心复习考研，决定暂时停更这个插件（到2020年年底）。

## 贡献代码

- Fork该项目到您的Github账号中
- Clone您fork的项目到本地
- 本地调试修改好后提交到您的项目中
- 从您fork的项目向我的项目发起Pull Request

## 部署步骤

- 下载源代码到本地

- 使用AS打开
- Build

## 版本内容更新

1.5.2

修复：二维码收款不自动记账的问题

修复：删除所有语义之后又出现的问题

修复：部分机型闪退

修复：支付宝部分未识别账单加入识别队列

优化：修改全自动记账功能，关闭该功能后，如果添加了语义规则则自动进行自动记账，未添加则不进行全自动记账

新增：微信扫码支付识别

新增：增加余额宝、余利宝自动记账

1.5

修复：自动转账收款到余额宝出现重复记账

修复：提现到账出现重复记账

修复：Edxposed失效问题

修复：花呗支付资产映射失败的问题

新增：资产映射，支持自定义各种付款渠道对应钱迹里面的资产名称

新增：全自动记账（需要自己定义语义学习），类似MIUI的系统记账。

新增：语义学习功能，支持全自动记账

新增：内置部分语义与资产映射，便于学习

优化：更新UI

优化：语义学习支持学习商品详情信息（商品详情优先级高）



1.4.4b5
修复：转账出现花呗支付的问题
修复：部分框架显示的未激活问题
修复：银行卡转账出现重复记账问题
修复：日志记录出现支付宝广告
优化：日志支持复制，出现问题可以只复制那一段日志内容


1.4.4b4
修复：使用花呗支付记账出现错误


1.4.4b3
修复：偶尔无法正确获取金额的问题
修复：自动收款时出现支付宝名称错误（实际上是因为多了一个空格）
修复：收到转账时自动收款设置不生效
修复：修改账本无效问题
优化：日志显示颜色优化
新增：点击处理的通知消息支持直接选择分类


1.4.4b2
修复：免密扣款时无法正确获取金额信息
修复：日志显示不全
修复：免费用户出现账本错误
新增：添加加群方式



1.4.4b1
修复：无法正确获取金额的问题

## 鸣谢

- [钱迹](http://www.qianjiapp.com/)
- [XUI](https://github.com/xuexiangjys/XUI)


## 版权声明

自动记账遵循 [GPL-3.0 License](/LICENSE) 开源协议发布，并提供免费使用，请勿用于非法用途。

版权所有Copyright © 2020 by dreamn (https://dreamn.cn)

All rights reserved.
    3. 免费、开源，无后门风险
    4. 支持监听店员收款信息，可使用支付宝微信小号/模拟器挂机，方便IOS用户
    5. 免root，免xp框架，不修改支付宝/微信客户端，防封更安全
    6. 如果您不熟悉PHP环境的配置，您可以使用Java版本的服务端
```

原作者的PHP版VPay：[这里](https://github.com/szvone/vmqphp)

原作者的JAVA版的开源地址位于：[这里](https://github.com/szvone/Vmq)

原作者的监控端的开源地址位于：[这里](https://github.com/szvone/VmqApk)

在线演示：[这里](https://pay.dreamn.cn/)

接口开发文档与使用帮助:[这里](https://doc.dreamn.cn/doc/Vpay%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/)

> **注意**
> 1. 本项目为开源项目，本人不可能总会及时修复存在的漏洞
> 2. 请仔细阅读README和开发文档,非代码本身问题不予以处理
> 3. 有能力者可自行对漏洞进行修复并pull给我，我会及时审查
> 4. 感谢您的理解与使用，如果对您有帮助，请点个star，感谢您的支持！

#### 开发建议

1. 不要自己封装处理类、处理函数，sdk中自带工具类可以直接使用
2. 默认工具类为`/sdk/pay`


#### 原理

用户扫码付款 -> 收到款项后手机通知栏会有提醒 -> V免签监控端监听到提醒，推送至服务端->服务端根据金额判断是哪笔订单

## 环境依赖

1. CentOS 7.0
2. PHP 7.3 + openssl + PDO
3. Apache / Nginx

## 伪静态
> Apache
```<IfModule mod_rewrite.c>
   RewriteEngine On
   RewriteRule ^index\.php$ - [L]
   RewriteCond %{REQUEST_FILENAME} !-f
   RewriteCond %{REQUEST_FILENAME} !-d
   RewriteRule . index.php [L]
   </IfModule>
```

> Nignx

```
   location / {
          if (!-e $request_filename){
              rewrite (.*) /index.php;
          }
      }
      location ^~ /protected {
          deny all;
      }
      location ^~ /install {
          deny all;
      }
```

## 部署步骤

1. 下载源代码到您的服务器
2. 然后访问服务器公网IP开始安装
3. 如果您是第一次使用建议您选择完全安装
4. 安装好后登录后台进行配置
5. 打开网站后台监控端设置
6. 下载监控端[点击这里](https://github.com/szvone/vmqApk/releases)
7. 安装监控端后使用手动配置或扫码配置
8. 监控端中点击开启服务跳转到辅助功能中开启服务
9. 开启服务后返回v免签点击检测监听权限
10. 如果显示监听权限正常，至此安装完毕，如果只收到通知栏推送的测试通知，则系统不兼容无法正常监听
11. 如果显示监听权限正常，还是无法正常运行，那么请确定微信是否关注 “微信支付” 和 “微信收款助手” 这两个公众号

**注意事项**

 - 本作品基于[SpeedPHP4](https://github.com/dreamncn/SpeedPHP)开发，采用MVC单一入口程序开发规范

 - 由服务器中间件实现伪静态，故本程序必须放在域名根目录才能正常运行。


### 测试

1、启用内置商城

  - 开启方式
    1. 登录后台
    2. 点击`系统设置`
    3. 将`内置商城`选项设为开启，并刷新页面即可
  - 使用方法
    1. 打开网站首页进行支付测试
    
2、使用SDK测试
    打开SDK路径阅读README，修改对应的文件，具体操作可以参考[开发文档](https://doc.dreamn.cn/doc/Vpay%E5%BC%80%E5%8F%91%E6%96%87%E6%A1%A3/#/%E4%BD%BF%E7%94%A8%E6%B5%8B%E8%AF%95)

## 版本内容更新

[Changelog](CHANGELOG.md)

## 鸣谢

- [V免签](https://github.com/szvone/Vmq)
- [SpeedPHP框架](https://github.com/SpeedPHP/manual)


## 版权声明

V免签 - Dreamn修改版遵循 [GPL-3.0 License](/LICENSE) 开源协议发布，并提供免费使用，请勿用于非法用途。

版权所有Copyright © 2020 by dreamn (https://dreamn.cn)

All rights reserved。
