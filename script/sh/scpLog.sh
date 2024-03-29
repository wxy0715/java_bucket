#!/usr/bin/expect
set timeout 10
set username [lindex $argv 0]
set password [lindex $argv 1]
set ip [lindex $argv 2]
set port [lindex $argv 3]
set sourcePath [lindex $argv 4]
spawn scp -P$port $sourcePath  $username@$ip:$sourcePath
expect {
"Are you sure you want to continue connecting (yes/no)?" { send "yes\n"; exp_continue }
"$username@$ip's password:" { send "$password\n" }
}
expect eof
