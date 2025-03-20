#!/usr/bin/env bash

yum install git -y

git fetch --all
git checkout ${CI_MERGE_REQUEST_TARGET_BRANCH_NAME}
AHEAD_BY=$(git rev-list --right-only --count origin/${CI_MERGE_REQUEST_TARGET_BRANCH_NAME}...origin/${CI_MERGE_REQUEST_SOURCE_BRANCH_NAME})
BEHIND_BY=$(git rev-list --left-only --count origin/${CI_MERGE_REQUEST_TARGET_BRANCH_NAME}...origin/${CI_MERGE_REQUEST_SOURCE_BRANCH_NAME})

echo "Ahead by: ${AHEAD_BY}"
echo "Behind by: ${BEHIND_BY}"

if [ "$BEHIND_BY" != "0" ]; then
  echo "Your source branch ${CI_MERGE_REQUEST_SOURCE_BRANCH_NAME} is behind origin/${CI_MERGE_REQUEST_TARGET_BRANCH_NAME} by ${BEHIND_BY} commits. Please pull in all changes from origin/${CI_MERGE_REQUEST_TARGET_BRANCH_NAME}"
  exit 1
fi
