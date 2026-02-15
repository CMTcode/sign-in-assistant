FROM python:3.11-slim

ENV TZ=Asia/Shanghai
ENV PYTHONUNBUFFERED=1

WORKDIR /app

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple

COPY config/ /app/config/
COPY signin/ /app/signin/
COPY main.py /app/

LABEL authors="ForkManTou"

ENTRYPOINT ["python", "main.py"]
