# 自动记账
 🚀一款基于Xposed和无障碍的记账插件


![](https://img.shields.io/static/v1?label=framework&message=Xposed/无障碍&color=success&style=for-the-badge)
![](https://img.shields.io/static/v1?label=licenes&message=GPL3.0&color=important&style=for-the-badge)
![](https://img.shields.io/github/stars/dreamncn/Qianji_auto.svg?style=for-the-badge)
![](https://img.shields.io/static/v1?label=download&message=44K&color=9cf&style=for-the-badge)

> 缘起
> 
> 资本在行动，他们用消费主义四处撒网，少年们纷纷卷入陷阱之中，原本富足的少年开始慢慢变穷。
> 
> 少年们很费解，为什么赚的钱越来越多，生活却越来越拮据。
> 
> 少年们找到了阅历丰富的老人，老人告诉他们：你们要学会理财。**理财？**
> 
> 哦！那可真是个大难题。
> 
> 可少年们还是行动了，年轻人总是那么的勇敢。
> 
> 想要理财，首先得弄清每一笔财富的去向。
> 
> 于是，少年们找到了记账软件：[钱迹](https://www.coolapk.com/apk/com.mutangtech.qianji)
> 
> 它是一款好软件。
> 
> 但，记录那些琐碎而零散的消费，会耗费大量的精力。这对于疲惫不堪的少年们来说，简直就是灾难。
> 
> 如果这些工作由机器来完成，那该多好啊！
> 
> 仿佛听到了群山中少年们的呼唤，光芒自迷雾中降生，**自动记账**——他来了！
> 
> 他，是你账单的守护者，记录你的每一笔收支。
> 
> 他，是支付与记账的桥梁，随付随到，从不缺席。
> 
> 无需苦思冥想，无需左右切换，只需在付款后轻轻一点，即可完成记账。
> 
> **自动记账：** 一款真正人性化的插件。

----
> 注意 
>
> 1、自动记账是一款辅助记账软件，基于[钱迹](https://www.coolapk.com/apk/com.mutangtech.qianji)自动记账接口开发。因此本插件需搭配[钱迹](https://www.coolapk.com/apk/com.mutangtech.qianji)App使用。<br/>
>
> 2、自动记账3.0版本中已经加入了对其他记账软件的支持，详情请参考[使用其他记账软件]()

----

自动记账提供 **Xposed** 和 **无障碍** 两种模式。由于实现的原理差异，在功能上具有一定的差异，在条件允许的情况下，我们推荐使用**Xposed模式**，使用**虚拟框架**的用户在体验上略差于**Xposed**。


具体的对比如下:

| 优点及缺点                                              | 无障碍                                    | Xposed                                |
| ------------------------------------------------------- | ----------------------------------------- | ------------------------------------- |
| **直接报销/退款，无需进入钱迹**                         | <font color="#ff7733">✘</font>            | <font color="#ff7733">✔</font>        |
| **直接从钱迹拉取资产、分类数据**                        | <font color="#ff7733">✘</font>            | <font color="#44cc66">✔</font>        |
| **微信、支付宝等账单识别**                              | <font color="#44cc66">✔</font>            | <font color="#44cc66">✔</font>        |
| **通知以及短信监听识别**                                | <font color="#44cc66">✔</font>            | <font color="#44cc66">✔</font>        |
| **后台扣费账单识别**                                    | <font color="#ff7733">✘</font>            | <font color="#44cc66">✔</font>        |
| **账单信息读取**                                        | **<font color="#ff7733">不够精准</font>** | **<font color="#44cc66">精准</font>** |
| **<font color="#ff7733">需要解锁BootLoader刷机</font>** | <font color="#44cc66">✘</font>            | <font color="#ff7733">✔</font>        |
| **<font color="#ff7733">需要常驻后台</font>**           | <font color="#ff7733">✔</font>            | <font color="#44cc66">✘</font>        |


## 文字教程

[点击开始教程](https://auto.ankio.net)


## 视频教程

[点击开始视频教程](https://player.bilibili.com/player.html?aid=289448844&bvid=BV1Hf4y147Yi&cid=303785417&page=1)

## 编译步骤

- 下载源代码到本地

```bash
git clone https://github.com/dreamncn/Qianji_auto
```

- 使用[Android Studio](https://developer.android.com/studio)打开，等待自动配置完成，如出现失败请配置科学上网

- 点击菜单中 `Run` - `Build`

## 下载

- [酷安](https://www.coolapk.com/apk/cn.dreamn.qianji_auto)
- [LSPosed仓库](https://github.com/Xposed-Modules-Repo/cn.dreamn.qianji_auto/releases/tag/v3.2.4)







 
