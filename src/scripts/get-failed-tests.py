#! /usr/bin/env python

import glob
import os
import xml.etree.ElementTree

scriptPath = os.path.dirname(os.path.realpath(__file__))
testResultsPath = "{}/../target/surefire-reports".format(scriptPath)
testResults = glob.glob("{}/TEST-*.xml".format(testResultsPath))

result = []

for testResult in testResults:
    tree = xml.etree.ElementTree.parse(testResult)
    for testCase in tree.getroot().iter('testcase'):
        testName = testCase.get("name").split("(")[0]
        testClass = testCase.get("classname")
        isFailure = testCase.find("failure")
        isError = testCase.find("error")
        if isFailure is not None or isError is not None:
            result.append("{}#{}".format(testClass, testName))

print ",".join(result)