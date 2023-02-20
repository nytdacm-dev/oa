#!/bin/bash -e
user=$DEPLOY_USER
host=$DEPLOY_HOST

time=$(date +%Y%m%d-%H%M%S)
current_dir=$(dirname $0)
jar_file_path=$current_dir/../oa-app/build/libs/oa-*.jar
logback_config_path=$current_dir/../config/logback.xml
deploy_dir=/home/$user/deploys/$time
jar_file_name=$(basename $jar_file_path)

function title {
  echo
  echo "###############################################################################"
  echo "## $1"
  echo "###############################################################################"
  echo
}

title '创建远程部署目录'
ssh $user@$host "mkdir -p $deploy_dir"
title '上传jar包'
scp $jar_file_path $user@$host:$deploy_dir
title '上传Dockerfile'
scp $current_dir/host.Dockerfile $user@$host:$deploy_dir/Dockerfile
title '上传日志文件'
scp $logback_config_path $user@$host:$deploy_dir/logback.xml
title '上传远程脚本'
scp $current_dir/setup.sh $user@$host:$deploy_dir
title '执行远程脚本'
ssh $user@$host "export JWT_SECRET_KEY=$JWT_SECRET_KEY; export user=$DEPLOY_USER; export version=$time; /bin/bash -e $deploy_dir/setup.sh $jar_file_name"
