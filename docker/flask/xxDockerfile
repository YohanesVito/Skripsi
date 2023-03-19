FROM ubuntu:focal
RUN apt update
RUN apt install software-properties-common -y
RUN add-apt-repository ppa:deadsnakes/ppa
RUN apt install python3.10 -y
RUN apt install mariadb-client -y
RUN apt-get install python3-pip -y
ADD . /code
WORKDIR /code
RUN apt-get install libmysqlclient-dev -y
RUN pip3 install -r requirements.txt
CMD ["python3", "main_local.py"]
