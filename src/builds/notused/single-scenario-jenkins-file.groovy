def mvnHome, mvnCommand, mvnBasicCommand, sendEmail

pipeline {
    agent any
    stages {
        stage('Clone Repo') {
            steps {
                script {
                    git branch: '${BRANCH}', credentialsId: "${GIT_LAB_CREDENTIALS_ID}", url: 'https://gitlab.com/dwp/counter-fraud-and-error-management-system/cfems/cfems-e2e-tests.git'
                }
            }
        }

        stage('Prepare') {
            steps {
                script {
                    mvnHome = "/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/maven_3.5.4"
                    mvnCommand = "'${mvnHome}/bin/mvn' -DproxyHost=$CFEMS_HTTP_PROXY_HOST -DproxyPort=$CFEMS_HTTP_PROXY_PORT -Djunit.max.threads=${MAX_THREADS} -Dconfig=jenkins-config.json -Dtest='${SCENARIO_NAME},!uk.gov.dwp.unittests.**'"
                    mvnBasicCommand = "'${mvnHome}/bin/mvn' -DproxyHost=$CFEMS_HTTP_PROXY_HOST -DproxyPort=$CFEMS_HTTP_PROXY_PORT -Djunit.max.threads=${MAX_THREADS} -Denvironment=${ENVIRONMENT}"
                    jdk = tool name: 'JDK1.13'
                    env.JAVA_HOME = "${jdk}"
                    sendEmail = "$SEND_EMAIL"
                }
            }
        }

        stage('Compile') {
            steps {
                sh "${mvnCommand} clean compile"
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    sh "${mvnBasicCommand} -Punit test"
                    sh "${mvnCommand} clean test -Dmaven.test.failure.ignore=true"
                    def failedTests = sh(script: "./scripts/get-failed-tests.py", returnStdout: true).trim()
                    if (failedTests != "") {
                        sh "./scripts/remove-test-failures.py"
                        echo "Repeating failed tests... ${failedTests}"
                        sleep(30)
                        sh "${mvnBasicCommand} -Dtest='${failedTests}' test site -Dsurefire.reportNameSuffix=REPEAT"
                        failedTests = sh(script: "./scripts/get-failed-tests.py", returnStdout: true).trim()
                        if (failedTests != "") {
                            currentBuild.result="FAILURE"
                        } else {
                            currentBuild.result="SUCCESS"
                        }
                    } else {
                        echo "All tests passed."
                        sh "${mvnCommand} site"
                    }

                }
            }
        }
    }

    post {
        always {
            echo 'Tests Complete...'
            junit '**/target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/case-history/*,target/pdf-difference/*,target/actual-pdf-files/*', fingerprint: true
            publishHTML(target: [allowMissing         : false,
                                 alwaysLinkToLastBuild: true,
                                 keepAll              : true,
                                 reportDir            : 'target/site/',
                                 reportFiles          : 'index.html',
                                 reportName           : 'HTML Report',
                                 reportTitles         : 'Single Scenario'])
        }
        failure {
            script {
                echo "Send failure email? ${sendEmail}"
                if (sendEmail == "true")
                    mail subject: "CFEMS E2E Test Job '${env.JOB_NAME}' Has Failed", to: "${EMAIL_LIST}", body: "Project: ${env.JOB_NAME} | Build Number: ${env.BUILD_NUMBER} | URL: ${env.BUILD_URL}"
            }
        }
    }
}