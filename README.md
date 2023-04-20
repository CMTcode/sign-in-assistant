<div align="center">
<h1 align="center">签到助手</h1>
<img src="https://img.shields.io/github/issues/CMTcode/sign-in-assistant?color=green?style=flat-square&logo=github">
<img src="https://img.shields.io/github/stars/CMTcode/sign-in-assistant?color=yellow?style=flat-square&logo=github">
<img src="https://img.shields.io/github/forks/CMTcode/sign-in-assistant?color=orange?style=flat-square&logo=github">
<img src="https://img.shields.io/github/license/CMTcode/sign-in-assistant?color=ff69b4">
<img src="https://img.shields.io/github/languages/code-size/CMTcode/sign-in-assistant?color=blueviolet">
</div>
该项目使用springboot作为框架整合各类应用签到，如百度贴吧签到、哔哩哔哩、网易云音乐、阿里云网盘等。

默认每日 04:01 开始每日签到任务,要修改请更改config/appconfig.yml内的scheduled.cron值

### 功能介绍

#### 百度贴吧

- 贴吧签到

#### 哔哩哔哩

- 观看分享视频
- 漫画签到
- 漫画权益领取
- 漫画每日阅读
- 每日投币任务
- 直播签到
- 直播送出即将过期的礼物

#### 网易云音乐

- 每日签到经验任务
- 云贝签到
- 每日300首歌等级任务

#### 阿里云网盘

- 每日签到

#### 消息推送

 目前只推送cookie失效的账号

- 邮箱
- Server酱 [Server酱官方](https://sct.ftqq.com/)

### 使用说明

第一次使用请修改config文件夹下的yml配置文件。

- #### 多账号使用说明

  要使用多账号请按如下配置yml（以baidu.yml为例）

  ```yml
  accounts:
    - name: 账号1
      bduss: aaaaaa
    - name: 账号2
      bduss: ccccc
  ```


- #### 各cookie获取说明

  - 阿里云网盘refreshToken获取说明

  ![](config/阿里云网盘refreshToken.png)

### 访问量

![](http://profile-counter.glitch.me/CMTcode/count.svg)

### 历史 Star 数

![](https://starchart.cc/CMTcode/sign-in-assistant.svg)

