#! /usr/bin/env python

import glob
import os
import xml.etree.ElementTree as ET

scriptPath = os.path.dirname(os.path.realpath(__file__))
surefireReports = "{}/../target/surefire-reports".format(scriptPath)
reports = glob.glob("{}/TEST-*.xml".format(surefireReports))


def remove_test_case(doc, testCase):
    for testSuite in doc.getroot().iter('testsuite'):
        testSuite.remove(testCase)


for report in reports:
    reportXml = ET.parse(report)
    removals = []

    for testCase in reportXml.getroot().iter('testcase'):
        isFailure = testCase.find("failure")
        isError = testCase.find("error")
        testName = testCase.get("name")
        testClass = testCase.get("classname")
        if isFailure is not None or isError is not None:
            print "Removing at {}#{} from reports. It will be re-tried...".format(testClass, testName)
            removals.append(testCase)

    for removal in removals:
        remove_test_case(reportXml, removal)

    reportXmlString = ET.tostring(reportXml.getroot(), encoding='utf8', method='xml')
    output = open(report, "w")
    output.write(reportXmlString)
    output.close()
