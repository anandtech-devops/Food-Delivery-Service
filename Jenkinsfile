pipeline {

    agent any

    environment {
        AWS_REGION = 'ap-south-1'
        ECR_REGISTRY = '166637874911.dkr.ecr.ap-south-1.amazonaws.com'
        ECR_REPOSITORY = 'food-delivery-service'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/anandtech-devops/Food-Delivery-Service.git'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: false, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Application') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh '''
                docker build \
                    -t ${ECR_REPOSITORY}:${IMAGE_TAG} .
                '''
            }
        }

        stage('Login to ECR') {
            steps {
                sh '''
                aws ecr get-login-password --region ${AWS_REGION} |
                docker login \
                    --username AWS \
                    --password-stdin ${ECR_REGISTRY}
                '''
            }
        }

        stage('Push Image to ECR') {
            steps {
                sh '''
                docker tag \
                    ${ECR_REPOSITORY}:${IMAGE_TAG} \
                    ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}

                docker push \
                    ${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}
                '''
            }
        }

        stage('Deploy with Helm') {
            steps {
                sh '''
                helm upgrade --install food-delivery-helm ./food-delivery \
                    --namespace food-delivery \
                    --create-namespace \
                    --set image.tag=${IMAGE_TAG}
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                echo "Waiting for Kubernetes rollout..."

                kubectl rollout status deployment/food-delivery-helm \
                    -n food-delivery \
                    --timeout=120s

                echo "Deployed Image:"
                kubectl get deployment food-delivery-helm \
                    -n food-delivery \
                    -o jsonpath='{.spec.template.spec.containers[0].image}'
                echo ""

                echo "Pods:"
                kubectl get pods -n food-delivery \
                    -l app.kubernetes.io/instance=food-delivery-helm \
                    -o wide
                '''
            }
        }
    }
}
