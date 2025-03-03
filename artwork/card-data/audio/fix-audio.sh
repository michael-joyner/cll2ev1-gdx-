#!/bin/bash
BR=128

set -e
set -o pipefail
trap 'echo ERROR; read a' ERR

cd "$(dirname "$0")"
cwd="$(pwd)"

for x in ???; do
	echo "--- mp3gain $x/"
	cd "$cwd"
	cd "$x"
	for mp3 in *.mp3; do
		if [ ! -f "$mp3" ]; then continue; fi
		normalize-mp3 -T 1 --bitrate "$BR" --mp3 "$mp3"
		#normalize-mp3 -b -a .7 -T 1 --bitrate "$BR" "$mp3"
		#mp3gain -q -m 6 -s i -k -a "$mp3" > /dev/null
		#mp3gain -k -r "$mp3"
	done
done
echo "DONE"
exit 0
