"""
阿里云网盘签到模块
"""
from typing import Dict, Any, Optional
from loguru import logger

from signin.core import SyncHttpClient, AuthenticationError
from signin.core.config import AliyundriveAccount
from signin.core.message import MessageService
from signin.scripts.base import BaseTask, register_task


@register_task
class AliyundriveTask(BaseTask):
    """
    阿里云网盘签到任务
    """
    
    task_name = "aliyundrive"
    platform = "aliyundrive"
    
    BASE_URL = "https://api.alipan.com"
    
    def __init__(
        self,
        account: AliyundriveAccount,
        message_service: Optional[MessageService] = None
    ):
        super().__init__(account.name)
        self.account = account
        self.message_service = message_service
        self.http = SyncHttpClient()
        self.access_token: Optional[str] = None
        self.headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
        }
    
    def check_authentication(self) -> bool:
        """检查登录状态"""
        if not self._refresh_access_token():
            return False
        user_info = self._get_user_info()
        return 'user_id' in user_info
    
    def execute(self) -> Dict[str, Any]:
        """执行签到任务"""
        return self._task_sign_in()
    
    def on_error(self, error: Exception) -> None:
        """错误处理"""
        super().on_error(error)
        if isinstance(error, AuthenticationError) and self.message_service:
            self.message_service.notify_cookie_expired("阿里云网盘", self.account_name)
    
    def _refresh_access_token(self) -> bool:
        """刷新访问令牌"""
        url = f"{self.BASE_URL}/auth/token"
        data = {
            "grant_type": "refresh_token",
            "refresh_token": self.account.refreshToken,
        }
        
        try:
            response = self.http.post(url, json_data=data, headers=self.headers)
            result = response.json()
            
            if result.get('status') == 'enabled':
                self.access_token = result.get('access_token')
                self.headers["Authorization"] = f"Bearer {self.access_token}"
                return True
            else:
                logger.error(f"刷新令牌失败: {result.get('message')}")
        except Exception as e:
            logger.error(f"刷新令牌异常: {e}")
        return False
    
    def _get_user_info(self) -> Dict[str, Any]:
        """获取用户信息"""
        url = f"{self.BASE_URL}/user/get"
        try:
            response = self.http.get(url, headers=self.headers)
            return response.json()
        except Exception as e:
            logger.error(f"获取用户信息失败: {e}")
            return {}
    
    def _task_sign_in(self) -> Dict[str, Any]:
        """每日签到任务"""
        logger.info("执行每日签到...")
        
        url = f"{self.BASE_URL}/activity/sign_in"
        data = {"isReward": True}
        
        try:
            response = self.http.post(url, json_data=data, headers=self.headers)
            result = response.json()
            
            if result.get('success'):
                sign_result = result.get('result', {})
                sign_count = sign_result.get('signInCount', 0)
                
                sign_logs = sign_result.get('signInLogs', [])
                reward = None
                for log in reversed(sign_logs):
                    if log.get('status') == 'NORMAL':
                        reward = log.get('reward', {})
                        break
                
                reward_info = ""
                if reward:
                    reward_info = f"{reward.get('name')} - {reward.get('description')}"
                    logger.info(f"签到成功，获得奖励: {reward_info}")
                
                logger.info(f"已连续签到 {sign_count} 天")
                
                return {
                    "success": True,
                    "sign_count": sign_count,
                    "reward": reward_info
                }
            else:
                logger.info("今日已签到")
                return {"success": True, "already_signed": True}
                
        except Exception as e:
            logger.error(f"签到失败: {e}")
            return {"success": False, "error": str(e)}
