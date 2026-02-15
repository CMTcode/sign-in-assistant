"""
任务调度模块
支持定时执行、并发执行和任务管理
"""
import asyncio
from datetime import datetime
from typing import List, Dict, Any, Optional
from concurrent.futures import ThreadPoolExecutor
from dataclasses import dataclass, field
from loguru import logger

from signin.core import Config, load_config, MessageService
from signin.scripts.base import BaseTask, TaskResult, TaskStatus, TaskRegistry
from signin.scripts.baidu import BaiduTiebaTask
from signin.scripts.bilibili import BilibiliTask
from signin.scripts.wangyi import WangyiMusicTask
from signin.scripts.aliyundrive import AliyundriveTask


@dataclass
class ExecutionReport:
    """
    执行报告
    """
    start_time: datetime
    end_time: Optional[datetime] = None
    total_tasks: int = 0
    success_count: int = 0
    failed_count: int = 0
    results: List[TaskResult] = field(default_factory=list)
    
    @property
    def duration_seconds(self) -> float:
        """执行耗时（秒）"""
        if self.end_time:
            return (self.end_time - self.start_time).total_seconds()
        return 0.0
    
    def to_dict(self) -> Dict[str, Any]:
        """转换为字典"""
        return {
            "start_time": self.start_time.isoformat(),
            "end_time": self.end_time.isoformat() if self.end_time else None,
            "duration_seconds": self.duration_seconds,
            "total_tasks": self.total_tasks,
            "success_count": self.success_count,
            "failed_count": self.failed_count,
            "results": [r.to_dict() for r in self.results]
        }


class TaskExecutor:
    """
    任务执行器
    管理和执行所有签到任务，支持并发执行
    """
    
    def __init__(self, config: Optional[Config] = None):
        """
        初始化任务执行器
        
        Args:
            config: 配置对象，如果为None则自动加载
        """
        self.config = config or load_config()
        self.message_service = MessageService(self.config)
        self._tasks: List[BaseTask] = []
        self._report: Optional[ExecutionReport] = None
    
    @property
    def report(self) -> Optional[ExecutionReport]:
        """获取最近一次执行报告"""
        return self._report
    
    def load_tasks(self) -> None:
        """
        加载所有已启用的任务
        """
        self._tasks.clear()
        
        if self.config.baidu.enabled:
            for account in self.config.baidu.accounts:
                task = BaiduTiebaTask(account, self.message_service)
                self._tasks.append(task)
        
        if self.config.bilibili.enabled:
            for account in self.config.bilibili.accounts:
                task = BilibiliTask(account, self.message_service)
                self._tasks.append(task)
        
        if self.config.wangyi.enabled:
            for account in self.config.wangyi.accounts:
                task = WangyiMusicTask(account, self.message_service)
                self._tasks.append(task)
        
        if self.config.aliyundrive.enabled:
            for account in self.config.aliyundrive.accounts:
                task = AliyundriveTask(account, self.message_service)
                self._tasks.append(task)
        
        logger.info(f"已加载 {len(self._tasks)} 个签到任务")
    
    def run_all(self, max_workers: int = 4) -> ExecutionReport:
        """
        执行所有任务
        
        Args:
            max_workers: 最大并发数
            
        Returns:
            执行报告
        """
        self.load_tasks()
        
        if not self._tasks:
            logger.warning("没有已启用的签到任务")
            return ExecutionReport(start_time=datetime.now())
        
        report = ExecutionReport(
            start_time=datetime.now(),
            total_tasks=len(self._tasks)
        )
        
        logger.info(f"开始执行签到任务，共 {len(self._tasks)} 个账号")
        
        with ThreadPoolExecutor(max_workers=max_workers) as executor:
            futures = [executor.submit(self._run_task, task) for task in self._tasks]
            
            for future in futures:
                result = future.result()
                report.results.append(result)
                if result.status == TaskStatus.SUCCESS:
                    report.success_count += 1
                else:
                    report.failed_count += 1
        
        report.end_time = datetime.now()
        self._report = report
        
        logger.info(
            f"所有签到任务完成 - 成功: {report.success_count}, "
            f"失败: {report.failed_count}, "
            f"耗时: {report.duration_seconds:.2f}秒"
        )
        
        return report
    
    def _run_task(self, task: BaseTask) -> TaskResult:
        """
        执行单个任务
        
        Args:
            task: 任务实例
            
        Returns:
            任务结果
        """
        try:
            return task.run()
        except Exception as e:
            logger.exception(f"任务执行异常: {task.account_name} - {e}")
            return TaskResult(
                task_name=task.task_name,
                account_name=task.account_name,
                status=TaskStatus.FAILED,
                message=str(e),
                error=e
            )
    
    def get_registered_platforms(self) -> List[str]:
        """
        获取已注册的平台列表
        
        Returns:
            平台名称列表
        """
        return TaskRegistry.list_tasks()
