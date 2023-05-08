
# Commands to release image
 * docker login registry.gitlab.com
 * docker build . -t nexus.mshome.net:8082/repository/docker-releases/axes
 * docker push nexus.mshome.net:8082/repository/docker-releases/axes
 