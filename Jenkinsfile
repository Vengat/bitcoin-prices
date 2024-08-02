pipeline {
    agent any

    environment {
        UNIT_TESTS_CRITERION = '80%' // Replace with your desired unit test pass criterion
    }

    stages {
        stage('Build and Test') {
            steps {
                sh 'mvn clean install'
                sh "mvn test -Dcriterion=${UNIT_TESTS_CRITERION}"
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build('my-image:latest')
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://docker-registry.example.com', 'docker-credentials') {
                        docker.image('my-image:latest').push()
                    }
                }
            }
        }

        stage('Deploy to Artifactory') {
            steps {
                // Replace with your Artifactory configuration
                // For example, using the Jenkins Artifactory plugin:
                // artifactoryDeployer(
                //     id: 'artifactory',
                //     server: 'ArtifactoryServer',
                //     deployPattern: '**/*.jar'
                // )
            }
        }
    }
}