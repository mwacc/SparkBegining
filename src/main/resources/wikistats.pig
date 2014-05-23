A = LOAD 'hdfs://10.0.2.15:8020/user/hue/sparkin/' USING PigStorage(' ') AS (src: chararray, item:chararray, count: int, size:int);
filtered = FILTER A BY (src matches '^en.*');
filtered2 = FOREACH filtered GENERATE item, count;

grouped = GROUP filtered2 BY item;
counted = FOREACH grouped GENERATE group as item, SUM(filtered2.count) as count;
ordered = ORDER counted BY count DESC;
result = LIMIT ordered 50;
dump result;
