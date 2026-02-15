"""
配置加载模块
负责加载和解析YAML配置文件
"""
import os
from pathlib import Path
from dataclasses import dataclass, field
from typing import List, Optional, Dict, Any
import yaml


@dataclass
class Account:
    """账号基类"""
    name: str


@dataclass
class BaiduAccount(Account):
    """百度贴吧账号"""
    bduss: str = ""


@dataclass
class BilibiliAccount(Account):
    """哔哩哔哩账号"""
    DEDEUSERID: str = ""
    SESSDATA: str = ""
    BILI_JCT: str = ""
    reserveCoins: int = 5
    upLive: str = "0"
    chargeForLove: str = "0"
    userAgent: str = ""


@dataclass
class WangyiAccount(Account):
    """网易云音乐账号"""
    cookie: str = ""


@dataclass
class AliyundriveAccount(Account):
    """阿里云网盘账号"""
    refreshToken: str = ""


@dataclass
class MailboxConfig:
    """邮箱配置"""
    host: str = "smtp.qq.com"
    port: int = 465
    form: str = ""
    username: str = ""
    password: str = ""
    email: str = ""


@dataclass
class ServerConfig:
    """Server酱配置"""
    key: str = ""


@dataclass
class PushConfig:
    """推送配置"""
    mailbox: MailboxConfig = field(default_factory=MailboxConfig)
    server: ServerConfig = field(default_factory=ServerConfig)


@dataclass
class MsgConfig:
    """消息配置"""
    pushType: str = "none"
    pushConfig: PushConfig = field(default_factory=PushConfig)


@dataclass
class ScheduledConfig:
    """定时任务配置"""
    cron: str = "0 1 4 * * ?"


@dataclass
class AppConfig:
    """应用主配置"""
    scheduled: ScheduledConfig = field(default_factory=ScheduledConfig)
    msg: MsgConfig = field(default_factory=MsgConfig)


@dataclass
class BaiduConfig:
    """百度贴吧配置"""
    enabled: bool = False
    accounts: List[BaiduAccount] = field(default_factory=list)


@dataclass
class BilibiliConfig:
    """哔哩哔哩配置"""
    enabled: bool = False
    accounts: List[BilibiliAccount] = field(default_factory=list)


@dataclass
class WangyiConfig:
    """网易云音乐配置"""
    enabled: bool = False
    accounts: List[WangyiAccount] = field(default_factory=list)


@dataclass
class AliyundriveConfig:
    """阿里云网盘配置"""
    enabled: bool = False
    accounts: List[AliyundriveAccount] = field(default_factory=list)


class Config:
    """
    全局配置类
    管理所有平台和应用的配置
    """
    
    _instance = None
    _config_dir: Path = None
    
    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance
    
    def __init__(self):
        if self._initialized:
            return
        self._initialized = True
        self.app: AppConfig = AppConfig()
        self.baidu: BaiduConfig = BaiduConfig()
        self.bilibili: BilibiliConfig = BilibiliConfig()
        self.wangyi: WangyiConfig = WangyiConfig()
        self.aliyundrive: AliyundriveConfig = AliyundriveConfig()
    
    @property
    def config_dir(self) -> Path:
        """获取配置文件目录"""
        if self._config_dir is None:
            self._config_dir = Path(os.getcwd()) / "config"
        return self._config_dir
    
    @config_dir.setter
    def config_dir(self, path: str):
        """设置配置文件目录"""
        self._config_dir = Path(path)
    
    def load_yaml(self, filename: str) -> Dict[str, Any]:
        """
        加载YAML配置文件
        
        Args:
            filename: 配置文件名
            
        Returns:
            解析后的字典
        """
        filepath = self.config_dir / filename
        if not filepath.exists():
            return {}
        with open(filepath, 'r', encoding='utf-8') as f:
            return yaml.safe_load(f) or {}
    
    def _parse_scheduled(self, data: Dict[str, Any]):
        """解析定时任务配置"""
        scheduled_data = data.get('scheduled', {})
        self.app.scheduled.cron = scheduled_data.get('cron', '0 1 4 * * ?')
    
    def _parse_msg(self, data: Dict[str, Any]):
        """解析消息推送配置"""
        msg_data = data.get('msg', {})
        self.app.msg.pushType = msg_data.get('pushType', 'none')
        
        push_config = msg_data.get('pushConfig', {})
        mailbox = push_config.get('mailbox', {})
        self.app.msg.pushConfig.mailbox.host = mailbox.get('host', 'smtp.qq.com')
        self.app.msg.pushConfig.mailbox.port = mailbox.get('port', 465)
        self.app.msg.pushConfig.mailbox.form = mailbox.get('form', '')
        self.app.msg.pushConfig.mailbox.username = mailbox.get('username', '')
        self.app.msg.pushConfig.mailbox.password = mailbox.get('password', '')
        self.app.msg.pushConfig.mailbox.email = mailbox.get('email', '')
        
        server = push_config.get('server', {})
        self.app.msg.pushConfig.server.key = server.get('key', '')
    
    def _parse_baidu(self, data: Dict[str, Any]):
        """解析百度贴吧配置"""
        baidu_data = data.get('baidu', {})
        self.baidu.enabled = baidu_data.get('enabled', False)
        accounts = baidu_data.get('accounts', [])
        self.baidu.accounts = [
            BaiduAccount(
                name=acc.get('name', ''),
                bduss=acc.get('bduss', '')
            )
            for acc in accounts
        ]
    
    def _parse_bilibili(self, data: Dict[str, Any]):
        """解析哔哩哔哩配置"""
        bilibili_data = data.get('bilibili', {})
        self.bilibili.enabled = bilibili_data.get('enabled', False)
        accounts = bilibili_data.get('accounts', [])
        self.bilibili.accounts = [
            BilibiliAccount(
                name=acc.get('name', ''),
                DEDEUSERID=acc.get('DEDEUSERID', ''),
                SESSDATA=acc.get('SESSDATA', ''),
                BILI_JCT=acc.get('BILI_JCT', ''),
                reserveCoins=acc.get('reserveCoins', 5),
                upLive=str(acc.get('upLive', '0')),
                chargeForLove=str(acc.get('chargeForLove', '0')),
                userAgent=acc.get('userAgent', '')
            )
            for acc in accounts
        ]
    
    def _parse_wangyi(self, data: Dict[str, Any]):
        """解析网易云音乐配置"""
        wangyi_data = data.get('wangyi', {})
        self.wangyi.enabled = wangyi_data.get('enabled', False)
        accounts = wangyi_data.get('accounts', [])
        self.wangyi.accounts = [
            WangyiAccount(
                name=acc.get('name', ''),
                cookie=acc.get('cookie', '')
            )
            for acc in accounts
        ]
    
    def _parse_aliyundrive(self, data: Dict[str, Any]):
        """解析阿里云网盘配置"""
        aliyundrive_data = data.get('aliyundrive', {})
        self.aliyundrive.enabled = aliyundrive_data.get('enabled', False)
        accounts = aliyundrive_data.get('accounts', [])
        self.aliyundrive.accounts = [
            AliyundriveAccount(
                name=acc.get('name', ''),
                refreshToken=acc.get('refreshToken', '')
            )
            for acc in accounts
        ]
    
    def load_from_single_file(self):
        """从单一配置文件加载所有配置"""
        data = self.load_yaml("config.yml")
        if not data:
            return False
        
        self._parse_scheduled(data)
        self._parse_msg(data)
        self._parse_baidu(data)
        self._parse_bilibili(data)
        self._parse_wangyi(data)
        self._parse_aliyundrive(data)
        
        return True
    
    def load_from_multiple_files(self):
        """从多个配置文件加载配置（兼容旧版本）"""
        self.load_app_config()
        self.load_baidu_config()
        self.load_bilibili_config()
        self.load_wangyi_config()
        self.load_aliyundrive_config()
    
    def load_app_config(self):
        """加载应用主配置"""
        data = self.load_yaml("appconfig.yml")
        if not data:
            return
        
        self._parse_scheduled(data)
        self._parse_msg(data)
    
    def load_baidu_config(self):
        """加载百度贴吧配置"""
        data = self.load_yaml("baidu.yml")
        if not data:
            return
        
        self.baidu.enabled = data.get('enabled', False)
        accounts = data.get('accounts', [])
        self.baidu.accounts = [
            BaiduAccount(
                name=acc.get('name', ''),
                bduss=acc.get('bduss', '')
            )
            for acc in accounts
        ]
    
    def load_bilibili_config(self):
        """加载哔哩哔哩配置"""
        data = self.load_yaml("bilbil.yml")
        if not data:
            return
        
        self._parse_bilibili({'bilibili': data})
    
    def load_wangyi_config(self):
        """加载网易云音乐配置"""
        data = self.load_yaml("wangyi.yml")
        if not data:
            return
        
        self._parse_wangyi({'wangyi': data})
    
    def load_aliyundrive_config(self):
        """加载阿里云网盘配置"""
        data = self.load_yaml("aliyundrive.yml")
        if not data:
            return
        
        self._parse_aliyundrive({'aliyundrive': data})
    
    def load_all(self):
        """加载所有配置（优先使用单一配置文件）"""
        if self.load_from_single_file():
            return
        
        self.load_from_multiple_files()


def load_config(config_dir: str = None) -> Config:
    """
    加载配置的便捷函数
    
    Args:
        config_dir: 配置文件目录路径
        
    Returns:
        Config实例
    """
    config = Config()
    if config_dir:
        config.config_dir = config_dir
    config.load_all()
    return config
