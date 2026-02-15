"""
百度贴吧签到模块
"""
import random
from typing import Dict, Any, List, Optional
from dataclasses import dataclass
from loguru import logger

from signin.core import SyncHttpClient, random_sleep, AuthenticationError
from signin.core.config import BaiduAccount
from signin.core.message import MessageService
from signin.scripts.base import BaseTask, register_task, TaskStatus


@dataclass
class Forum:
    """贴吧信息"""
    id: str
    name: str
    level: int = 0


@register_task
class BaiduTiebaTask(BaseTask):
    """
    百度贴吧签到任务
    """
    
    task_name = "baidu_tieba"
    platform = "baidu"
    
    BASE_URL = "https://tieba.baidu.com"
    
    def __init__(
        self,
        account: BaiduAccount,
        message_service: Optional[MessageService] = None
    ):
        super().__init__(account.name)
        self.account = account
        self.message_service = message_service
        self.http = SyncHttpClient()
        self.headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Connection": "keep-alive",
        }
        self.cookies = {"BDUSS": account.bduss}
    
    def check_authentication(self) -> bool:
        """检查登录状态"""
        tbs = self._get_tbs()
        return tbs is not None
    
    def execute(self) -> Dict[str, Any]:
        """执行签到任务"""
        tbs = self._get_tbs()
        if not tbs:
            raise AuthenticationError("获取tbs失败", platform=self.platform)
        
        forums = self._get_forums()
        if not forums:
            logger.warning(f"账号 {self.account_name} 没有关注的贴吧")
            return {"signed": 0, "total": 0, "forums": []}
        
        logger.info(f"账号 {self.account_name} 共关注 {len(forums)} 个贴吧")
        
        success_count = 0
        failed_forums = []
        results = []
        
        for forum in forums:
            random_sleep(100, 500)
            result = self._sign_forum(forum, tbs)
            results.append({
                "name": forum.name,
                "success": result.get("no") == 0 or result.get("no") == 1101
            })
            
            if result.get('no') == 0:
                success_count += 1
                logger.info(f"签到成功: {forum.name}")
            elif result.get('no') == 1101:
                logger.debug(f"已签到: {forum.name}")
            else:
                failed_forums.append(forum)
        
        retry_count = 0
        while failed_forums and retry_count < 3:
            retry_count += 1
            logger.info(f"开始第 {retry_count} 次重签，共 {len(failed_forums)} 个贴吧")
            
            tbs = self._get_tbs()
            retry_failed = []
            
            for forum in failed_forums:
                random_sleep(500, 2000)
                result = self._sign_forum(forum, tbs)
                
                if result.get('no') == 0:
                    success_count += 1
                    logger.info(f"重签成功: {forum.name}")
                elif result.get('no') == 1101:
                    pass
                else:
                    retry_failed.append(forum)
            
            failed_forums = retry_failed
        
        return {
            "signed": success_count,
            "total": len(forums),
            "retries": retry_count,
            "results": results
        }
    
    def on_error(self, error: Exception) -> None:
        """错误处理"""
        super().on_error(error)
        if isinstance(error, AuthenticationError) and self.message_service:
            self.message_service.notify_cookie_expired("百度贴吧", self.account_name)
    
    def _get_tbs(self) -> Optional[str]:
        """获取贴吧tbs参数"""
        url = "https://tieba.baidu.com/dc/common/tbs"
        try:
            response = self.http.get(url, headers=self.headers, cookies=self.cookies)
            data = response.json()
            if data.get('is_login') == 1:
                return data.get('tbs')
        except Exception as e:
            logger.error(f"获取tbs失败: {e}")
        return None
    
    def _get_forums(self) -> List[Forum]:
        """获取关注的贴吧列表"""
        forums = []
        page = 1
        
        while True:
            url = f"{self.BASE_URL}/mg/o/getForumHome"
            params = {"st": 0, "pn": page, "rn": 100}
            
            try:
                response = self.http.get(url, params=params, headers=self.headers, cookies=self.cookies)
                data = response.json()
                
                if data.get('no') != 0:
                    break
                
                forum_list = data.get('data', {}).get('like_forum', [])
                if not forum_list:
                    break
                
                for forum in forum_list:
                    forums.append(Forum(
                        id=str(forum.get('forum_id', '')),
                        name=forum.get('forum_name', ''),
                        level=forum.get('level', 0)
                    ))
                
                if len(forum_list) < 100:
                    break
                    
                page += 1
            except Exception as e:
                logger.error(f"获取贴吧列表异常: {e}")
                break
        
        return forums
    
    def _sign_forum(self, forum: Forum, tbs: str) -> Dict[str, Any]:
        """签到单个贴吧"""
        url = f"{self.BASE_URL}/sign/add"
        data = {"kw": forum.name, "tbs": tbs}
        
        headers = self.headers.copy()
        headers["Content-Type"] = "application/x-www-form-urlencoded"
        
        try:
            response = self.http.post(url, data=data, headers=headers, cookies=self.cookies)
            return response.json()
        except Exception as e:
            logger.error(f"签到贴吧 {forum.name} 异常: {e}")
            return {"no": -1, "error": str(e)}
