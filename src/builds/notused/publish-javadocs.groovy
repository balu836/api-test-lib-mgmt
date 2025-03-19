def sshCommand, scpCommand, seleniumHost, mvnHome, mvnCommand

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
                    seleniumHost = "fesjenk@selenium-mgmt.fed-dev-cfems.dwpcloud.uk"
                    httpRoot = "/selenium-shared/pdf-downloads/"
                    sshCommand = "ssh -o 'StrictHostKeyChecking no' -i /var/lib/jenkins/.ssh/id_rsa ${seleniumHost}"
                    scpCommand = "scp -o 'StrictHostKeyChecking no' -i /var/lib/jenkins/.ssh/id_rsa"
                    mvnHome = "/usr/local/apache-maven/apache-maven-3.5.4/apache-maven-3.5.4"
                    mvnCommand = "'${mvnHome}/bin/mvn' -DproxyHost=$CFEMS_HTTP_PROXY_HOST -DproxyPort=$CFEMS_HTTP_PROXY_PORT"
                    jdk = tool name: 'JDK1.13'
                    env.JAVA_HOME = "${jdk}"
                }
            }
        }

        stage('Generate Docs') {
            steps {
                sh "${mvnCommand} javadoc:javadoc"
                sh "${mvnCommand} javadoc:test-javadoc"
            }
        }

        stage('Publish Docs') {
            steps {
                script {
                    sh "${scpCommand} -r target/site ${seleniumHost}:~/javadoc"
                    sh "${sshCommand} 'sudo rm -rf ${httpRoot}/javadoc'"
                    sh "${sshCommand} 'sudo mv ~/javadoc ${httpRoot}/javadoc'"
                    sh "${sshCommand} 'sudo chmod -R o+rX ${httpRoot}/javadoc'"
                }
            }
        }
    }
}