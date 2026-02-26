#!/bin/bash

#curl 'http://www.abair.tcd.ie/?view=files&lang=gle&page=synthesis&synth=gd&xpos=0&ypos=372&speed=Gn%C3%A1thluas&pitch=1.0&input=An+gceann%C3%B3idh+t%C3%BA+carr+nua%3F&xmlfile=20170528_110022.xml&colors=default' \
curl 'http://www.abair.tcd.ie/' \
-XPOST \
-H 'Origin: http://www.abair.tcd.ie' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-H 'Referer: http://www.abair.tcd.ie/?view=files&lang=gle&page=synthesis&synth=gd&xpos=0&ypos=372&speed=Gn%C3%A1thluas&pitch=1.0&input=An+gceann%C3%B3idh+t%C3%BA+carr+nua%3F&xmlfile=20170528_110022.xml&colors=default' \
-H 'Upgrade-Insecure-Requests: 1' \
-H 'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8' \
-H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4' \
--data 'input=An+gceann%C3%B3idh+siad+carr+nua%3F&submit=D%C3%A9an+sint%C3%A9is&lang=gle&voice=&view=files&synth=gd&xpos=0&ypos=392&page=synthesis&xmlfile=20170528_110218.xml&colors=default&speed=Gn%C3%A1thluas&countdown=1972'
