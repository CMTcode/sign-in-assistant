#!/bin/bash

# 项目名
name="signin"
# 打包镜像名
img_name="cmt/signin"
# 配置文件映射路径
config_path="/opt/docker/signin/config/"
# 日志文件映射路径
log_path="/opt/docker/signin/logs/"

echo "----------开始构建项目----------"

if [[ -n $(docker ps -q -f "name=^${name}$") ]];then
  echo "----------存在历史项目----------"
  echo "----------开始删除----------"
	docker stop ${name}
  docker rm ${name}
  docker rmi ${img_name}
  echo "----------删除结束----------"
fi

docker build -t $img_name .

echo "----------开始启动项目----------"

docker run --name $name -d -v ${config_path}:/config -v ${log_path}:/logs ${img_name}
docker ps
docker logs --tail=100 ${name}

echo "----------启动项目完成----------"