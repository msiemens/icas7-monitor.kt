FROM zenika/kotlin:1.4-jdk12

RUN yum install --enablerepo=ol7_optional_latest -y \
    gcc \
    libstdc++-devel \
    libstdc++-static \
    glibc-static \
    zlib-devel \
    zlib-static \
    && yum clean -y all

CMD cd /app && \
    ./gradlew nativeImage \
        --no-daemon \
        -Dorg.gradle.jvmargs=-XX:+UseContainerSupport \
        -Dorg.gradle.unsafe.watch-fs=false