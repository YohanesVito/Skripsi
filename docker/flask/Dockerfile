FROM ubuntu:focal
RUN sudo apt update
RUN sudo apt install software-properties-common -y
RUN sudo add-apt-repository ppa:deadsnakes/ppa
RUN sudo apt install python3.10 -y
RUN sudo apt install mariadb-client -y
ADD . /code
WORKDIR /code
RUN pip install -r requirements.txt
RUN sudo apt-get install libmysqlclient-dev -y
CMD ["python", "main_local.py"]