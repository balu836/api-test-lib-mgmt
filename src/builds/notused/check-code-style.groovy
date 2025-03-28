def mvnHome, mvnCommand, sendEmail

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
                    mvnCommand = "'${mvnHome}/bin/mvn' -DproxyHost=$CFEMS_HTTP_PROXY_HOST -DproxyPort=$CFEMS_HTTP_PROXY_PORT"
                    jdk = tool name: 'JDK1.13'
                    env.JAVA_HOME = "${jdk}"
                    sendEmail = "$SEND_EMAIL"
                }
            }
        }

        stage('Compile & Lint') {
            steps {
                sh "${mvnCommand} clean compile checkstyle:checkstyle"
            }
        }
    }

    post {
        always {
            echo 'Checks Complete...'
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