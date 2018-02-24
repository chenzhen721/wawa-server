@echo off

echo ================git pull=======================
@call git pull

sleep 5

@call mvn clean package -U -P test -P groovy -Dmaven.test.skip=true

if %ERRORLEVEL% EQU 0 (
	echo ================SUCCESS=======================
	call:pubApi 120.79.52.5 "publish to 120.79.52.5"
	echo ================SUCCESS=======================
	sleep 15

) else (
	COLOR C
	    echo -------         !! FAILD !!      -------------
	pause
)

exit

:pubApi
scp target/wawa-server.war mlsty@%~1:~/test-wawa-server
ssh mlsty@%~1  "source /etc/profile;cd ~/test-wawa-server;rm -rf bak.webapp && mv -f webapp bak.webapp;unzip wawa-server.war -d webapp;cp ROOT.xml webapp;./restart.sh"
pause
