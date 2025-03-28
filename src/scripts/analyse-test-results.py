#! /usr/bin/env python

import json
import os
import sys
import xml.etree.ElementTree as ET
from datetime import datetime, date

scriptPath = os.path.dirname(os.path.realpath(__file__))
resultPath = "{}/../test-results.json".format(scriptPath)

if sys.argv[1] == "ingest":
    filePath = sys.argv[2]
    fileDateStr = sys.argv[3]
    fileDate = datetime.strptime(fileDateStr, "%d-%m-%Y")

    tree = ET.parse(filePath)

    for case in tree.getroot().iter('case'):
        if case.find("skipped").text == "true":
            className = case.find("className").text
            testName = case.find("testName").text
            fullName = "{}.{}".format(className, testName)

            testResults = json.loads(open(resultPath, "r").read())
            caseInfo = testResults.get(fullName, {})
            caseCount = caseInfo.get("count", 0)
            caseInfo["count"] = caseCount + 1

            fromDateStr = caseInfo.get("from", date.today().strftime("%d-%m-%Y"))
            fromDate = datetime.strptime(fromDateStr, "%d-%m-%Y")
            toDateStr = caseInfo.get("to", "01-01-1970")
            toDate = datetime.strptime(toDateStr, "%d-%m-%Y")

            if fileDate >= toDate:
                caseInfo["to"] = fileDate.strftime("%d-%m-%Y")

            if fileDate <= fromDate:
                caseInfo["from"] = fileDate.strftime("%d-%m-%Y")

            testResults[fullName] = caseInfo

            output = open(resultPath, "w")
            output.write(json.dumps(testResults, indent=2))
            output.close()

if sys.argv[1] == "report":
    testResults = json.loads(open(resultPath, "r").read())
    output = {}
    maxDays = int(sys.argv[2])
    for key in testResults:
        info = testResults[key]
        diff = datetime.strptime(info["to"], "%d-%m-%Y") - datetime.strptime(info["from"], "%d-%m-%Y")
        if diff.days > maxDays:
            output[key] = "{} days".format(diff.days)
    print json.dumps(output, indent=2)
