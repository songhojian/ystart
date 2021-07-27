FROM mysql:8.0.23

MAINTAINER ystart(wangiegie@gmail.com)

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY ./1schema.sql /docker-entrypoint-initdb.d

COPY ./2ystartx.sql /docker-entrypoint-initdb.d

COPY ./3ystartx_ac.sql /docker-entrypoint-initdb.d

COPY ./4ystartx_job.sql /docker-entrypoint-initdb.d

COPY ./5ystartx_mp.sql /docker-entrypoint-initdb.d

COPY ./6ystartx_config.sql /docker-entrypoint-initdb.d

COPY ./7ystartx_pay.sql /docker-entrypoint-initdb.d

COPY ./8ystartx_codegen.sql /docker-entrypoint-initdb.d