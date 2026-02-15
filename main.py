"""
签到助手主程序入口
Version: 2.0.0
"""
import sys
import signal
import argparse
from pathlib import Path
from datetime import datetime
from loguru import logger

from signin import __version__, __author__
from signin.core import load_config, setup_logger
from signin.scheduler import Scheduler


def print_banner():
    """
    打印启动Banner
    """
    banner = f"""
    ╔═══════════════════════════════════════════╗
    ║                                           ║
    ║         签 到 助 手 v{__version__}              ║
    ║                                           ║
    ║   支持平台: 百度贴吧 | 哔哩哔哩 |         ║
    ║            网易云音乐 | 阿里云网盘        ║
    ║                                           ║
    ╚═══════════════════════════════════════════╝
    """
    print(banner)


def main():
    """
    主函数
    """
    parser = argparse.ArgumentParser(
        description='签到助手 - 多平台自动签到工具',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
示例:
  python main.py                    # 启动定时任务模式
  python main.py --once             # 只执行一次后退出
  python main.py -c /path/to/config # 指定配置目录
  python main.py --version          # 显示版本信息
        """
    )
    parser.add_argument('-c', '--config', type=str, default='config', help='配置文件目录')
    parser.add_argument('-l', '--log', type=str, default='logs', help='日志文件目录')
    parser.add_argument('--once', action='store_true', help='只执行一次后退出')
    parser.add_argument('--version', '-v', action='store_true', help='显示版本信息')
    parser.add_argument('--debug', action='store_true', help='启用调试模式')
    
    args = parser.parse_args()
    
    if args.version:
        print(f"签到助手 v{__version__}")
        print(f"作者: {__author__}")
        return
    
    config_dir = Path(args.config)
    if not config_dir.exists():
        logger.error(f"配置目录不存在: {config_dir}")
        sys.exit(1)
    
    log_level = "DEBUG" if args.debug else "INFO"
    setup_logger(log_dir=args.log, level=log_level)
    print_banner()
    
    config = load_config(str(config_dir))
    
    if args.once:
        scheduler = Scheduler(config)
        report = scheduler.run_once()
        
        print("\n" + "=" * 50)
        print("执行报告:")
        print(f"  总任务数: {report.total_tasks}")
        print(f"  成功: {report.success_count}")
        print(f"  失败: {report.failed_count}")
        print(f"  耗时: {report.duration_seconds:.2f}秒")
        print("=" * 50)
        return
    
    scheduler = Scheduler(config)
    
    def signal_handler(sig, frame):
        logger.info("收到退出信号，正在停止...")
        scheduler.stop()
        sys.exit(0)
    
    signal.signal(signal.SIGINT, signal_handler)
    signal.signal(signal.SIGTERM, signal_handler)
    
    logger.info("启动时执行一次签到任务...")
    scheduler.run_once()
    
    scheduler.start()
    
    next_run = scheduler.get_next_run_time()
    if next_run:
        logger.info(f"下次执行时间: {next_run.strftime('%Y-%m-%d %H:%M:%S')}")
    
    logger.info("签到助手已启动，按 Ctrl+C 退出")
    
    try:
        import time
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        scheduler.stop()


if __name__ == '__main__':
    main()
