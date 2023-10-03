#!/bin/bash -e

container_name=oa-prod
user=$(whoami)
filename=nytdacm_oa_files_`date +%Y-%m-%d"_"%H_%M_%S`.tar.gz
backup_dir=/home/$user/backups/

mkdir -p $backup_dir

docker run --rm --volumes-from $container_name -v $backup_dir:/backup ubuntu:22.04 tar cvf /backup/$filename /data
