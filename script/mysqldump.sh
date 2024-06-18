#!/bin/bash
#数据库ip  需修改
BaseIP=192.168.11.177
#远程数据库端口
BsaePort=3306
#数据库用户名   需修改
BaseUsername=root
#数据库密码    需修改
BasePassword=root
#备份时间标识
BackTime=`date +%Y-%m-%d_%H-%M-%S`
#备份的文件本地目录   需修改
Back_file=/home/mysqlbak
#获取mysql容器id
mysqlId=$(docker ps | grep mysql | awk '{print $1}')
#备份目录
if  [ ! -d $Back_file ]; 
then
   mkdir -p $Back_file  #创建文件
fi

#获取全部数据库
DataBase=$(docker exec  $mysqlId  mysql --socket=/var/lib/mysql/mysql.sock  -u $BaseUsername -p$BasePassword  -e "SHOW DATABASES;" | grep -v Database) 
echo $DataBase
#循环备份   
for  db  in ${DataBase[*]}
do    #自带数据库不参与备份
   if [ ${db}  != "information_schema" ]  &&  [ ${db}  != "mysql" ]  && [ ${db}  != "performance_schema" ]  &&  [ ${db} !=  "sys" ] && [ ${db} !=  "master" ];then
    Back_Prefix=${db}_$BackTime.sql
    #直接拿到容器内部数据库备份到本地  不需要先备份到容器内部在cp到本地
    docker exec  $mysqlId  /usr/bin/mysqldump --socket=/var/lib/mysql/mysql.sock -u $BaseUsername -p$BasePassword  ${db} > ${Back_file}/$Back_Prefix
    fi
done
cd  $Back_file
tar -zcvf  $Back_file/$BackTime.tar.gz  ./*.sql 
rm -rf $Back_file/*.sql
#保留七天内的备份
find $Back_file -name "*.tar.gz" -type f -mtime +7 -exec rm -rf {} \;