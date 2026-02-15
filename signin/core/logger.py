"""
日志配置模块
使用loguru进行日志管理
"""
import sys
from pathlib import Path
from typing import Optional
from loguru import logger


def setup_logger(
    log_dir: str = "logs",
    level: str = "INFO",
    rotation: str = "00:00",
    retention: str = "30 days",
    compression: str = "zip"
) -> None:
    """
    配置日志系统
    
    Args:
        log_dir: 日志文件目录
        level: 日志级别
        rotation: 日志轮转时间
        retention: 日志保留时间
        compression: 日志压缩格式
    """
    log_path = Path(log_dir)
    log_path.mkdir(parents=True, exist_ok=True)
    
    logger.remove()
    
    logger.add(
        sys.stdout,
        level=level,
        format="<green>{time:YYYY-MM-DD HH:mm:ss}</green> | "
               "<level>{level: <8}</level> | "
               "<cyan>{name}</cyan>:<cyan>{function}</cyan>:<cyan>{line}</cyan> - "
               "<level>{message}</level>",
        colorize=True
    )
    
    logger.add(
        log_path / "signin_{time:YYYY-MM-DD}.log",
        level=level,
        format="{time:YYYY-MM-DD HH:mm:ss} | {level: <8} | {name}:{function}:{line} - {message}",
        rotation=rotation,
        retention=retention,
        compression=compression,
        encoding="utf-8"
    )
    
    logger.add(
        log_path / "error_{time:YYYY-MM-DD}.log",
        level="ERROR",
        format="{time:YYYY-MM-DD HH:mm:ss} | {level: <8} | {name}:{function}:{line} - {message}\n{exception}",
        rotation=rotation,
        retention=retention,
        compression=compression,
        encoding="utf-8"
    )
    
    logger.add(
        log_path / "performance_{time:YYYY-MM-DD}.log",
        level="DEBUG",
        format="{time:YYYY-MM-DD HH:mm:ss} | {level: <8} | {message}",
        rotation=rotation,
        retention=retention,
        compression=compression,
        encoding="utf-8",
        filter=lambda record: "performance" in record["extra"]
    )


def get_logger(name: Optional[str] = None):
    """
    获取logger实例
    
    Args:
        name: logger名称
        
    Returns:
        logger实例
    """
    if name:
        return logger.bind(name=name)
    return logger
