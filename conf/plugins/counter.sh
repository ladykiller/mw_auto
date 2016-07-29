#!/bin/sh
echo "counter started"
time=30

if [ $# -ne 0 ]; then
   time=$1
fi
for i in `seq -w $time -1 1`
do
    date "+%Y-%m-%d %H:%M:%S"
    echo  "$i";
    sleep 1;
done

echo "counter end"

echo "MW_SUCCESS"