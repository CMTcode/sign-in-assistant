"""
消息推送模块
支持邮箱和Server酱推送
"""
from abc import ABC, abstractmethod
from typing import Optional
import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
import httpx
from loguru import logger

from signin.core.config import Config, MailboxConfig, ServerConfig


class MessageSender(ABC):
    """
    消息发送器抽象基类
    """
    
    @abstractmethod
    def send(self, title: str, message: str) -> bool:
        """
        发送消息
        
        Args:
            title: 消息标题
            message: 消息内容
            
        Returns:
            是否发送成功
        """
        pass


class EmailSender(MessageSender):
    """
    邮箱消息发送器
    """
    
    def __init__(self, config: MailboxConfig):
        """
        初始化邮箱发送器
        
        Args:
            config: 邮箱配置
        """
        self.config = config
    
    def send(self, title: str, message: str) -> bool:
        """
        发送邮件
        
        Args:
            title: 邮件标题
            message: 邮件内容
            
        Returns:
            是否发送成功
        """
        if not self.config.email:
            logger.warning("未配置邮件接收人，跳过邮件发送")
            return False
        
        try:
            msg = MIMEMultipart()
            msg['From'] = self.config.form
            msg['To'] = self.config.email
            msg['Subject'] = title
            
            msg.attach(MIMEText(message, 'plain', 'utf-8'))
            
            with smtplib.SMTP_SSL(self.config.host, self.config.port) as server:
                server.login(self.config.username, self.config.password)
                server.sendmail(self.config.username, self.config.email, msg.as_string())
            
            logger.info(f"邮件发送成功: {title}")
            return True
        except Exception as e:
            logger.error(f"邮件发送失败: {e}")
            return False


class ServerChanSender(MessageSender):
    """
    Server酱消息发送器
    """
    
    API_URL = "https://sctapi.ftqq.com/{key}.send"
    
    def __init__(self, config: ServerConfig):
        """
        初始化Server酱发送器
        
        Args:
            config: Server酱配置
        """
        self.config = config
    
    def send(self, title: str, message: str) -> bool:
        """
        发送Server酱消息
        
        Args:
            title: 消息标题
            message: 消息内容
            
        Returns:
            是否发送成功
        """
        if not self.config.key:
            logger.warning("未配置Server酱Key，跳过发送")
            return False
        
        try:
            url = self.API_URL.format(key=self.config.key)
            data = {
                "title": title,
                "desp": message
            }
            
            with httpx.Client() as client:
                response = client.post(url, data=data)
                result = response.json()
            
            if result.get('code') == 0:
                logger.info(f"Server酱推送成功: {title}")
                return True
            else:
                logger.error(f"Server酱推送失败: {result}")
                return False
        except Exception as e:
            logger.error(f"Server酱推送异常: {e}")
            return False


class MessageService:
    """
    消息推送服务
    根据配置选择推送方式
    """
    
    def __init__(self, config: Config):
        """
        初始化消息服务
        
        Args:
            config: 全局配置
        """
        self.config = config
        self._sender: Optional[MessageSender] = None
    
    @property
    def sender(self) -> Optional[MessageSender]:
        """获取消息发送器"""
        if self._sender is not None:
            return self._sender
        
        push_type = self.config.app.msg.pushType
        if push_type == 'email':
            self._sender = EmailSender(self.config.app.msg.pushConfig.mailbox)
        elif push_type == 'server':
            self._sender = ServerChanSender(self.config.app.msg.pushConfig.server)
        elif push_type == 'none':
            self._sender = None
        else:
            logger.warning(f"未知的推送类型: {push_type}")
            self._sender = None
        
        return self._sender
    
    def send(self, title: str, message: str) -> bool:
        """
        发送消息
        
        Args:
            title: 消息标题
            message: 消息内容
            
        Returns:
            是否发送成功
        """
        if self.sender is None:
            logger.debug("消息推送未启用")
            return False
        
        return self.sender.send(title, message)
    
    def notify_cookie_expired(self, platform: str, account_name: str):
        """
        通知Cookie失效
        
        Args:
            platform: 平台名称
            account_name: 账号名称
        """
        title = f"【签到助手】{platform}账号Cookie失效通知"
        message = f"您的{platform}账号【{account_name}】Cookie已失效，请及时更新配置。"
        self.send(title, message)
