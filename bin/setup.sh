root=/home/$user/deploys/$version
container_name=oa-prod
#nginx_container_name=oa-nginx
db_container_name=db-for-oa
db_password=123456
network=network_oa

function title {
  echo
  echo "###############################################################################"
  echo "## $1"
  echo "###############################################################################"
  echo
}

title '创建数据库'
if [ "$(docker ps -aq -f name=^${db_container_name}$)" ]; then
  echo '已存在数据库'
else
  docker run -d --name $db_container_name \
            --network=$network \
            -e POSTGRES_USER=nytdacm_oa \
            -e POSTGRES_DB=nytdacm_oa_production \
            -e POSTGRES_PASSWORD=$db_password \
            -e PGDATA=/var/lib/postgresql/data/pgdata \
            -v nytdacm_oa-data:/var/lib/postgresql/data \
            postgres:14
  echo '数据库创建成功'
fi

title '构建镜像'
docker build $root -t $container_name:$version

title '删除容器'
if [ "$(docker ps -aq -f name=^$container_name$)" ]; then
  docker rm -f $container_name
fi

title '运行容器'
docker run -d \
    -p 80:8080 \
    --name $container_name \
    --network $network \
    -e PG_HOST=$db_container_name \
    -e PG_USER=nytdacm_oa \
    -e PG_PASSWORD=$db_password \
    -e PG_DB=nytdacm_oa_production \
    -v /home/root/.m2:/mvn \
    $container_name:$version

title '全部执行完毕'
