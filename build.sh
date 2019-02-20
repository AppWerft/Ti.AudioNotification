#!/bin/bash

ti build -b -p android -d android

git add -A
git commit -m "litle fixes"
git push origin master