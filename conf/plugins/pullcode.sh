#!/bin/sh

basedir=/Users/majl/
branch=''
vcs=''
url=''
function usage() {
    echo "-b the branch you want to deploy"
    echo "-v the name for the vcs tool,such as git or svn"
    echo "-u the addres of the repo"
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
}
pullcode() {
    echo "starting pull code \n"
    echo "the basedir is $basedir \n"
    echo "the repo address is $url \n"
    echo "the branch is $branch \n"
    echo "the vcs tool is $vcs \n"
    cd $basedir
    rm -fr $branch
    git clone $url $branch
    cd $branch
    git checkout $branch
    git pull -f
    echo "end pull code \n"
}

while getopts "b:v:u:" arg
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
        ?)
            usage
            exit 0
            ;;
    esac
done
check_empty
pullcode