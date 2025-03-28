#!/usr/bin/env bash

SOURCE=$CI_MERGE_REQUEST_SOURCE_BRANCH_NAME
TARGET=$CI_MERGE_REQUEST_TARGET_BRANCH_NAME

BRANCH1=$1
BRANCH2=$2

if [[ ("$SOURCE" == "$BRANCH1" && "$TARGET" == "$BRANCH2") || ("$SOURCE" == "$BRANCH2" && "$TARGET" == "$BRANCH1") ]]; then
  echo "You're not allowed to merge from $SOURCE to $TARGET"
  exit 1
fi
