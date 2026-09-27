# Food Delivery Service — DevOps CI/CD & Kubernetes Project

A production-style **Food Delivery Order Management System** built with Java Spring Boot and deployed using a complete DevOps CI/CD workflow.

The project demonstrates application containerization, automated CI/CD, private AWS ECR integration, self-managed Kubernetes deployment, Helm-based releases, AWS RDS MySQL integration, and Kubernetes monitoring using Prometheus and Grafana.

---

## 🚀 Project Overview

The application provides REST APIs for managing food delivery restaurant information.

The complete DevOps workflow is:

```text
Developer
   │
   ▼
GitHub
   │
   ▼
Jenkins CI/CD
   │
   ├── Maven Build
   │
   ├── Docker Build
   │
   ├── AWS ECR Push
   │
   └── Helm Deployment
           │
           ▼
     Kubernetes Cluster
           │
           ├── Food Delivery Application
           │
           └── AWS RDS MySQL
           
Monitoring:
Kubernetes → Prometheus → Grafana
```

---

## 🛠️ Technology Stack

### Application

* Java 21
* Spring Boot 3.5.5
* REST API
* MySQL

### DevOps

* Git
* GitHub
* Jenkins
* Maven
* Docker
* AWS ECR
* Kubernetes
* Helm
* Linux
* Bash

### AWS

* Amazon EC2
* Amazon ECR
* Amazon RDS MySQL
* IAM

### Kubernetes

* Kubernetes v1.34.11
* kubeadm
* containerd
* Flannel
* Kubernetes Secrets
* Services
* Deployments
* Ingress
* Helm

### Monitoring

* Prometheus
* Grafana
* kube-state-metrics
* Node Exporter

---

# 🏗️ Architecture

```text
                     ┌─────────────────┐
                     │     GitHub      │
                     │  Source Code    │
                     └────────┬────────┘
                              │
                              ▼
                     ┌─────────────────┐
                     │     Jenkins     │
                     │    CI / CD      │
                     └────────┬────────┘
                              │
                 ┌────────────┼────────────┐
                 │            │            │
                 ▼            ▼            ▼
              Maven        Docker       AWS ECR
               Build        Build         Push
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │ Kubernetes      │
                                    │ Cluster         │
                                    └────────┬────────┘
                                             │
                                      Helm Deployment
                                             │
                                             ▼
                              ┌────────────────────────┐
                              │ Food Delivery Pods      │
                              │ Spring Boot Application │
                              └───────────┬────────────┘
                                          │
                                          ▼
                                  ┌────────────────┐
                                  │ AWS RDS MySQL  │
                                  └────────────────┘

Monitoring:

Kubernetes
    │
    ▼
Prometheus
    │
    ▼
Grafana
```

---

# 📁 Project Structure

```text
Food-Delivery-Service/
│
├── food-delivery/
│   ├── Chart.yaml
│   ├── values.yaml
│   └── templates/
│       ├── deployment.yaml
│       ├── service.yaml
│       └── ingress.yaml
│
├── src/
│   ├── main/
│   │   └── java/
│   └── test/
│
├── Dockerfile
├── Jenkinsfile
├── pom.xml
└── README.md
```

---

# 🔄 CI/CD Pipeline

Jenkins automates the complete application deployment workflow.

### Pipeline stages

```text
Checkout Code
      ↓
Build Application
      ↓
Build Docker Image
      ↓
Login to AWS ECR
      ↓
Push Docker Image
      ↓
Deploy with Helm
      ↓
Verify Kubernetes Rollout
```

### Jenkins Pipeline

The pipeline performs:

1. Checkout code from GitHub.
2. Build the Spring Boot application using Maven.
3. Create a Docker image.
4. Authenticate with AWS ECR.
5. Push the Docker image to ECR.
6. Deploy/update the application using Helm.
7. Verify the Kubernetes rollout.

Docker images are tagged using the Jenkins `BUILD_NUMBER`.

Example:

```text
food-delivery-service:7
```

and pushed to:

```text
166637874911.dkr.ecr.ap-south-1.amazonaws.com/food-delivery-service:7
```

---

# 🐳 Docker

The application is containerized using Docker.

Base image:

```dockerfile
FROM eclipse-temurin:21-jre
```

The application runs on:

```text
Port: 8080
```

Docker allows the same application image to be promoted through different deployment environments without changing the application package.

---

# ☸️ Kubernetes Deployment

The application runs on a self-managed Kubernetes cluster created using `kubeadm`.

### Cluster

```text
1 Master Node
2 Worker Nodes
```

Networking:

```text
Flannel
```

Container runtime:

```text
containerd
```

The application is deployed in:

```text
Namespace: food-delivery
```

The Kubernetes Deployment runs multiple replicas for application availability.

---

# 📦 Helm

The Kubernetes application is packaged using Helm.

Helm chart:

```text
food-delivery/
```

The chart contains:

```text
Chart.yaml
values.yaml
templates/
```

Helm is used by Jenkins to deploy the application:

```bash
helm upgrade --install food-delivery-helm ./food-delivery \
    --namespace food-delivery \
    --create-namespace \
    --set image.tag=${IMAGE_TAG}
```

This allows the Docker image tag to be updated automatically during each CI/CD deployment.

---

# 🔐 Private AWS ECR Authentication

The Docker images are stored in a private Amazon ECR repository.

Kubernetes uses an image pull secret:

```text
ecr-secret
```

The Helm Deployment references this secret using:

```yaml
imagePullSecrets:
  - name: ecr-secret
```

This allows Kubernetes worker nodes to authenticate with the private ECR repository and pull application images.

---

# 🗄️ Database

The application uses **Amazon RDS MySQL** as the persistent database.

Database configuration is provided to Kubernetes through environment variables and Kubernetes Secrets.

Sensitive database credentials are not stored directly in the application source code.

Secret:

```text
food-delivery-db-secret
```

The database password is referenced using:

```yaml
valueFrom:
  secretKeyRef:
    name: food-delivery-db-secret
    key: SPRING_DATASOURCE_PASSWORD
```

---

# 📊 Monitoring

The Kubernetes cluster is monitored using:

```text
Prometheus + Grafana
```

The monitoring stack was deployed using:

```text
kube-prometheus-stack
```

Prometheus collects Kubernetes metrics and Grafana provides visualization dashboards.

### Metrics monitored

* Kubernetes node health
* Pod health
* Pod CPU usage
* Pod memory usage
* Pod restart count
* Kubernetes components
* Container metrics
* Application workload status

Example PromQL queries:

### CPU

```promql
sum by (pod) (
  rate(container_cpu_usage_seconds_total{
    namespace="food-delivery",
    container!="POD",
    container!=""
  }[5m])
)
```

### Memory

```promql
sum by (pod) (
  container_memory_working_set_bytes{
    namespace="food-delivery",
    container!="POD",
    container!=""
  }
)
```

### Pod Restarts

```promql
kube_pod_container_status_restarts_total{
  namespace="food-delivery"
}
```

### Pod Status

```promql
kube_pod_status_phase{
  namespace="food-delivery",
  pod=~"food-delivery-helm-.*"
}
```

---

# 🔧 Troubleshooting & Real-World Issues

During implementation, several Kubernetes and CI/CD issues were identified and resolved.

### 1. Private ECR ImagePullBackOff

Pods initially failed to pull the private ECR image.

Root cause:

```text
no basic auth credentials
```

Resolution:

* Created Kubernetes Docker registry secret.
* Configured `imagePullSecrets` in the Helm Deployment.
* Validated the Helm chart.
* Redeployed through Jenkins.

---

### 2. Kubernetes Worker DiskPressure

A worker node reported:

```text
DiskPressure=True
```

The EC2 EBS volume was expanded and the filesystem was resized.

After cleanup/resizing and kubelet recovery:

```text
DiskPressure=False
```

---

### 3. Kubernetes Secret Key Mismatch

The application initially failed with:

```text
CreateContainerConfigError
```

Root cause:

The Deployment referenced:

```text
password
```

while the actual Kubernetes Secret contained:

```text
SPRING_DATASOURCE_PASSWORD
```

The Helm Deployment was corrected to reference the correct secret key.

---

### 4. Jenkins Kubernetes Access

Jenkins initially did not have Kubernetes credentials.

A Kubernetes kubeconfig was securely configured on the Jenkins server, allowing Jenkins to execute:

```bash
kubectl
```

and:

```bash
helm
```

against the Kubernetes cluster.

---

### 5. Automated Rollout Verification

The Jenkins pipeline verifies the Kubernetes deployment using:

```bash
kubectl rollout status deployment/food-delivery-helm \
    -n food-delivery \
    --timeout=120s
```

This ensures that Jenkins reports failure when the Kubernetes rollout does not become healthy.

---

# 🧪 Application API

The application exposes restaurant APIs under:

```text
/restaurants
```

Example:

```http
GET /restaurants
```

Example response:

```json
[
  {
    "id": 1,
    "name": "Anand Food Corner",
    "location": "Lucknow",
    "cuisine": "Indian"
  }
]
```

---

# 🔒 Security Practices

The project follows basic DevOps security practices:

* Database passwords are stored in Kubernetes Secrets.
* AWS resources use IAM roles where applicable.
* Private ECR authentication is handled through Kubernetes image pull secrets.
* Sensitive credentials are not committed to GitHub.
* Application deployment is automated through Jenkins.

---

# 🚀 Deployment Flow

A typical deployment works as follows:

```text
1. Developer pushes code
          ↓
2. GitHub stores source code
          ↓
3. Jenkins starts pipeline
          ↓
4. Maven builds application
          ↓
5. Docker image is created
          ↓
6. Image is pushed to AWS ECR
          ↓
7. Helm updates Kubernetes deployment
          ↓
8. Kubernetes pulls new image
          ↓
9. Application pods start
          ↓
10. Jenkins verifies rollout
          ↓
11. Prometheus collects metrics
          ↓
12. Grafana visualizes metrics
```

---

# 📌 Key DevOps Concepts Demonstrated

This project demonstrates practical experience with:

* CI/CD automation
* Infrastructure on AWS
* Docker containerization
* Kubernetes orchestration
* Helm-based deployments
* Private container registries
* Kubernetes Secrets
* Database integration
* Linux administration
* Application monitoring
* Prometheus and PromQL
* Grafana dashboards
* Deployment troubleshooting
* Root cause analysis
* Automated deployment verification

---

# 👨‍💻 Author

**Anand Srivastava**

DevOps Engineer | AWS | Kubernetes | Docker | Jenkins | CI/CD

* LinkedIn: https://linkedin.com/in/anand-srivastava-79b51918
* GitHub: https://github.com/anandtech-devops
* Portfolio: https://ananddevops.website
* Project Repository: https://github.com/anandtech-devops/Food-Delivery-Service
