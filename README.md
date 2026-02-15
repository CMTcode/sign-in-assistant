<div align="center">
<h1 align="center">签到助手</h1>
<img src="https://img.shields.io/github/issues/CMTcode/sign-in-assistant?color=green?style=flat-square&logo=github">
<img src="https://img.shields.io/github/stars/CMTcode/sign-in-assistant?color=yellow?style=flat-square&logo=github">
<img src="https://img.shields.io/github/forks/CMTcode/sign-in-assistant?color=orange?style=flat-square&logo=github">
<img src="https://img.shields.io/github/license/CMTcode/sign-in-assistant?color=ff69b4">
<img src="https://img.shields.io/github/languages/code-size/CMTcode/sign-in-assistant?color=blueviolet">
</div>

基于 Python 的多平台自动签到助手，采用插件化架构设计，支持百度贴吧、哔哩哔哩、网易云音乐、阿里云网盘等平台的每日签到任务。

## 版本信息

- **当前版本**: 2.0.0
- **Python要求**: 3.11+

## 目录

- [功能特性](#功能特性)
- [技术栈](#技术栈)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [Docker 部署](#docker-部署)
- [项目结构](#项目结构)
- [扩展开发](#扩展开发)
- [常见问题](#常见问题)
- [开源协议](#开源协议)

## 功能特性

### 百度贴吧

- 关注贴吧一键签到
- 自动重签机制
- 登录状态检测

### 哔哩哔哩

| 功能 | 说明 |
|------|------|
| 观看分享视频 | 自动观看并分享视频获取经验 |
| 漫画签到 | 每日漫画签到 |
| 漫画每日阅读 | 自动阅读漫画章节 |
| 漫画权益领取 | 大会员每月领取漫读券 |
| 每日投币任务 | 自动投币（可配置预留硬币数） |
| 直播签到 | 直播间签到获取奖励 |
| 直播送礼 | 自动送出即将过期的礼物 |
| B币券充电 | 年度大会员月底自动使用B币券充电 |

### 网易云音乐

| 功能 | 说明 |
|------|------|
| 每日签到 | 签到获取经验值 |
| 云贝签到 | 每日云贝签到 |
| 每日300首 | 自动播放300首歌曲提升等级（仅限10级以下用户） |

### 阿里云网盘

- 每日签到领取奖励
- 自动刷新Token

### 消息推送

仅推送 Cookie 失效的账号通知：

- 邮箱推送
- Server酱推送

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Python | 3.11+ | 编程语言 |
| httpx | 0.25+ | HTTP 客户端（支持连接池） |
| loguru | 0.7+ | 日志管理 |
| APScheduler | 3.10+ | 任务调度 |
| PyYAML | 6.0+ | YAML 解析 |

## 快速开始

### 环境要求

- Python 3.11 或更高版本
- pip 包管理器

### 本地运行

1. **克隆项目**

```bash
git clone https://github.com/CMTcode/sign-in-assistant.git
cd sign-in-assistant
```

2. **安装依赖**

```bash
pip install -r requirements.txt
```

3. **修改配置文件**

编辑 `config/config.yml` 文件，填入你的账号信息。

4. **运行程序**

```bash
# 启动定时任务模式
python main.py

# 只执行一次后退出
python main.py --once

# 指定配置目录
python main.py -c /path/to/config

# 启用调试模式
python main.py --debug

# 显示版本信息
python main.py --version
```

## 配置说明

配置文件位于 `config/config.yml`，所有配置集中在一个文件中管理：

```yaml
# ============================================
# 签到助手配置文件 v2.0
# ============================================

# 定时任务时间配置
scheduled:
  # cron表达式，默认每日 04:01 执行
  # 可通过 https://cron.qqe2.com/ 生成
  cron: 0 1 4 * * ?

# 消息推送配置
msg:
  # 推送方式: none(不开启) | email(邮件) | server(Server酱)
  pushType: none
  pushConfig:
    mailbox:
      host: smtp.qq.com
      port: 465
      form: 签到系统<xxx@qq.com>
      username: xxxxxx@qq.com
      password: xxxxxx
      email: receiver@example.com
    server:
      key: your-server-chan-key

# ============================================
# 各平台签到配置
# ============================================

# 百度贴吧配置
baidu:
  enabled: false
  accounts:
    - name: 账号1
      bduss: your_bduss_here

# 哔哩哔哩配置
bilibili:
  enabled: false
  accounts:
    - name: 账号1
      DEDEUSERID: xxx
      SESSDATA: xxx
      BILI_JCT: xxx
      reserveCoins: 5      # 预留硬币数
      upLive: 0            # 送礼UP主的UID
      chargeForLove: 0     # 充电对象UID

# 网易云音乐配置
wangyi:
  enabled: false
  accounts:
    - name: 账号1
      cookie: MUSIC_U=xxxxxxx;

# 阿里云网盘配置
aliyundrive:
  enabled: false
  accounts:
    - name: 账号1
      refreshToken: xxxx
```

### Cookie 获取方法

| 平台 | 获取方式 |
|------|----------|
| 百度贴吧 | 登录贴吧首页，F12获取Cookie中的BDUSS |
| 哔哩哔哩 | 登录B站，F12获取Cookie中的DEDEUSERID、SESSDATA、BILI_JCT |
| 网易云音乐 | 登录网易云音乐，F12获取Cookie（必须包含MUSIC_U） |
| 阿里云网盘 | 登录阿里云网盘网页版 -> F12 -> Application -> Local Storage -> token -> refresh_token |

## Docker 部署

### 构建镜像

```bash
docker build -t cmt/signin:2.0 .
```

### 运行容器

```bash
docker run -d \
  --name signin \
  -v /opt/docker/signin/config:/app/config \
  -v /opt/docker/signin/logs:/app/logs \
  cmt/signin:2.0
```

### 使用脚本部署

```bash
chmod +x run.sh
./run.sh
```

## 项目结构

```
sign-in-assistant/
├── config/
│   └── config.yml                # 配置文件
├── signin/                       # 主程序包
│   ├── core/                     # 核心模块
│   │   ├── config.py             # 配置加载
│   │   ├── logger.py             # 日志管理
│   │   ├── http.py               # HTTP客户端（连接池）
│   │   ├── message.py            # 消息推送
│   │   ├── exceptions.py         # 自定义异常
│   │   └── utils.py              # 工具函数
│   ├── scripts/                  # 各平台签到脚本
│   │   ├── base.py               # 任务基类和注册机制
│   │   ├── baidu.py              # 百度贴吧
│   │   ├── bilibili.py           # 哔哩哔哩
│   │   ├── wangyi.py             # 网易云音乐
│   │   └── aliyundrive.py        # 阿里云网盘
│   └── scheduler/                # 任务调度
│       ├── executor.py           # 任务执行器
│       └── scheduler.py          # 定时调度器
├── main.py                       # 程序入口
├── requirements.txt              # 依赖列表
├── Dockerfile
├── run.sh
└── README.md
```

## 扩展开发

### 添加新平台

1. 创建新的签到模块文件 `signin/scripts/new_platform.py`

2. 继承 `BaseTask` 类并使用 `@register_task` 装饰器：

```python
from signin.scripts.base import BaseTask, register_task

@register_task
class NewPlatformTask(BaseTask):
    """新平台签到任务"""
    
    task_name = "new_platform"
    platform = "new_platform"
    
    def __init__(self, account, message_service=None):
        super().__init__(account.name)
        # 初始化代码
    
    def check_authentication(self) -> bool:
        """检查登录状态"""
        # 实现登录检查逻辑
        return True
    
    def execute(self) -> dict:
        """执行签到任务"""
        # 实现签到逻辑
        return {"success": True}
```

3. 在配置中添加新平台配置项

4. 在 `executor.py` 中加载新任务

## 常见问题

### 定时任务不执行？

检查 `config/config.yml` 中的 cron 表达式是否正确，默认为每日 04:01 执行。

### Cookie 失效怎么办？

程序会在启动时和每次签到前检测登录状态，失效时会通过配置的消息推送方式通知。

### 如何查看执行报告？

执行 `python main.py --once` 后会显示执行报告，包括成功/失败数量和耗时。

## 开源协议

本项目基于 [GPL-2.0](LICENSE) 协议开源。

## 访问量

![](http://profile-counter.glitch.me/CMTcode/count.svg)

## 历史 Star 数

![](https://starchart.cc/CMTcode/sign-in-assistant.svg)
