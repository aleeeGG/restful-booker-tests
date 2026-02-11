pipeline {
    agent any

    environment {
        TELEGRAM_TOKEN = credentials('tg-bot-token')
        TELEGRAM_CHAT  = credentials('tg-chat-id')
    }

    stages {

        stage('Сборка Docker-образа') {
            steps {
                sh 'docker build -t booking-tests .'
            }
        }

        stage('Запуск автотестов') {
            steps {
                sh 'docker run --name booking-container booking-tests || true'
            }
        }

        stage('Копирование отчетов') {
            steps {
                sh 'docker cp booking-container:/app/target ./target || true'
            }
        }

        stage('Анализ результатов') {
            steps {
                script {

                    TOTAL = sh(
                        script: """
                        grep -h "Tests run:" target/surefire-reports/*.txt \
                        | sed 's/.*Tests run: //' \
                        | sed 's/,.*//' \
                        | awk '{sum+=\$1} END {print sum}'
                        """,
                        returnStdout: true
                    ).trim()

                    FAIL = sh(
                        script: """
                        grep -h "Failures:" target/surefire-reports/*.txt \
                        | sed 's/.*Failures: //' \
                        | sed 's/,.*//' \
                        | awk '{sum+=\$1} END {print sum}'
                        """,
                        returnStdout: true
                    ).trim()

                    FAILED_TESTS = sh(
                        script: """
                        grep -R "<<< FAILURE!" target/surefire-reports \
                        | sed 's/.*reports\\///' \
                        | sed 's/.txt.*//' \
                        | sort -u || true
                        """,
                        returnStdout: true
                    ).trim()

                    if (FAILED_TESTS == "") {
                        FAILED_TESTS = "Нет упавших тестов 🎉"
                    }

                    env.MESSAGE = """
Результаты автотестов

Всего тестов: ${TOTAL}
Упало: ${FAIL}

Упавшие тесты:
${FAILED_TESTS}
"""
                }
            }
        }

        stage('Отправка отчета в Telegram') {
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
