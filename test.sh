#!/bin/bash

# Blending code & documentation. Read this function only and you will hopefully
# understand most of this file.
function help_me()
{
    # PARAMETERS
    # $1: Config file, may be missing. Default is "config.sh".

    echo "Usage $0 [config-file]"
}

# Check args so we don't run the script with something that's obviously wrong.
function check_args()
{
    if [ $# -gt 1 ]; then
        help_me
        exit 1
    fi
}

# Function that parses all arguments, extracts the config file and sources it.
function load_config()
{
    # Keep this parsing stuff in sync with help_me, please :D.
    if [ $# -eq 0 ]; then
        local CONFIG_FILE="config.sh"
    else
        local CONFIG_FILE=$1
    fi

    # Source the file so we get our configuration loaded.
    source $CONFIG_FILE
}

readonly TMP_TESTDIR="tmp-test-dir"

# Prepares the test environment for a certain test: creates a temporary test
# folder and store it in TMP_TESTDIR variable, copies all program files and all
# test data needed for a specific test. First argument of this function should
# be the name of the directory containing test files (input, expected output,
# maybe scripts, etc.).
function prepare_test()
{
    # Trim trailing backslash in directory vars.
    local TESTFILES="${1%/}"

    # Make sure the folder exists and it is empty.
    if [ -d $TMP_TESTDIR ]; then
        rm -rf $TMP_TESTDIR/*
    else
        mkdir $TMP_TESTDIR
    fi

    # Copy program files.
    for PFILE in $PROGRAM_FILES; do
        cp -R "$PFILE" "$TMP_TESTDIR"
    done

    # Copy everything in the test dir.
    cp -n -R $TESTFILES/* "$TMP_TESTDIR"
}

# Clean the mess left behind by a test.
function clean_test()
{
    if [ -d $TMP_TESTDIR ]; then
        rm -rf $TMP_TESTDIR
    fi
}

# Pretty prints test path, dots dots dots and then [FAIL] / [PASS].
# Arguments: $1 - test name / path, $2 - 0 for pass, 1 for fail.
function pretty_print()
{
    USE_COLORS=1
    local TESTFILES="${1%/}"
    local TEST_RESULT=$2
    local TEST_DURATION=$3

    local readonly COLOR_RED='\e[1;31m';
    local readonly COLOR_GREEN='\e[1;32m';
    local readonly COLOR_YELLOW='\e[1;33m';
    local readonly COLOR_END='\e[0m';
    local readonly PADLENGTH=54
    local readonly PADDING=`printf '%0.1s' "."{1..80}`
    if [ ${USE_COLORS} -eq 1 ]; then
        local readonly PASS_PATTERN="[${COLOR_GREEN}PASS${COLOR_END}]"
        local readonly FAIL_PATTERN="[${COLOR_RED}FAIL${COLOR_END}]"
        local readonly TIME_PATTERN="[${COLOR_YELLOW}TIME${COLOR_END}]"
    else
        local readonly PASS_PATTERN="[PASS]"
        local readonly FAIL_PATTERN="[FAIL]"
        local readonly TIME_PATTERN="[TIME]"
    fi

    printf "%s" $TESTFILES
    printf "%*.*s" 0 $((PADLENGTH - ${#TESTFILES})) $PADDING
    case $TEST_RESULT in
        0)
            printf "$PASS_PATTERN"
            ;;
        1)
            printf "$FAIL_PATTERN"
            ;;
        2)
            printf "$TIME_PATTERN"
            ;;
    esac

    printf " (took ${TEST_DURATION}s)"

    printf "\n"
}

# Runs a test and displays a funny message (in fact there's nothing funny about
# PASS / FAIL). Expects as the first argument the directory in which test files
# reside.
function run_test()
{
    local TESTFILES="${1%/}"

    prepare_test $TESTFILES
    # We need to know where we are, so we can return later here
    local CURRENT_DIR="`pwd`"
    cd $TMP_TESTDIR
    TEST_DURATION="?"
    validate_test
    pretty_print "$TESTFILES" $TEST_RESULT $TEST_DURATION
    cd $CURRENT_DIR
    clean_test
}

function main()
{
    check_args "$@"
    load_config "$@"

    INDIV_TESTS=`find "$TESTFILES_ROOT/" -mindepth 1 -maxdepth 1 -type d | sort`
    for TESTFILES in $INDIV_TESTS; do
        run_test "$TESTFILES"
    done
}

# This function will be used to check the program. You should implement a call
# to your program and then verify the output. The function will be called in
# the directory where the test files and program files have been copied.
# This function should bind variable TEST_RESULT to one of the following values:
#     0 - pass
#     1 - fail
#     2 - timeout (one may find useful timed_run function)
# Variable TEST_DURATION _may_ be set as well.
function validate_test()
{
    set +e
    TEST_RESULT=0
    timed_run bash run.sh
    if [ $TIMED_RUN_RESULT -eq 0 ]; then
        STD_OUT_AND_ERR=`bash check.sh`
        if [ $? -ne 0 ]; then
            echo "${STD_OUT_AND_ERR}"
            TEST_RESULT=1
        fi

    else
        TEST_RESULT=2
    fi

    set -e
}

# Timed run of a command. Starts the command in background and waits for it for
# certain amount of time. When the timeout passes, the command gets killed.
# As a return value, this function sets the TIMED_RUN_RESULT variable to one
# of the following values:
# 0 - finished in time
# 1 - did not finish in time
# NOTE This will _NOT_ work on the cluster.
function timed_run()
{
    $@ 2>&1 | grep -v "warning" | grep -v "^$" &

    local PID=$!
    disown $PID
    local T_START=`date +%s%N`
    local T_CURRENT=`date +%s%N`
    while true; do
        local T_CURRENT=`date +%s%N`
        TEST_DURATION=$(((T_CURRENT-T_START)/1000000))
        if [ $TEST_DURATION -gt $TIMEOUT_MILLIS ]; then
            break;
        fi
        RUNNING=`is_running $PID`
        if [ $RUNNING -eq 0 ]; then
            break
        fi
        sleep 0.01
    done

    RUNNING=`is_running $PID`
    TEST_DURATION="$((TEST_DURATION/1000)).$((TEST_DURATION%1000))"

    if [ $RUNNING -eq 1 ]; then
        kill -9 $PID
        TIMED_RUN_RESULT=1
    else
        TIMED_RUN_RESULT=0
    fi
}

# Checks wether a certain process is running, given its PID as the first
# argument. Its return value (1 - running, 0 - not running) is written to the
# standard output.
function is_running()
{
    local RUNNING=`ps -e | sed 's/^ *//g' | cut -d ' ' -f 1 | grep "^$1$" | wc -l`
    if [ $RUNNING -ne 0 ]; then
        echo 1
    else
        echo 0
    fi
}

# set -v # Echo commands (for debug purposes).
set -e # Exit on command error.
set -u # Error un unset variable.
# exec 2>/dev/null # Disable stderr

main "$@"

