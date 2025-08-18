#!/bin/sh
set -e

ES_URL="http://elastic:9200"
INDEX="teachers"
BULK_FILE="/data/teachers_bulk.json"

echo "⏳ Waiting for Elasticsearch to be ready at $ES_URL ..."
until curl -s "$ES_URL" >/dev/null 2>&1; do
  sleep 2
done
echo "✅ Elasticsearch is up."

# Check if index already exists
if curl -s -o /dev/null -w "%{http_code}" "$ES_URL/$INDEX" | grep -q "200"; then
  echo "ℹ️ Index '$INDEX' already exists. Skipping data load."
else
  echo "📌 Creating index with mapping..."
  curl -s -X PUT "$ES_URL/$INDEX" -H 'Content-Type: application/json' -d '{
   "mappings": {
     "properties": {
       "id":         { "type": "keyword" },
       "name":       { "type": "text" },
       "description":{ "type": "text" },
       "subject":    { "type": "keyword" },
       "level":      { "type": "keyword" },
       "rating":     { "type": "float" },
       "availability": {
         "type": "nested",
         "properties": {
           "day":        { "type": "keyword" },
           "start_time": { "type": "date", "format": "HH:mm" },
           "end_time":   { "type": "date", "format": "HH:mm" }
         }
       }
     }
   }
  }'

  echo "🚀 Loading bulk data into '$INDEX'..."
  curl -s -H "Content-Type: application/json" \
       -X POST "$ES_URL/_bulk" \
       --data-binary @"$BULK_FILE"

  echo "✅ Data loaded successfully."
fi
