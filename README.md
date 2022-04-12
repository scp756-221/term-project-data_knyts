[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-f059dc9a6f8d3a56e377f745f24479a46679e63a5d9fe6f495e02850cd0d8118.svg)](https://classroom.github.com/online_ide?assignment_repo_id=7355594&assignment_repo_type=AssignmentRepo)
# CMPT756 Project: Team Data Knyts

For our project, we have decided to implement a Playlist microservice to extend the Users and Music microservices. Since this project heavily emphasizes the distributed systems aspect of a cloud application, such as scaling and load testing, we chose to implement a relatively simple microservice that we clearly understand and are confident in our ability to create. This will allow us to put more focus on analyzing our system, documenting our findings, and practicing agile methodology.

This is the project repo for **CMPT 756 (Spring 2022)**

## Important Directories

- `cluster`: Configuration files for the cluster
- `db`: Database service
- `gatling`: Scala scripts used by Gatling to generate test load on the application
- `loader`: Loader service used to insert data into the DynamoDB service
- `logs`: Where logs are stored
- `mcli`: Client for the music service
- `s1`: User service
- `s2`: Music service
- `s3`: Playlist service
- `tools`: For quick scripts that are useful in make-files

## Deploying and running

#### 1. Instantiate the template files

Fill in the required values in the template variable file.


Copy the file `cluster/tpl-vars-blank.txt` to `cluster/tpl-vars.txt`
and fill in all the required values in `tpl-vars.txt`. Note that you
will need to have installed Gatling (https://gatling.io/open-source/start-testing/) first, because you will be entering its path in `tpl-vars.txt`. 

Once you have filled in all the details, run:

~~~
make -f k8s-tpl.mak templates
~~~

#### 2. Create EKS cloud cluster

~~~
make -f eks.mak start
~~~

#### 3. Create a namespace for our cluster

The context name for the EKS cluster we just made is aws756. We will use that context-name for kubectl:

~~~
kubectl config use-context aws756
kubectl create ns c756ns
kubectl config set-context aws756 --namespace=c756ns
~~~

#### 4. Build Docker images and push to Github container registry

Build images for the database service, user service, music service, playlist service, and the data loader. After running this command, you may go to Github packages and make each image public.

~~~
make -f k8s.mak cri
~~~

#### 5. Deploy our services to the cluster

~~~
istioctl install --set profile=demo -y
make -f k8s.mak dynamodb-init
make -f k8s.mak gw db s2 s3 s1
~~~

#### 6. Get external IP used by our application

To send requests to our application or view dashboards such as Grafana, we can get the external IP of our application by running:

~~~
kubectl -n istio-system get service istio-ingressgateway | cut -c -140
~~~

## Monitoring and Analysis

The following tools (Grafana, Prometheus and Gatling) are orchestrated together to create a unified monitoring solution for our application. This can be set up by running:

~~~
make -f k8s.mak provision
~~~

#### Grafana

The Grafana URL can be found by running:

~~~
make -f k8s.mak grafana-url
~~~

#### Prometheus

The Prometheus URL can be found by running:

~~~
make -f k8s.mak prometheus-url
~~~

#### Gatling

The command below can be run to generate test load using Gatling:

~~~
TODO: 
~~~

And the following command stops Gatling jobs that are currently running:

~~~
./tools/kill-gatling.sh
~~~

#### Scaling

~~~
kubectl scale deployment/'service_name' --replicas='number_of_replicas'
~~~
Example: 
~~~
kubectl scale deployment/cmpt756db --replicas=10
~~~
Checking the change
~~~
kubectl describe deploy/cmpt756db
~~~

## Killing the Cluster

~~~
make -f eks.mak stop
~~~
