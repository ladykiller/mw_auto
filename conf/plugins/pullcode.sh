#!/bin/sh

basedir=/opt/auto/workspace/
branch=''
vcs=''
url=''
project=''
function usage() {
    echo "-b the branch you want to deploy"
    echo "-v the name for the vcs tool,such as git or svn"
    echo "-u the addres of the repo"
    echo "-p the  name of the repo"
}
function check_empty() {
    if [ x$branch == "x" ]; then
        echo "-b must set"
        usage
        exit 1
    fi
    if [ x$vcs == "x" ]; then
        echo "-v must set"
        usage
        exit 1
    fi
    if [ x$url == "x" ]; then
        echo "-u must set"
        usage
        exit 1
    fi
    if [ x$project == "x" ]; then
        echo "-u must set"
        usage
        exit 1
    fi
}
pullcode() {
    echo "starting pull code \n"
    echo "the basedir is $basedir \n"
    echo "the repo address is $url \n"
    echo "the repo name  is $project\n"
    echo "the branch is $branch \n"
    echo "the vcs tool is $vcs \n"
    cd $basedir
    if [ -d $project ] && [ -e $project ]; then
        cd $project
    else
        git clone $url
        cd $project
    fi
    git checkout $branch
    git pull -f
    echo "end pull code \n"
    echo "MW_SUCCESS"
}

while getopts "b:v:u:p:" arg
do
    case $arg in
        b)
            branch=$OPTARG
            ;;
        v)
            vcs=$OPTARG
            ;;
        u)
            url=$OPTARG
            ;;
        p)
            project=$OPTARG
            ;;
        ?)
            usage
            exit 0
            ;;
    esac
done
check_empty
pullcode
