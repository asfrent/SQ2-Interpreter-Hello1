#!/bin/bash
TEXT=`diff out ok`
if [ $? -eq 0 ]; then
    exit 0
else
    echo "****************"
    echo "* CHECK FAILED *"
    echo "****************"
    echo
    echo "DIFF:"
    echo "====="
    echo "$TEXT"
    echo

    echo "OK:"
    echo "====="
    cat ok
    echo

    echo "OUT:"
    echo "====="
    cat out
    echo

    exit 1
fi
