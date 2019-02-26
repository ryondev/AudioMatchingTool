#!/bin/bash

echo "play begin now";

for file_name in `ls`;
do
  ffplay -ar 16000 -channels 1 -f s16le -i $file_name
  echo "play next pcm"
done
  
