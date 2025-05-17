FROM python:3.12.10-alpine AS build
LABEL authors="cesar"

WORKDIR /api

RUN addgroup -S nonroot && adduser -S nonroot -G nonroot

USER nonroot
COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY ./api.py ./config.py ./
COPY ./data ./data
COPY ./src ./src

CMD ["python", "api.py"]