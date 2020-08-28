FROM zenika/kotlin:1.4-jdk12

RUN mkdir /build

COPY .docker/install-deps.sh /build/
RUN bash /build/install-deps.sh

COPY .docker/install-musl.sh /build/
RUN bash /build/install-musl.sh

RUN cp ./usr/lib/gcc/x86_64-redhat-linux/4.8.2/libstdc++.a /usr/local/musl/lib/

COPY .docker/install-zlib.sh /build/
RUN bash /build/install-zlib.sh

COPY .docker/build.sh /build/
CMD bash /build/build.sh