#!/bin/bash

[ "$UID" -eq 0 ] || exec sudo bash "$0" "$@"

set -x

#cf: https://docs.docker.com/machine/drivers/gce/
gcloud auth login

#launch machine
docker-machine create --driver google --google-project formation-manage --google-machine-type f1-micro manager-1
date
docker-machine create --driver google --google-project formation-manage --google-machine-type f1-micro node-1
date
docker-machine create --driver google --google-project formation-manage --google-machine-type f1-micro consul-1
date

#get ips
manager_ip=$(docker-machine ip manager-1)
node_ip=$(docker-machine ip node-1)
consul_ip=$(docker-machine ip consul-1)

# Launch consul
eval "$(docker-machine env --swarm consul-1)"
docker run --name consul-server-1 -h consul-server-1 -p 8500:8500 -d progrium/consul -server -bootstrap -ui-dir /ui
date

# Launch swarm manager
eval "$(docker-machine env --swarm manager-1)"
docker run -d -p 4000:4000 swarm manage -H :4000 --replication --advertise $manager_ip:4000 consul://$consul_ip:8500
date

# secondary swarm manager:
# docker-machine create --driver google --google-project formation-manage --google-machine-type f1-micro manager-2
# manager2_ip=$(docker-machine ip manager-2)
# docker run -d -p 4000:4000 swarm manage -H :4000 --replication --advertise $manager2_ip:4000 consul://$consul_ip:8500
# date

eval "$(docker-machine env --swarm node-1)"
docker run -d swarm join --advertise=$node_ip:2375 consul://$consul_ip:8500
date

# to use consul machine as node too
eval "$(docker-machine env --swarm consul-1)"
docker run -d swarm join --advertise=$consul_ip:2375 consul://$consul_ip:8500
date

# secondary node
# eval "$(docker-machine env --swarm node-2)"
# node2_ip=$(docker-machine ip node-2)
# docker run -d swarm join --advertise=$node2_ip:2375 consul://$consul_ip:8500
# date

# launch app container
eval "$(docker-machine env --swarm manager-1)"
docker -H :4000 run hello-world

swarm list consul://$consul_ip/



