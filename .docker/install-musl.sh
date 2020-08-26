set -e

ARTIFACT=musl-1.2.1.tar.gz

cd /tmp
wget https://musl.libc.org/releases/$ARTIFACT
tar -xvf $ARTIFACT

cd musl-*

./configure --disable-shared
make
make install

cd /tmp
rm -rf musl*