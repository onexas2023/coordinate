
# Commands to run allinone image
 * docker stack deploy -c coordinate.allinone.compose.yaml coordinate --with-registry-auth
 (--with-registry-auth are used as a image-pull bug workaround when running in private repository)
