docker build -t build-kotln .

docker run \
  --rm \
  -it \
  -v "$(pwd)":/app \
  -v "$(pwd)"/dist/:/app/build/graal \
  -v /tmp/kotlin-native/gradle-root:/root/.gradle \
  -v /tmp/kotlin-native/gradle:/app/.gradle build-kotln:latest