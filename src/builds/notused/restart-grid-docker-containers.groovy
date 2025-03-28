def sshCommand, scpCommand, seleniumHost, dockerComposePath

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
                    dockerComposePath = "/grid-setup/selenium-grid.yml"
                    sshCommand = "ssh -o 'StrictHostKeyChecking no' -i /var/lib/jenkins/.ssh/id_rsa ${seleniumHost}"
                    scpCommand = "scp -o 'StrictHostKeyChecking no' -i /var/lib/jenkins/.ssh/id_rsa "
                }
            }
        }

//        stage('Deploy') {
//            steps {
//                script {
//                    sh "echo 'deploying docker-compose file...'"
//                    sh "${scpCommand} grid/selenium-grid.yml ${seleniumHost}:~"
//                   sh "${sshCommand} 'sudo cp ~/selenium-grid.yml ${dockerComposePath}'"
//                    sh "${sshCommand} 'sudo rm -rf /home/fesjenk/file-upload'"
//                    sh "${sshCommand} 'sudo rm -rf /grid-setup/file-upload'"
//                    sh "${scpCommand} -r grid/file-upload ${seleniumHost}:~"
//                    sh "${sshCommand} 'sudo cp -r ~/file-upload /grid-setup/file-upload'"
//                }
//            }
//        }

        stage('Stop Containers') {
            steps {
                script {
                    sh "echo 'stopping containers...'"
                    sh "${sshCommand} 'sudo docker-compose -f ${dockerComposePath} down -v'"
                }
            }
        }

        stage('Cleanup') {
            steps {
                script {
                    sh "echo 'cleaning up...'"
                    sh """
                        ${sshCommand} 'sudo rm -f /selenium-shared/pdf-downloads/*.pdf'
                        ${sshCommand} 'sudo ls -la /selenium-shared/pdf-downloads/'
                        ${sshCommand} 'sudo rm -f /selenium-shared/pdf-uploads/*'
                        ${sshCommand} 'sudo ls -la /selenium-shared/pdf-uploads/'
                    """
                }
            }
        }

        stage('Start Containers') {
            steps {
                sh "echo 'starting containers...'"
                sh "${sshCommand} 'sudo docker-compose -f ${dockerComposePath} build'"
                sh "${sshCommand} 'sudo docker-compose -f ${dockerComposePath} up --force-recreate -d'"
            }
        }
    }
}