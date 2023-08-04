
#install java 11

#install scala 
sudo apt install scala -y
# download spark
wget https://downloads.apache.org/spark/spark-3.4.1/spark-3.4.1-bin-hadoop3.tgz
###add below lines to end of the .bashrc file:
SPARK_HOME="SPARK_DIRECTORY/spark-3.4.1-bin-hadoop3"
export SBT_HOME="SBT_DIRECTORY/bin/sbt-launch.jar" 
export PATH="$PATH:${SBT_HOME}/bin:${SPARK_HOME}/bin:${SPARK_HOME}/sbin"
export PYSPARK_PYTHON=/usr/bin/python3

#go to the spark installation directoy and find 
#conf/spark-env.sh file and modify below lines:
#NOTE: Databricks Runtimes and databricks-connect won't work with Java 14. Only DBR 10.x have an experimental support for Java 11
JAVA_HOME=${JAVA_HOME}
SPARK_WORKER_MEMORY=2g
###
###for dashboard:
sudo snap install node --classic
### install nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.35.3/install.sh | bash
### go to dashboard directory:
npm install --force
sudo npm install -g @angular/cli --force
##now for running dashboard:
nvm use 16
ng serve --open
###

