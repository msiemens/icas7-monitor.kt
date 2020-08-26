set -e

ARTIFACT=zlib-1.2.11.tar.gz

cd /tmp
wget https://zlib.net/$ARTIFACT
tar -xvf $ARTIFACT

cd zlib-*

CC=/usr/local/musl/bin/musl-gcc ./configure --static --prefix=/usr/local/musl
make
make install

cd /tmp
rm -rf zlib*