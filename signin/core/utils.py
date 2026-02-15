"""
工具函数模块
提供通用工具函数和装饰器
"""
import random
import time
import hashlib
import functools
from typing import Dict, Callable, Optional, Any, TypeVar, ParamSpec
from loguru import logger

P = ParamSpec('P')
T = TypeVar('T')


USER_AGENTS = [
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15",
]


def get_random_user_agent() -> str:
    """
    获取随机User-Agent
    
    Returns:
        随机User-Agent字符串
    """
    return random.choice(USER_AGENTS)


def random_sleep(min_ms: int = 500, max_ms: int = 2000) -> None:
    """
    随机休眠
    
    Args:
        min_ms: 最小毫秒数
        max_ms: 最大毫秒数
    """
    sleep_time = random.randint(min_ms, max_ms) / 1000
    time.sleep(sleep_time)


def md5_hash(text: str) -> str:
    """
    计算MD5哈希值
    
    Args:
        text: 待哈希的文本
        
    Returns:
        MD5哈希字符串
    """
    return hashlib.md5(text.encode('utf-8')).hexdigest()


def parse_cookie_string(cookie_str: str) -> Dict[str, str]:
    """
    解析Cookie字符串为字典
    
    Args:
        cookie_str: Cookie字符串
        
    Returns:
        Cookie字典
    """
    cookies = {}
    if not cookie_str:
        return cookies
    
    for item in cookie_str.split(';'):
        item = item.strip()
        if '=' in item:
            key, value = item.split('=', 1)
            cookies[key.strip()] = value.strip()
    
    return cookies


def dict_to_cookie_str(cookie_dict: Dict[str, str]) -> str:
    """
    将Cookie字典转换为字符串
    
    Args:
        cookie_dict: Cookie字典
        
    Returns:
        Cookie字符串
    """
    return '; '.join([f"{k}={v}" for k, v in cookie_dict.items()])


def mask_string(text: str, show_len: int = 4) -> str:
    """
    遮蔽字符串中间部分
    
    Args:
        text: 原始字符串
        show_len: 显示的字符数
        
    Returns:
        遮蔽后的字符串
    """
    if len(text) <= show_len * 2:
        return text[:2] + '*' * (len(text) - 2)
    return text[:show_len] + '*' * (len(text) - show_len * 2) + text[-show_len:]


def retry_on_failure(
    max_retries: int = 3,
    delay: float = 1.0,
    backoff: float = 2.0,
    exceptions: tuple = (Exception,),
    on_retry: Optional[Callable] = None
) -> Callable[[Callable[P, T]], Callable[P, T]]:
    """
    失败重试装饰器
    
    Args:
        max_retries: 最大重试次数
        delay: 初始延迟时间（秒）
        backoff: 延迟时间增长因子
        exceptions: 需要重试的异常类型
        on_retry: 重试时的回调函数
        
    Returns:
        装饰器函数
    """
    def decorator(func: Callable[P, T]) -> Callable[P, T]:
        @functools.wraps(func)
        def wrapper(*args: P.args, **kwargs: P.kwargs) -> T:
            current_delay = delay
            last_exception = None
            
            for attempt in range(max_retries + 1):
                try:
                    return func(*args, **kwargs)
                except exceptions as e:
                    last_exception = e
                    if attempt < max_retries:
                        if on_retry:
                            on_retry(attempt + 1, max_retries, e)
                        logger.warning(
                            f"{func.__name__} 执行失败，{current_delay:.1f}秒后重试 "
                            f"({attempt + 1}/{max_retries}): {e}"
                        )
                        time.sleep(current_delay)
                        current_delay *= backoff
                    else:
                        logger.error(f"{func.__name__} 重试{max_retries}次后仍然失败")
                        raise
            
            raise last_exception
        
        return wrapper
    
    return decorator


def measure_time(func: Callable[P, T]) -> Callable[P, T]:
    """
    测量函数执行时间的装饰器
    
    Args:
        func: 被装饰的函数
        
    Returns:
        装饰后的函数
    """
    @functools.wraps(func)
    def wrapper(*args: P.args, **kwargs: P.kwargs) -> T:
        start_time = time.perf_counter()
        result = func(*args, **kwargs)
        end_time = time.perf_counter()
        elapsed_ms = (end_time - start_time) * 1000
        logger.debug(f"{func.__name__} 执行耗时: {elapsed_ms:.2f}ms")
        return result
    
    return wrapper
