set -e

export PATH=$PATH:/usr/local/musl/bin

cd /app
./gradlew nativeImage \
    --no-daemon \
    -Dorg.gradle.jvmargs=-XX:+UseContainerSupport \
    -Dorg.gradle.unsafe.watch-fs=false
