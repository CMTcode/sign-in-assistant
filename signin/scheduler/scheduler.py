"""
定时任务调度器
基于APScheduler实现
"""
from datetime import datetime
from typing import Optional, Callable
from loguru import logger
from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.triggers.cron import CronTrigger
from apscheduler.events import EVENT_JOB_EXECUTED, EVENT_JOB_ERROR

from signin.core import Config, load_config
from .executor import TaskExecutor, ExecutionReport


class Scheduler:
    """
    定时任务调度器
    支持cron表达式和事件监听
    """
    
    def __init__(self, config: Optional[Config] = None):
        """
        初始化调度器
        
        Args:
            config: 配置对象
        """
        self.config = config or load_config()
        self.executor = TaskExecutor(self.config)
        self.scheduler = BackgroundScheduler()
        self._last_report: Optional[ExecutionReport] = None
        
        self.scheduler.add_listener(
            self._job_executed,
            EVENT_JOB_EXECUTED | EVENT_JOB_ERROR
        )
    
    @property
    def last_report(self) -> Optional[ExecutionReport]:
        """获取最近一次执行报告"""
        return self._last_report
    
    def parse_cron(self, cron_expr: str) -> CronTrigger:
        """
        解析cron表达式
        
        Args:
            cron_expr: cron表达式（秒 分 时 日 月 周）
            
        Returns:
            CronTrigger实例
        """
        parts = cron_expr.strip().split()
        
        if len(parts) == 6:
            second, minute, hour, day, month, day_of_week = parts
        elif len(parts) == 5:
            second = '0'
            minute, hour, day, month, day_of_week = parts
        else:
            raise ValueError(f"无效的cron表达式: {cron_expr}")
        
        return CronTrigger(
            second=second,
            minute=minute,
            hour=hour,
            day=day,
            month=month,
            day_of_week=day_of_week
        )
    
    def add_job(self, job_func: Optional[Callable] = None) -> None:
        """
        添加定时任务
        
        Args:
            job_func: 任务函数，默认为执行所有签到任务
        """
        cron_expr = self.config.app.scheduled.cron
        trigger = self.parse_cron(cron_expr)
        
        func = job_func or self.executor.run_all
        
        self.scheduler.add_job(
            func,
            trigger=trigger,
            id='signin_task',
            name='签到任务',
            replace_existing=True
        )
        
        logger.info(f"定时任务已添加，cron表达式: {cron_expr}")
    
    def add_custom_job(
        self,
        job_func: Callable,
        job_id: str,
        cron_expr: str,
        name: str = ""
    ) -> None:
        """
        添加自定义定时任务
        
        Args:
            job_func: 任务函数
            job_id: 任务ID
            cron_expr: cron表达式
            name: 任务名称
        """
        trigger = self.parse_cron(cron_expr)
        
        self.scheduler.add_job(
            job_func,
            trigger=trigger,
            id=job_id,
            name=name or job_id,
            replace_existing=True
        )
        
        logger.info(f"自定义任务已添加: {name or job_id}, cron: {cron_expr}")
    
    def _job_executed(self, event) -> None:
        """
        任务执行事件处理
        
        Args:
            event: 事件对象
        """
        if event.exception:
            logger.error(f"定时任务执行失败: {event.exception}")
        else:
            logger.info("定时任务执行完成")
            self._last_report = self.executor.report
    
    def start(self) -> None:
        """启动调度器"""
        self.add_job()
        self.scheduler.start()
        logger.info("定时任务调度器已启动")
    
    def stop(self) -> None:
        """停止调度器"""
        self.scheduler.shutdown(wait=True)
        logger.info("定时任务调度器已停止")
    
    def run_once(self) -> ExecutionReport:
        """
        立即执行一次任务
        
        Returns:
            执行报告
        """
        logger.info("手动触发签到任务")
        self._last_report = self.executor.run_all()
        return self._last_report
    
    def get_next_run_time(self) -> Optional[datetime]:
        """
        获取下次执行时间
        
        Returns:
            下次执行时间
        """
        job = self.scheduler.get_job('signin_task')
        if job:
            return job.next_run_time
        return None
    
    def get_jobs(self) -> list:
        """
        获取所有任务
        
        Returns:
            任务列表
        """
        return self.scheduler.get_jobs()
