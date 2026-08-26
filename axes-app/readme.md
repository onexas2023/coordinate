
# Commands to release image
 * docker login registry.gitlab.com
 * docker build . -t nexus.onexas2023.net:8082/repository/docker-releases/axes
 * docker push nexus.onexas2023.net:8082/repository/docker-releases/axes
 