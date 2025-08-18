FROM curlimages/curl:8.9.1

COPY teachers_bulk.json /data/teachers_bulk.json
COPY load-data.sh /data/load-data.sh

WORKDIR /data

ENTRYPOINT ["./load-data.sh"]
