pipeline {
    agent any

    environment {
        TELEGRAM_TOKEN = credentials('tg-bot-token')
        TELEGRAM_CHAT  = credentials('tg-chat-id')
    }

    stages {

        stage('Checkout') {
            steps {
                git 'https://github.com/aleeeGG/restful-booker-tests.git'
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

        stage('Parse Results') {
            steps {
                script {

                    TOTAL = sh(
                        script: "grep -h 'Tests run:' target/surefire-reports/*.txt | awk '{sum+=\$3} END {print sum}'",
                        returnStdout: true
                    ).trim()

                    FAIL = sh(
                        script: "grep -h 'Failures:' target/surefire-reports/*.txt | awk '{sum+=\$2} END {print sum}'",
                        returnStdout: true
                    ).trim()

                    FAILED_TESTS = sh(
                        script: "grep -R '<<< FAILURE!' target/surefire-reports | sed 's/.*reports\\///' | sed 's/.txt.*//' || true",
                        returnStdout: true
                    ).trim()

                    env.MESSAGE = """
Autotests finished

Total: ${TOTAL}
Failed: ${FAIL}

Failed tests:
${FAILED_TESTS}
"""
                }
            }
        }

        stage('Send To Telegram') {
            steps {
                sh """
                curl -s -X POST https://api.telegram.org/bot${TELEGRAM_TOKEN}/sendMessage \
                -d chat_id=${TELEGRAM_CHAT} \
                -d text="${MESSAGE}"
                """
            }
        }
    }

    post {
        always {
            sh 'docker rm booking-container || true'
        }
    }
}
