"""
自定义异常类
提供统一的异常处理机制
"""
from typing import Optional, Any


class SignInError(Exception):
    """
    签到助手基础异常类
    所有自定义异常都应继承此类
    """
    
    def __init__(self, message: str, code: Optional[int] = None, details: Optional[Any] = None):
        self.message = message
        self.code = code
        self.details = details
        super().__init__(self.message)
    
    def __str__(self):
        if self.code:
            return f"[{self.code}] {self.message}"
        return self.message


class AuthenticationError(SignInError):
    """
    认证错误异常
    用于Cookie失效、登录失败等认证相关问题
    """
    
    def __init__(self, message: str = "认证失败，请检查Cookie是否有效", platform: Optional[str] = None):
        self.platform = platform
        super().__init__(message, code=401)


class NetworkError(SignInError):
    """
    网络错误异常
    用于网络请求失败、超时等问题
    """
    
    def __init__(self, message: str = "网络请求失败", url: Optional[str] = None):
        self.url = url
        super().__init__(message, code=503)


class ConfigurationError(SignInError):
    """
    配置错误异常
    用于配置文件缺失、格式错误等问题
    """
    
    def __init__(self, message: str = "配置错误", config_file: Optional[str] = None):
        self.config_file = config_file
        super().__init__(message, code=400)


class TaskExecutionError(SignInError):
    """
    任务执行错误异常
    用于任务执行过程中的错误
    """
    
    def __init__(self, message: str, task_name: Optional[str] = None, account_name: Optional[str] = None):
        self.task_name = task_name
        self.account_name = account_name
        super().__init__(message, code=500)
