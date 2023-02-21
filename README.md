## 数据库启动脚本

```shell
docker run -d \
   --name db-for-nytdacm_oa \
   -e POSTGRES_USER=nytdacm_oa \
   -e POSTGRES_PASSWORD=123456 \
   -e POSTGRES_DB=nytdacm_oa_dev \
   -e PGDATA=/var/lib/postgresql/data/pgdata \
   -e TZ=Asia/Shanghai \
   -e PGTZ=Asia/Shanghai \
   -e LANG=en_US.UTF-8 \
   -p 5432:5432 \
   postgres:15
```
