"""
网易云音乐签到模块
"""
import random
from typing import Dict, Any, Optional, List
from dataclasses import dataclass
from loguru import logger

from signin.core import SyncHttpClient, random_sleep, parse_cookie_string, AuthenticationError
from signin.core.config import WangyiAccount
from signin.core.message import MessageService
from signin.scripts.base import BaseTask, register_task


@register_task
class WangyiMusicTask(BaseTask):
    """
    网易云音乐签到任务
    """
    
    task_name = "wangyi_music"
    platform = "wangyi"
    
    BASE_URL = "https://music.163.com"
    
    def __init__(
        self,
        account: WangyiAccount,
        message_service: Optional[MessageService] = None
    ):
        super().__init__(account.name)
        self.account = account
        self.message_service = message_service
        self.http = SyncHttpClient()
        self.cookies = parse_cookie_string(account.cookie)
        self.headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Referer": "https://music.163.com/",
            "Origin": "https://music.163.com",
        }
    
    def check_authentication(self) -> bool:
        """检查登录状态"""
        if 'MUSIC_U' not in self.cookies:
            return False
        level_info = self._get_user_level()
        return level_info.get('code') == 200
    
    def execute(self) -> Dict[str, Any]:
        """执行签到任务"""
        results = {}
        
        results['daily_signin'] = self._task_daily_signin()
        results['yunbei_sign'] = self._task_yunbei_sign()
        results['listen_songs'] = self._task_listen_songs()
        
        return results
    
    def on_error(self, error: Exception) -> None:
        """错误处理"""
        super().on_error(error)
        if isinstance(error, AuthenticationError) and self.message_service:
            self.message_service.notify_cookie_expired("网易云音乐", self.account_name)
    
    def _get_user_level(self) -> Dict[str, Any]:
        """获取用户等级信息"""
        url = "https://music.163.com/weapi/user/level"
        try:
            response = self.http.post(url, json_data={}, headers=self.headers, cookies=self.cookies)
            return response.json()
        except Exception as e:
            logger.error(f"获取用户等级失败: {e}")
            return {}
    
    def _task_daily_signin(self) -> Dict[str, Any]:
        """每日签到任务"""
        logger.info("执行每日签到...")
        
        url = "https://music.163.com/weapi/point/dailyTask"
        data = {"type": 0}
        
        try:
            response = self.http.post(url, json_data=data, headers=self.headers, cookies=self.cookies)
            result = response.json()
            if result.get('code') == 200:
                logger.info(f"签到成功，获得 {result.get('point', 0)} 点经验")
                return {"success": True, "point": result.get('point', 0)}
            return {"success": False, "message": result.get('msg', '已签到')}
        except Exception as e:
            return {"success": False, "error": str(e)}
    
    def _task_yunbei_sign(self) -> Dict[str, Any]:
        """云贝签到任务"""
        logger.info("执行云贝签到...")
        
        url = "https://music.163.com/weapi/point/dailyTask"
        data = {"type": 0, "withCredentials": True}
        
        try:
            response = self.http.post(url, json_data=data, headers=self.headers, cookies=self.cookies)
            result = response.json()
            if result.get('code') == 200:
                logger.info(f"云贝签到成功，获得 {result.get('point', 0)} 个云贝")
                return {"success": True, "point": result.get('point', 0)}
            return {"success": False, "message": result.get('msg', '已签到')}
        except Exception as e:
            return {"success": False, "error": str(e)}
    
    def _task_listen_songs(self) -> Dict[str, Any]:
        """每日300首听歌任务"""
        logger.info("执行每日300首听歌任务...")
        
        level_info = self._get_user_level()
        level = level_info.get('data', {}).get('level', 0)
        
        if level >= 10:
            logger.info(f"用户等级已达 {level} 级，跳过刷歌任务")
            return {"success": False, "reason": "等级已达上限"}
        
        now_play_count = int(level_info.get('data', {}).get('nowPlayCount', 0))
        
        logger.info(f"当前听歌数: {now_play_count}")
        
        recommend = self._get_recommend_songs()
        songs = recommend.get('data', {}).get('dailySongs', [])
        
        if not songs:
            logger.warning("未获取到推荐歌曲")
            return {"success": False, "reason": "未获取到歌曲"}
        
        success_count = 0
        for song in songs:
            if success_count >= 300:
                break
            
            song_id = song.get('id')
            source_id = song.get('sourceId', '')
            
            random_sleep(300, 800)
            result = self._listen_song(song_id, str(source_id))
            
            if result.get('code') == 200:
                success_count += 1
                if success_count % 50 == 0:
                    logger.info(f"已播放 {success_count} 首")
        
        logger.info(f"听歌任务完成，共播放 {success_count} 首")
        return {"success": True, "played": success_count}
    
    def _get_recommend_songs(self) -> Dict[str, Any]:
        """获取每日推荐歌曲"""
        url = "https://music.163.com/weapi/v1/discovery/recommend/songs"
        try:
            response = self.http.post(url, json_data={}, headers=self.headers, cookies=self.cookies)
            return response.json()
        except Exception as e:
            logger.error(f"获取推荐歌曲失败: {e}")
            return {}
    
    def _listen_song(self, song_id: int, source_id: str = "") -> Dict[str, Any]:
        """听歌打卡"""
        url = "https://music.163.com/weapi/v3/play/song/record"
        data = {
            "songId": song_id,
            "sourceid": source_id,
            "type": "song",
            "time": random.randint(60, 300),
        }
        try:
            response = self.http.post(url, json_data=data, headers=self.headers, cookies=self.cookies)
            return response.json()
        except Exception:
            return {}
