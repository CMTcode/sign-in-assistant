"""
签到脚本模块
提供插件化的任务注册机制
"""
from .base import (
    BaseTask,
    CompositeTask,
    TaskStatus,
    TaskResult,
    TaskRegistry,
    register_task
)

__all__ = [
    'BaseTask',
    'CompositeTask',
    'TaskStatus',
    'TaskResult',
    'TaskRegistry',
    'register_task',
]
