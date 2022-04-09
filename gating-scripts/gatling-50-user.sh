sudo docker container run --detach --rm \
  -v ${PWD}/gatling/results:/opt/gatling/results \
  -v ${PWD}/gatling:/opt/gatling/user-files \
  -v ${PWD}/gatling/target:/opt/gatling/target \
  -e CLUSTER_IP=a85a2297119fd49c4acdaeb6cd19c190-222644939.us-west-2.elb.amazonaws.com \
  -e USERS=50 \
  -e SIM_NAME=ReadUserSim \
  --label gatling \
  ghcr.io/scp-2021-jan-cmpt-756/gatling:3.4.2 \
  -s proj756.ReadUserSim
