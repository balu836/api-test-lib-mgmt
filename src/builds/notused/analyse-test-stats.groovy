def staleTests

pipeline {
    agent any
    stages {
        stage('Clone Repo') {
            steps {
                script {
                    git branch: 'master', credentialsId: "${GIT_LAB_CREDENTIALS_ID}", url: 'https://gitlab.com/dwp/counter-fraud-and-error-management-system/cfems/cfems-e2e-tests.git'
                }
            }
        }

        stage('Analyse') {
            steps {
                script {
                    staleTests = sh(script: "./scripts/count-skipped-tests.sh $BUILD_JOB cfems-e2e-tests $THRESHOLD", returnStdout: true).trim()
                    staleRelease60Tests = sh(script: "./scripts/count-skipped-tests.sh $BUILD_JOB cfems-e2e-tests-release-6.0 $THRESHOLD", returnStdout: true).trim()
                }
            }
        }
    }

    post {
        always {
            script {
                def sendEmail = "$SEND_EMAIL"
                echo "Send failure email? ${sendEmail}"
                echo "Stale tests for master release: ${staleTests}..."
                echo "Stale tests for release-6.0: ${staleRelease60Tests}..."

                if (sendEmail == "true" && staleTests != "{}")
                    mail subject: "The CFEMS E2E Job: master/'${BUILD_JOB}' has tests disabled for more than ${THRESHOLD} days", to: "${EMAIL_LIST}", body: "The following tests have been disabled for more than ${THRESHOLD} days. Please review them! ${staleTests}"

                if (sendEmail == "true" && staleRelease60Tests != "{}")
                    mail subject: "The CFEMS E2E Job: release-6.0/'${BUILD_JOB}' has tests disabled for more than ${THRESHOLD} days", to: "${EMAIL_LIST}", body: "The following tests have been disabled for more than ${THRESHOLD} days. Please review them! ${staleTests}"
            }
        }
    }
}