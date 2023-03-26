# 南京邮电大学通达学院程序设计校队官网

## 如何运行

请提前安装好 [Node.js](https://nodejs.org) 18 或以上版本，并且安装
[pnpm](https://pnpm.io) 包管理工具。可以使用 Node.js 自带的 corepack 进行管理。

```shell
$ git submodule update --init -- web  # 拉取前端代码
```

前端命令：

```shell
$ cd web
$ pnpm i            # 安装前端依赖
$ pnpm run dev      # 启动开发环境
$ pnpm run build    # 构建前端代码
```

后端命令：

```shell
$ ./gradlew oa-app:bootRun   # 运行项目
$ ./gradlew oa-app:bootJar   # 构建 fat jar 以供部署
```

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
