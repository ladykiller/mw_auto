#!/bin/sh
echo "counter started"
for i in `seq -w 30 -1 1`
do
    echo  "$i";
    sleep 1;
done

echo "counter end"
echo "MW_SUCCESS"