#!/bin/sh

# devdaily.com
#
# this script converts all files that match the pattern "*.GIF" to lower-case.

for i in `ls *.GIF`
do
  orig=$i
  new=`echo $i | tr [A-Z] [a-z]`
  echo "Moving $orig --> $new"
  mv $orig $new
done

