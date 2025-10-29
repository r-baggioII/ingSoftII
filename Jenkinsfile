// Declarative Jenkins pipeline
// Requirements for the Jenkins agent executing this pipeline:
// - Maven installed and available as `mvn`
// - Docker daemon available and the Jenkins user allowed to run `docker` commands
// - (Optional) Docker registry credentials configured in Jenkins and the CREDENTIAL ID passed in env variable DOCKER_REG_CREDENTIALS
// Behavior:
// 1) Runs `mvn -B -DskipTests package` on the repository (builds all Maven modules)
// 2) Finds produced .jar/.war files under module target/ directories
// 3) For modules that contain a Dockerfile, builds an image using that Dockerfile
// 4) For stand-alone jars/wars without a Dockerfile, creates a simple Dockerfile at build time using openjdk and packages the jar into an image
// 5) Optionally pushes images to a registry if DOCKER_REGISTRY and DOCKER_REG_CREDENTIALS are provided
// 6) Runs containers (docker run) replacing any previous container with the same name

pipeline {
  agent any

  environment {
    // Set these in Jenkins job or leave empty for local docker-only deployment
    DOCKER_REGISTRY = "" // e.g. myregistry.example.com (optional)
    DOCKER_REG_CREDENTIALS = "" // Jenkins credentials ID for registry (optional)
    SKIP_TESTS = 'true'
    IMAGE_TAG = "${env.BRANCH_NAME ?: 'local'}-${env.BUILD_NUMBER ?: '0'}"
    DEFAULT_PORT = '8080' // default port mapping when nothing else provided
    // Use exact absolute path provided by the user. Can be overridden in job config.
    MODULE_PATH = "/root/ingSoftII/SCRUM/ej5/D/greedy_gym"
  }

  stages {
    stage('Build & Deploy greedy_gym') {
      steps {
        // Ensure repository is checked out into the Jenkins workspace so paths like
        // $WORKSPACE/SCRUM/ej5/D/greedy_gym exist and are accessible to the jenkins user.
        checkout scm
        script {
          sh '''
          set -eu

          # Use the exact path provided by the user (MODULE_PATH). Do not try multiple candidates.
          module_dir="${MODULE_PATH}"

          if [ ! -d "${module_dir}" ]; then
            echo "ERROR: requested MODULE_PATH does not exist or is not accessible: ${module_dir}"
            echo "Check permissions: the jenkins user may not have access to /root."
            echo "Requested path listing (if accessible):"
            ls -la "${module_dir}" || true
            echo "JENKINS WORKSPACE env: ${WORKSPACE:-<unset>}"
            echo "Current working directory: $(pwd)"
            echo "Workspace root listing (for debugging):"
            ls -la || true
            exit 2
          fi

          echo "Entering ${module_dir}"
          cd "${module_dir}"

          echo "Pulling latest changes (if any)"
          git pull || true

          echo "Building with Maven..."
          # Ensure Java 17 is available for Maven
          export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
          export PATH="$JAVA_HOME/bin:$PATH"
          mvn -B -U -DskipTests package

          echo "Building docker image..."
          img_name="greedy_gym"
          if [ -n "${DOCKER_REGISTRY}" ]; then
            full_image="${DOCKER_REGISTRY}/${img_name}:${IMAGE_TAG}"
          else
            full_image="${img_name}:${IMAGE_TAG}"
          fi

          docker build -t "${full_image}" .

          if [ -n "${DOCKER_REGISTRY}" ] && [ -n "${DOCKER_REG_CREDENTIALS}" ]; then
            echo "Pushing ${full_image} to registry..."
            docker push "${full_image}"
          fi

          echo "Restarting container ${img_name}"
          docker rm -f "${img_name}" || true

          if [ -n "${PORT_MAPS:-}" ]; then
            port_args=""
            IFS=','; for pm in ${PORT_MAPS}; do port_args+=" -p ${pm}"; done
          else
            port_args=" -p ${DEFAULT_PORT}:${DEFAULT_PORT}"
          fi

          # shellcheck disable=SC2086
          docker run -d --restart unless-stopped --name "${img_name}" ${port_args} "${full_image}"
          '''
        }
      }
    }
  }

  post {
    success {
      echo "Pipeline completed successfully."
    }
    failure {
      echo "Pipeline failed. Check logs." 
    }
  }
}
