"""
哔哩哔哩签到模块
"""
import random
from typing import Dict, Any, Optional
from dataclasses import dataclass
from datetime import datetime
from loguru import logger

from signin.core import SyncHttpClient, random_sleep
from signin.core.config import BilibiliAccount
from signin.core.message import MessageService
from signin.scripts.base import BaseTask, register_task, CompositeTask


@dataclass
class UserInfo:
    """用户信息"""
    mid: int = 0
    name: str = ""
    level: int = 0
    coins: float = 0.0
    vip_type: int = 0
    vip_status: int = 0


@register_task
class BilibiliTask(CompositeTask):
    """
    哔哩哔哩签到任务
    包含多个子任务的复合任务
    """
    
    task_name = "bilibili"
    platform = "bilibili"
    
    def __init__(
        self,
        account: BilibiliAccount,
        message_service: Optional[MessageService] = None
    ):
        super().__init__(account.name)
        self.account = account
        self.message_service = message_service
        self.http = SyncHttpClient()
        self.headers = {
            "User-Agent": account.userAgent or "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
            "Referer": "https://www.bilibili.com/",
            "Origin": "https://www.bilibili.com",
        }
        self.cookies = {
            "DEDEUSERID": account.DEDEUSERID,
            "SESSDATA": account.SESSDATA,
            "BILI_JCT": account.BILI_JCT,
        }
        self.user_info: Optional[UserInfo] = None
        
        self._setup_sub_tasks()
    
    def _setup_sub_tasks(self):
        """设置子任务"""
        self.add_sub_task(BilibiliVideoWatchTask(self))
        self.add_sub_task(BilibiliCoinAddTask(self))
        self.add_sub_task(BilibiliMangaSignTask(self))
        self.add_sub_task(BilibiliMangaReadTask(self))
        self.add_sub_task(BilibiliLiveCheckinTask(self))
        self.add_sub_task(BilibiliGiveGiftTask(self))
        self.add_sub_task(BilibiliVipPrivilegeTask(self))
    
    def check_authentication(self) -> bool:
        """检查登录状态"""
        user_info = self._get_user_info()
        return user_info is not None and user_info.mid > 0
    
    def execute(self) -> Dict[str, Any]:
        """执行签到任务"""
        self.user_info = self._get_user_info()
        if self.user_info:
            logger.info(f"用户: {self.user_info.name}, 硬币: {self.user_info.coins}")
        
        return super().execute()
    
    def on_error(self, error: Exception) -> None:
        """错误处理"""
        super().on_error(error)
        from signin.core.exceptions import AuthenticationError
        if isinstance(error, AuthenticationError) and self.message_service:
            self.message_service.notify_cookie_expired("哔哩哔哩", self.account_name)
    
    def _get_user_info(self) -> Optional[UserInfo]:
        """获取用户信息"""
        url = "https://api.bilibili.com/x/space/acc/info"
        try:
            response = self.http.get(url, headers=self.headers, cookies=self.cookies)
            data = response.json()
            
            if data.get('code') == 0:
                info = data.get('data', {})
                return UserInfo(
                    mid=info.get('mid', 0),
                    name=info.get('name', ''),
                    level=info.get('level', 0),
                    coins=self._get_coin_balance(),
                    vip_type=info.get('vip', {}).get('type', 0),
                    vip_status=info.get('vip', {}).get('status', 0),
                )
        except Exception as e:
            logger.error(f"获取用户信息失败: {e}")
        return None
    
    def _get_coin_balance(self) -> float:
        """获取硬币余额"""
        url = "https://account.bilibili.com/api/getuserinfo"
        try:
            response = self.http.get(url, headers=self.headers, cookies=self.cookies)
            data = response.json()
            if data.get('code') == 0:
                return float(data.get('data', {}).get('money', 0))
        except Exception:
            pass
        return 0.0
    
    def _get_daily_task_status(self) -> Dict[str, Any]:
        """获取每日任务状态"""
        url = "https://api.bilibili.com/x/member/web/exp/reward"
        try:
            response = self.http.get(url, headers=self.headers, cookies=self.cookies)
            data = response.json()
            if data.get('code') == 0:
                return data.get('data', {})
        except Exception as e:
            logger.error(f"获取任务状态失败: {e}")
        return {}
    
    def _get_region_videos(self, rid: int = 1, num: int = 50) -> list:
        """获取分区视频列表"""
        url = f"https://api.bilibili.com/x/web-interface/dynamic/region?ps={num}&rid={rid}"
        try:
            response = self.http.get(url, headers=self.headers)
            data = response.json()
            if data.get('code') == 0:
                archives = data.get('data', {}).get('archives', [])
                return [a.get('bvid', '') for a in archives if a.get('bvid')]
        except Exception as e:
            logger.error(f"获取分区视频失败: {e}")
        return []


class BilibiliSubTask(BaseTask):
    """B站子任务基类"""
    
    platform = "bilibili"
    
    def __init__(self, parent: BilibiliTask):
        super().__init__(parent.account_name)
        self.parent = parent
        self.http = parent.http
        self.headers = parent.headers
        self.cookies = parent.cookies
        self.account = parent.account


class BilibiliVideoWatchTask(BilibiliSubTask):
    """观看分享视频任务"""
    
    task_name = "bilibili_video_watch"
    
    def check_authentication(self) -> bool:
        return True
    
    def execute(self) -> Dict[str, Any]:
        task_status = self.parent._get_daily_task_status()
        results = {"watch": False, "share": False}
        
        if not task_status.get('watch', False):
            videos = self.parent._get_region_videos()
            if videos:
                bvid = random.choice(videos)
                if self._watch_video(bvid):
                    results["watch"] = True
                    logger.info("观看视频成功")
        else:
            results["watch"] = True
            logger.info("今日观看视频任务已完成")
        
        if not task_status.get('share', False):
            videos = self.parent._get_region_videos()
            if videos:
                bvid = random.choice(videos)
                if self._share_video(bvid):
                    results["share"] = True
                    logger.info("分享视频成功")
        else:
            results["share"] = True
            logger.info("今日分享视频任务已完成")
        
        return results
    
    def _watch_video(self, bvid: str) -> bool:
        url = "https://api.bilibili.com/x/click-interface/web/heartbeat"
        data = {"bvid": bvid, "played_time": random.randint(1, 90)}
        try:
            response = self.http.post(url, data=data, headers=self.headers, cookies=self.cookies)
            return response.json().get('code') == 0
        except Exception:
            return False
    
    def _share_video(self, bvid: str) -> bool:
        url = "https://api.bilibili.com/x/web-interface/share/add"
        data = {"bvid": bvid, "csrf": self.account.BILI_JCT}
        try:
            response = self.http.post(url, data=data, headers=self.headers, cookies=self.cookies)
            return response.json().get('code') == 0
        except Exception:
            return False


class BilibiliCoinAddTask(BilibiliSubTask):
    """投币任务"""
    
    task_name = "bilibili_coin_add"
    
    def check_authentication(self) -> bool:
        return True
    
    def execute(self) -> Dict[str, Any]:
        task_status = self.parent._get_daily_task_status()
        coins_today = task_status.get('coins', 0)
        
        need_coins = 5 - coins_today
        if need_coins <= 0:
            logger.info("今日投币任务已完成")
            return {"coins_added": 0, "message": "已完成"}
        
        balance = self.parent.user_info.coins if self.parent.user_info else 0
        reserve = self.account.reserveCoins
        
        if balance <= reserve:
            logger.info(f"硬币余额 {balance} 低于预留 {reserve}，跳过投币")
            return {"coins_added": 0, "message": "余额不足"}
        
        available = int(balance - reserve)
        need_coins = min(need_coins, available)
        
        logger.info(f"需要投币 {need_coins} 枚")
        
        videos = self.parent._get_region_videos(num=50)
        if not videos:
            return {"coins_added": 0, "message": "未获取到视频"}
        
        success_count = 0
        for bvid in videos:
            if success_count >= need_coins:
                break
            
            if self._check_coin_status(bvid) > 0:
                continue
            
            random_sleep(500, 2000)
            if self._add_coin(bvid):
                success_count += 1
                logger.info(f"投币成功 ({success_count}/{need_coins})")
        
        return {"coins_added": success_count, "target": need_coins}
    
    def _check_coin_status(self, bvid: str) -> int:
        url = f"https://api.bilibili.com/x/web-interface/coin/info?bvid={bvid}"
        try:
            response = self.http.get(url, headers=self.headers, cookies=self.cookies)
            data = response.json()
            if data.get('code') == 0:
                return data.get('data', {}).get('multiply', 0)
        except Exception:
            pass
        return 0
    
    def _add_coin(self, bvid: str) -> bool:
        url = "https://api.bilibili.com/x/web-interface/coin/add"
        data = {
            "bvid": bvid,
            "multiply": 1,
            "select_like": 1,
            "cross_domain": "true",
            "csrf": self.account.BILI_JCT,
        }
        try:
            response = self.http.post(url, data=data, headers=self.headers, cookies=self.cookies)
            return response.json().get('code') == 0
        except Exception:
            return False


class BilibiliMangaSignTask(BilibiliSubTask):
    """漫画签到"""
    
    task_name = "bilibili_manga_sign"
    
    def check_authentication(self) -> bool:
        return True
    
    def execute(self) -> Dict[str, Any]:
        url = "https://manga.bilibili.com/twirp/activity.v1.Activity/ClockIn"
        data = {"platform": "android"}
        try:
            response = self.http.post(url, json_data=data, headers=self.headers, cookies=self.cookies)
            success = response.json().get('code') == 0
            if success:
                logger.info("漫画签到成功")
            return {"success": success}
        except Exception as e:
            return {"success": False, "error": str(e)}


class BilibiliMangaReadTask(BilibiliSubTask):
    """漫画阅读"""
    
    task_name = "bilibili_manga_read"
    
    def check_authentication(self) -> bool:
        return True
    
    def execute(self) -> Dict[str, Any]:
        url = "https://manga.bilibili.com/twirp/activity.v1.Activity/ReadComics"
        data = {"device": "pc", "platform": "web", "comic_id": 27355, "ep_id": 381662}
        try:
            response = self.http.post(url, json_data=data, headers=self.headers, cookies=self.cookies)
            success = response.json().get('code') == 0
            if success:
                logger.info("漫画阅读成功")
            return {"success": success}
        except Exception as e:
            return {"success": False, "error": str(e)}


class BilibiliLiveCheckinTask(BilibiliSubTask):
    """直播签到"""
    
    task_name = "bilibili_live_checkin"
    
    def check_authentication(self) -> bool:
        return True
    
    def execute(self) -> Dict[str, Any]:
        url = "https://api.live.bilibili.com/xlive/web-ucenter/v1/sign/DoSign"
        try:
            response = self.http.get(url, headers=self.headers, cookies=self.cookies)
            result = response.json()
            if result.get('code') == 0:
                data = result.get('data', {})
                logger.info(f"直播签到成功: {data.get('text')}")
                return {"success": True, "reward": data.get('text')}
            return {"success": False, "message": result.get('message')}
        except Exception as e:
            return {"success": False, "error": str(e)}


class BilibiliGiveGiftTask(BilibiliSubTask):
    """送出即将过期的礼物"""
    
    task_name = "bilibili_give_gift"
    
    def check_authentication(self) -> bool:
        return True
    
    def execute(self) -> Dict[str, Any]:
        gifts = self._get_gift_list()
        if not gifts:
            logger.info("背包没有礼物")
            return {"gifts_sent": 0}
        
        now_time = int(datetime.now().timestamp())
        sent = 0
        
        for gift in gifts:
            expire_at = gift.get('expire_at', 0)
            if expire_at == 0 or (expire_at - now_time) >= 60 * 60 * 25:
                continue
            
            room_id, uid = self._get_live_room()
            if room_id and uid:
                if self._send_gift(room_id, uid, gift):
                    sent += 1
                    logger.info(f"送出礼物: {gift.get('gift_name')} x {gift.get('gift_num')}")
        
        return {"gifts_sent": sent}
    
    def _get_gift_list(self) -> list:
        url = "https://api.live.bilibili.com/xlive/web-room/v1/gift/bag_list"
        try:
            response = self.http.get(url, headers=self.headers, cookies=self.cookies)
            data = response.json()
            if data.get('code') == 0:
                return data.get('data', {}).get('list', [])
        except Exception:
            pass
        return []
    
    def _get_live_room(self) -> tuple:
        if self.account.upLive != "0":
            url = f"http://api.live.bilibili.com/room/v1/Room/getRoomInfoOld?mid={self.account.upLive}"
            try:
                response = self.http.get(url, headers=self.headers)
                data = response.json()
                if data.get('code') == 0:
                    room_id = data.get('data', {}).get('roomid', '0')
                    if room_id != '0':
                        return str(room_id), self.account.upLive
            except Exception:
                pass
        
        url = "https://api.live.bilibili.com/relation/v1/AppWeb/getRecommendList"
        try:
            response = self.http.get(url, headers=self.headers)
            data = response.json()
            if data.get('code') == 0:
                room_id = data.get('data', {}).get('list', [])[6].get('roomid', '')
                room_url = f"https://api.live.bilibili.com/xlive/web-room/v1/index/getInfoByRoom?room_id={room_id}"
                room_resp = self.http.get(room_url, headers=self.headers)
                room_data = room_resp.json()
                uid = room_data.get('data', {}).get('room_info', {}).get('uid', '')
                return str(room_id), str(uid)
        except Exception:
            pass
        return "", ""
    
    def _send_gift(self, room_id: str, uid: str, gift: dict) -> bool:
        url = "https://api.live.bilibili.com/gift/v2/live/bag_send"
        data = {
            "uid": self.account.DEDEUSERID,
            "biz_id": room_id,
            "ruid": uid,
            "bag_id": gift.get('bag_id', 0),
            "gift_id": gift.get('gift_id', 0),
            "gift_num": gift.get('gift_num', 0),
            "csrf": self.account.BILI_JCT,
        }
        try:
            response = self.http.post(url, data=data, headers=self.headers, cookies=self.cookies)
            return response.json().get('code') == 0
        except Exception:
            return False


class BilibiliVipPrivilegeTask(BilibiliSubTask):
    """领取大会员权益"""
    
    task_name = "bilibili_vip_privilege"
    
    def check_authentication(self) -> bool:
        return True
    
    def execute(self) -> Dict[str, Any]:
        if not self.parent.user_info or self.parent.user_info.vip_type == 0:
            logger.info("非大会员，跳过领取权益")
            return {"success": False, "reason": "非大会员"}
        
        day = datetime.now().day
        results = {}
        
        if day == 1:
            url = "https://manga.bilibili.com/twirp/user.v1.User/GetVipReward"
            data = {"reason_id": 1}
            try:
                response = self.http.post(url, json_data=data, headers=self.headers, cookies=self.cookies)
                result = response.json()
                if result.get('code') == 0:
                    amount = result.get('data', {}).get('amount', 0)
                    logger.info(f"领取漫读券成功: {amount}张")
                    results["manga_coupon"] = amount
            except Exception as e:
                results["error"] = str(e)
        
        return results
