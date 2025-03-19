### Selenium GRID Setup

This will show the setup instructions for a remote selenium grid used to run the tests (mainly from Jenkins).

It is setup as a collection of docker containers (See `selenium-grid.yml`) that are hosted on an EC2 instance.

It is provisioned as part of the DevOps terraform suite so manual intervention should not be necessary.
These steps should only be used as last resort as maintenance steps on an existing server.
In the future these steps will be moved to puppet scripts by the DevOps team.


#### Available Servers

- http://selenium-nonprod.cfems.awsfed-dev.dwpcloud.uk:4444/grid/console
    
    - Old version (no longer used)
    - It's an `m5.large` so is a bit costly given the spiky nature of the load
    
- http://selenium-two-nonprod.cfems.awsfed-dev.dwpcloud.uk:4444/grid/console

    - Currently being used
    - It's a `t3.large` which is much better w.r.t. pricing although available credits should be monitored
    
    
#### Containers

See `selenium-grid.yml` for the details:

 - `selenium-grid-hub` is the main hub container that exposes the entrypoint URL.
 
 - `selenium-chrome-node` hosts the chrome nodes.
 
 - `selenium-firefox-node` hosts the firefox nodes.
 
 - `grid-nginx` exposes downloaded files for access from the grid clients that are running the tests.
 
 
#### Setup Steps

- Copy the `selenium-grid.yml` file to `/grid-setup/selenium-grid.yml`
- Copy the 'file-upload folder' to '/grid-setup/'

- Stop the containers if they are already started

    ```bash
    docker-compose -f /grid-setup/selenium-grid.yml down -v
    ```

- Create the shared folder and set the permissions on it.

    ```bash
    mkdir -p /selenium-shared/pdf-downloads
    chmod -R 0777 /selenium-shared/
    ```

- Start the containers.

    ```bash
    docker-compose -f /grid-setup/selenium-grid.yml up -d
    ```

#### Troubleshooting Issues

- Having problems with the grid, perhaps try [redeploying the grid container](http://jenkins-nonprod.cfems.awsfed-dev.dwpcloud.uk:8080/job/QA/job/cfems-e2e-tests/job/restart-grid-docker-containers/) 

- Get the error message `docker-compose: error while loading shared libraries: libz.so.1: failed to map segment from shared object: Operation not permitted`?
Run the following to remount `/tmp`

    ```bash
    mount /tmp -o remount,exec
    ```
    
- Might need to update the version of `container-selinux` running if you get some errors starting the containers.

    ```bash
    yum install -y http://mirror.centos.org/centos/7/extras/x86_64/Packages/container-selinux-2.107-3.el7.noarch.rpm
    ```
