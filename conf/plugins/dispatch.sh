  #!/bin/sh
  packetdir=''
  dstip=''
  dstdir=''
  backupdir=''
  projectname=''
  usage() {
    echo -e "-p the dir for the packet that is sended \n"
    echo -e "-i the dst host \n"
    echo -e "-d the dst dir on dst host \n"
    echo -e "-b the backupdir dir on dst host \n"
  }

  function check_empty() {
      if [ x$packetdir == "x" ]; then
          echo -e "-p must set"
          usage
          exit 1
      fi
      if [ x$dstip == "x" ]; then
          echo -e "-i must set"
          usage
          exit 1
      fi
      if [ x$backupdir == "x" ]; then
          echo -e "-b must set"
          usage
          exit 1
      fi
      if [ x$dstdir == "x" ]; then
          echo -e "-d must set"
          usage
          exit 1
      fi
  }
  getProName(){
    packetname=`basename $packetdir`
    if [ x$packetname == "x" ]; then
      echo -e "the packetname sended is not get \n"
      exit 1
    fi
    projectname=${packetname%%.*}

  }
  dispath(){
    echo -e "send start"
    scp $packetdir $dstip:$dstdir
    echo -e "send end\n\n"
    echo -e "unpack start"
    ssh -o StrictHostKeyChecking=no $dstip "cd $dstdir && tar -xzvf $packetname && rm -fr $packetname "
    echo -e "unpack end\n\n"
  }
  backup() {
    echo -e "backup start"
    ssh -o StrictHostKeyChecking=no $dstip "ls -lrt  $backupdir 2>&1 >/dev/null" 
    exist=$?
    if [ $exist -ne 0 ]; then
     echo -e "the backup dir is not exist"
     exit 1
   fi
   ssh -o StrictHostKeyChecking=no $dstip "ls -lrt  $dstdir/$projectname 2>&1 >/dev/null" 2>&1 >/dev/null 

	exist=$?
    if [ $exist -eq 0 ]; then
      ssh -o StrictHostKeyChecking=no $dstip  "cd $dstdir && tar -czvf $projectname.tgz $projectname && mv $projectname.tgz $backupdir"
    else
      echo -e "the project not  exists, not need backup"
    fi
    echo -e "backup end \n \n"
  }


  if [ $# == 0 ]; then
    echo -e "the arg is too few \n"
    usage
    exit
  fi

  while getopts "p:i:d:b:" arg
  do
      case $arg in
          p)
              packetdir=$OPTARG
              ;;
          i)
              dstip=$OPTARG
              ;;
          d)
              dstdir=$OPTARG
              ;;
          b)
              backupdir=$OPTARG
              ;;
          ?)
              usage
              exit 0
              ;;
      esac
  done
  check_empty
  getProName
  backup
dispath
echo "MW_SUCCESS"
