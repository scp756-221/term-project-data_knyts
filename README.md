[![Open in Visual Studio Code](https://classroom.github.com/assets/open-in-vscode-f059dc9a6f8d3a56e377f745f24479a46679e63a5d9fe6f495e02850cd0d8118.svg)](https://classroom.github.com/online_ide?assignment_repo_id=7355594&assignment_repo_type=AssignmentRepo)
# CMPT756 Project: Team Data Knyts

For our project, we have decided to implement a Playlist microservice to extend the Users and Music microservices. Since this project heavily emphasizes the distributed systems aspect of a cloud application, such as scaling and load testing, we chose to implement a relatively simple microservice that we clearly understand and are confident in our ability to create. This will allow us to put more focus on analyzing our system, documenting our findings, and practicing agile methodology.

This is the project repo for **CMPT 756 (Spring 2022)**

### File Structure
`cluster`
`s1`
`s2`
`s3`

### Microservices

##### Music
##### Users
##### Playlist


### Running

#### 1. Instantiate the template files

Fill in the required values in the template variable file


Copy the file `cluster/tpl-vars-blank.txt` to `cluster/tpl-vars.txt`
and fill in all the required values in `tpl-vars.txt`.  

Once you have filled in all the details, run

~~~
$ make -f k8s-tpl.mak templates
~~~

### TODO: Next Steps

### TODO: TPA 
#### Grafana
#### Prometheus
#### Gatling

### TODO: Analysis & Simulations
