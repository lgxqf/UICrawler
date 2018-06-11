#!/bin/bash

COUNT=$1

if [ ! $COUNT ]; then
	COUNT=2
	echo "appium instance count is $COUNT"
fi

APP_PORT=4721
BP_PORT=4724

for((i=0;i<$COUNT;i++))
do
	APP_PORT=$[APP_PORT + 2]
	BP_PORT=$[APP_PORT + 1]
	echo "start appium at port $APP_PORT, BP_PORT $BP_PORT"
	appium --session-override  -p  $APP_PORT -bp $BP_PORT &
done


