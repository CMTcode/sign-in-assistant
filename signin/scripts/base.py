"""
任务插件基类
提供统一的任务接口和生命周期管理
"""
from abc import ABC, abstractmethod
from dataclasses import dataclass, field
from typing import Dict, Any, Optional, List
from datetime import datetime
from enum import Enum
from loguru import logger

from signin.core.exceptions import TaskExecutionError, AuthenticationError


class TaskStatus(Enum):
    """任务状态枚举"""
    PENDING = "pending"
    RUNNING = "running"
    SUCCESS = "success"
    FAILED = "failed"
    SKIPPED = "skipped"


@dataclass
class TaskResult:
    """
    任务执行结果
    """
    task_name: str
    account_name: str
    status: TaskStatus
    message: str = ""
    data: Dict[str, Any] = field(default_factory=dict)
    error: Optional[Exception] = None
    start_time: Optional[datetime] = None
    end_time: Optional[datetime] = None
    
    @property
    def duration_ms(self) -> Optional[float]:
        """计算执行耗时（毫秒）"""
        if self.start_time and self.end_time:
            return (self.end_time - self.start_time).total_seconds() * 1000
        return None
    
    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            "task_name": self.task_name,
            "account_name": self.account_name,
            "status": self.status.value,
            "message": self.message,
            "data": self.data,
            "error": str(self.error) if self.error else None,
            "duration_ms": self.duration_ms,
        }


class BaseTask(ABC):
    """
    任务基类
    所有签到任务都应继承此类
    """
    
    task_name: str = "base_task"
    platform: str = "unknown"
    
    def __init__(self, account_name: str = ""):
        """
        初始化任务
        
        Args:
            account_name: 账号名称
        """
        self.account_name = account_name
        self._status = TaskStatus.PENDING
        self._result: Optional[TaskResult] = None
    
    @property
    def status(self) -> TaskStatus:
        """获取任务状态"""
        return self._status
    
    @property
    def result(self) -> Optional[TaskResult]:
        """获取任务结果"""
        return self._result
    
    @abstractmethod
    def check_authentication(self) -> bool:
        """
        检查认证状态
        
        Returns:
            是否认证成功
        """
        pass
    
    @abstractmethod
    def execute(self) -> Dict[str, Any]:
        """
        执行任务核心逻辑
        
        Returns:
            执行结果数据
        """
        pass
    
    def pre_execute(self) -> None:
        """
        执行前钩子
        子类可重写此方法进行前置处理
        """
        logger.info(f"开始执行任务: [{self.platform}] {self.account_name}")
    
    def post_execute(self, result: Dict[str, Any]) -> None:
        """
        执行后钩子
        子类可重写此方法进行后置处理
        
        Args:
            result: 执行结果
        """
        pass
    
    def on_error(self, error: Exception) -> None:
        """
        错误处理钩子
        子类可重写此方法进行错误处理
        
        Args:
            error: 异常对象
        """
        logger.error(f"任务执行失败: [{self.platform}] {self.account_name} - {error}")
    
    def run(self) -> TaskResult:
        """
        运行任务（模板方法）
        
        Returns:
            任务执行结果
        """
        start_time = datetime.now()
        self._status = TaskStatus.RUNNING
        
        try:
            if not self.check_authentication():
                raise AuthenticationError(
                    f"认证失败，请检查Cookie是否有效",
                    platform=self.platform
                )
            
            self.pre_execute()
            result_data = self.execute()
            self.post_execute(result_data)
            
            self._status = TaskStatus.SUCCESS
            self._result = TaskResult(
                task_name=self.task_name,
                account_name=self.account_name,
                status=self._status,
                message="执行成功",
                data=result_data,
                start_time=start_time,
                end_time=datetime.now()
            )
            
        except AuthenticationError as e:
            self._status = TaskStatus.FAILED
            self._result = TaskResult(
                task_name=self.task_name,
                account_name=self.account_name,
                status=self._status,
                message=f"认证失败: {e}",
                error=e,
                start_time=start_time,
                end_time=datetime.now()
            )
            self.on_error(e)
            
        except Exception as e:
            self._status = TaskStatus.FAILED
            self._result = TaskResult(
                task_name=self.task_name,
                account_name=self.account_name,
                status=self._status,
                message=f"执行失败: {e}",
                error=e,
                start_time=start_time,
                end_time=datetime.now()
            )
            self.on_error(e)
        
        return self._result


class CompositeTask(BaseTask):
    """
    复合任务
    包含多个子任务的组合任务
    """
    
    task_name = "composite_task"
    
    def __init__(self, account_name: str = "", sub_tasks: Optional[List[BaseTask]] = None):
        super().__init__(account_name)
        self.sub_tasks = sub_tasks or []
        self._sub_results: List[TaskResult] = []
    
    def add_sub_task(self, task: BaseTask) -> None:
        """
        添加子任务
        
        Args:
            task: 子任务实例
        """
        self.sub_tasks.append(task)
    
    def check_authentication(self) -> bool:
        """检查认证状态"""
        return True
    
    def execute(self) -> Dict[str, Any]:
        """
        执行所有子任务
        
        Returns:
            汇总结果
        """
        results = []
        success_count = 0
        
        for task in self.sub_tasks:
            result = task.run()
            results.append(result)
            if result.status == TaskStatus.SUCCESS:
                success_count += 1
        
        return {
            "total": len(self.sub_tasks),
            "success": success_count,
            "failed": len(self.sub_tasks) - success_count,
            "results": [r.to_dict() for r in results]
        }


class TaskRegistry:
    """
    任务注册表
    管理所有可用的任务类型
    """
    
    _tasks: Dict[str, type] = {}
    
    @classmethod
    def register(cls, task_class: type) -> type:
        """
        注册任务类
        
        Args:
            task_class: 任务类
            
        Returns:
            原任务类（支持装饰器用法）
        """
        if not issubclass(task_class, BaseTask):
            raise TypeError(f"{task_class} 必须继承自 BaseTask")
        
        platform = getattr(task_class, 'platform', 'unknown')
        cls._tasks[platform] = task_class
        logger.debug(f"注册任务: {platform} -> {task_class.__name__}")
        return task_class
    
    @classmethod
    def get_task_class(cls, platform: str) -> Optional[type]:
        """
        获取任务类
        
        Args:
            platform: 平台名称
            
        Returns:
            任务类或None
        """
        return cls._tasks.get(platform)
    
    @classmethod
    def list_tasks(cls) -> List[str]:
        """
        列出所有已注册的平台
        
        Returns:
            平台名称列表
        """
        return list(cls._tasks.keys())


def register_task(task_class: type) -> type:
    """
    任务注册装饰器
    
    Args:
        task_class: 任务类
        
    Returns:
        原任务类
    """
    return TaskRegistry.register(task_class)
