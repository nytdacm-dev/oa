#!/bin/bash

db_container_name=db-for-oa
user=oa
filename=nytdacm_oa_dump_`date +%Y-%m-%d"_"%H_%M_%S`.sql
backup_dir=/home/$user/backups/

mkdir -p $backup_dir

docker exec -t $db_container_name pg_dumpall -c -U nytdacm_oa | gzip > $backup_dir/nytdacm_oa_dump_`date +%Y-%m-%d"_"%H_%M_%S`.gz
