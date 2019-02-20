#!/bin/bash

if [[ $# -eq 0 ]] ; then
    echo "No message supplied"
    exit 1
fi

ti build -b -p android -d android

git add -A
git commit -m '$1'
git push origin master