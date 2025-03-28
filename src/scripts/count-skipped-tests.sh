#!/usr/bin/env bash

BUILD_JOB=$1
BUILD_FOLDER=$2
THRESHOLD=$3

function countSkippedTests() {
  RESULT=$(cat $1 | grep "<skipped>true</skipped>" | wc -l)
  echo $RESULT
}

echo "{}" >test-results.json

RES=$(find $JENKINS_HOME/jobs/QA/jobs/$BUILD_FOLDER/jobs/$BUILD_JOB/builds -mindepth 1 -maxdepth 1 -type d -mtime -7)
for i in $RES; do
  RESULT_FILE=${i}/junitResult.xml
  FILE_DATE=$(date -r $RESULT_FILE "+%d-%m-%Y")
  ./scripts/analyse-test-results.py ingest $RESULT_FILE $FILE_DATE
done

./scripts/analyse-test-results.py report $THRESHOLD
