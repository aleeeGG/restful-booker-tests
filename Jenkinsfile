pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                git 'https://github.com/ТВОЙ_ГИТ/RestfulBookerTesting.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t booking-tests .'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'docker run --name booking-container booking-tests || true'
            }
        }

        stage('Copy Reports') {
            steps {
                sh 'docker cp booking-container:/app/target ./target || true'
            }
        }

        stage('Send Report To Telegram') {
            steps {
                sh '''
                TOTAL=$(grep -o "Tests run: [0-9]*" target/surefire-reports/*.txt | head -1)
                FAIL=$(grep -o "Failures: [0-9]*" target/surefire-reports/*.txt | head -1)

                FAILED_TESTS=$(grep -R "<<< FAILURE!" target/surefire-reports | sed 's/.*reports\\///' | sed 's/.txt.*//' )

                MESSAGE="Autotests finished\n$TOTAL\n$FAIL\n\nFailed tests:\n$FAILED_TESTS"

                curl -X POST https://api.telegram.org/bot<TOKEN>/sendMessage \
                -d chat_id=<CHAT_ID> \
                -d text="$MESSAGE"
                '''
            }
        }
    }

    post {
        always {
            sh 'docker rm booking-container || true'
        }
    }
}
