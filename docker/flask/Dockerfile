FROM python:3.10.1
ADD . /code
WORKDIR /code
RUN pip install -r requirements.txt
RUN apt-get install libmariadb-dev
CMD ["python", "main_local.py"]