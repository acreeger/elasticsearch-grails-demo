for file in x*
do 
curl -XPUT 'http://localhost:9200/_bulk' --data-binary @$file
done