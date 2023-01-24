## 数据库启动脚本

```shell
 docker run -d \
    --name db-for-nytdacm_oa \
    -e POSTGRES_USER=nytdacm_oa \
    -e POSTGRES_PASSWORD=123456 \
    -e POSTGRES_DB=nytdacm_oa_dev \
    -e PGDATA=/var/lib/postgresql/data/pgdata \
    -v nytdacm_oa-data:/var/lib/postgresql/data \
    -p 5432:5432 \
    postgres:14
```
