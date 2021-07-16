#!/bin/bash
if [ `cat out | grep "Error" | wc -l` -gt 0 ]; then
    exit 0
else
    echo "****************"
    echo "* CHECK FAILED *"
    echo "****************"
    echo
    echo "This test should have semantic errors."
    echo
    echo "OUT:"
    echo "====="
    cat out
    echo

    exit 1
fi
