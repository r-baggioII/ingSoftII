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
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build (Maven)') {
      steps {
        script {
          sh '''
          set -euo pipefail
          echo "Building all Maven modules..."
          if [ "${SKIP_TESTS}" = 'true' ]; then
            mvn -B -U -DskipTests package
          else
            mvn -B -U package
          fi
          '''
        }
      }
    }

    stage('Build Docker images and deploy') {
      steps {
        script {
          // We run a shell loop to find artifacts and build images.
          sh '''
          set -euo pipefail
          echo "Searching for built artifacts (jar/war) under **/target ..."

          # find all jar/war files in targets (exclude -sources and -javadoc if any)
          artifacts=$(find . -type f \( -name "*.jar" -o -name "*.war" \) -path "*/target/*" ! -name "*-sources.*" ! -name "*-javadoc.*" || true)

          if [ -z "${artifacts}" ]; then
            echo "No artifacts found. Exiting stage.";
            exit 0
          fi

          echo "Found artifacts:";
          echo "${artifacts}"

          # Iterate over artifacts
          echo "Processing artifacts..."
          IFS=$'\n'
          for artifact in ${artifacts}; do
            echo "---- artifact: ${artifact}"
            # module dir = path before /target/
            module_dir=$(dirname "${artifact%/target/*}")
            # In case artifact path is like ./module/target/app.jar
            module_dir=$(echo "${artifact}" | sed -E 's#(.*)/target/.*#\1#')

            # derive a safe image name from module_dir
            img_name=$(echo "${module_dir}" | sed -E 's#^\./##; s#/#-#g; s/[^a-zA-Z0-9_.-]/_/g')
            if [ -n "${DOCKER_REGISTRY}" ]; then
              full_image="${DOCKER_REGISTRY}/${img_name}:${IMAGE_TAG}"
            else
              full_image="${img_name}:${IMAGE_TAG}"
            fi

            echo "Module dir: ${module_dir}, image: ${full_image}"

            # If module has its own Dockerfile, use it as build context
            if [ -f "${module_dir}/Dockerfile" ]; then
              echo "Found Dockerfile in ${module_dir}, building image..."
              docker build -t "${full_image}" "${module_dir}"
            else
              # create temp build context
              tmpdir=$(mktemp -d)
              echo "Creating temp docker context ${tmpdir}"
              cp "${artifact}" "${tmpdir}/app.jar"
              cat > "${tmpdir}/Dockerfile" <<'DOCK'
              FROM eclipse-temurin:17-jre-jammy
              COPY app.jar /app/app.jar
              WORKDIR /app
              ENTRYPOINT ["java","-jar","/app/app.jar"]
DOCK
              echo "Building image from generic Dockerfile..."
              docker build -t "${full_image}" "${tmpdir}"
              rm -rf "${tmpdir}"
            fi

            # Optionally push to registry if configured
            if [ -n "${DOCKER_REGISTRY}" ] && [ -n "${DOCKER_REG_CREDENTIALS}" ]; then
              echo "Pushing ${full_image} to registry..."
              docker push "${full_image}"
            fi

            # Run the container: container name = img_name (sanitized)
            container_name=$(echo "${img_name}" | sed -E 's/[^a-zA-Z0-9_.-]/_/g')
            echo "Stopping existing container (if any) named ${container_name}..."
            docker rm -f "${container_name}" || true

            # Default port mapping; allow overriding with env var PORT_MAPS (e.g. "8080:8080,9000:9000")
            if [ -n "${PORT_MAPS:-}" ]; then
              port_args=""
              IFS=','; for pm in ${PORT_MAPS}; do port_args+=" -p ${pm}"; done
            else
              port_args=" -p ${DEFAULT_PORT}:${DEFAULT_PORT}"
            fi

            echo "Running container ${container_name} from image ${full_image}"
            # shellcheck disable=SC2086
            docker run -d --restart unless-stopped --name "${container_name}" ${port_args} "${full_image}"
          done
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
