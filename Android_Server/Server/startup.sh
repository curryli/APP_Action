#!/bin/bash

python ~/acc/index.py >> ~/acc.log 2>&1 &

tail -0f ~/acc.log

