

# Commands to release image 
 * docker build . -t nexus.mshome.net:8082/repository/docker-releases/coordinate
 * docker login registry.gitlab.com
 * docker push nexus.mshome.net:8082/repository/docker-releases/coordinate

# Schema conflict SOP 
 * Get the ready new version (e.g. 0.6.3,which is base on 0.5.1 and include change on 0.5.2)
 * Check user's last db version from flyway\_history\_coordinate (ex. 0.5.2)
 * RD produce a new migration sql that without conflict for new version (simply remove change in 0.5.2 from 0.6.3 in this example)
 * Upgrade DB by the migration sql by SQL command tool
 * Drop flyway\_history\_coordinate
 * Set base line config to new version in boot.yaml (coordinate-data.schema.migration.baseline.coordinate=0.6.3)
  