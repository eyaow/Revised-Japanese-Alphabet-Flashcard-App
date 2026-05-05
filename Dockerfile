# STAGE 1: Build
FROM ubuntu:24.04 AS build
RUN apt-get update && apt-get install -y wget gnupg software-properties-common unzip maven
RUN wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add - && \
    add-apt-repository -y https://packages.adoptium.net/artifactory/deb && \
    apt-get update && apt-get install -y temurin-23-jdk

WORKDIR /build
COPY pom.xml .
COPY src ./src

# Build the JPro release and move it to a clean folder
RUN mvn jpro:release && \
    ZIP_FILE=$(ls target/*.zip | head -n 1) && \
    unzip $ZIP_FILE -d /build/extracted && \
    INTERNAL_DIR=$(find /build/extracted -maxdepth 1 -type d | grep -v "extracted$" | head -n 1) && \
    mkdir -p /build/final_release && \
    cp -r $INTERNAL_DIR/* /build/final_release/

# STAGE 2: Run (Slim Runtime)
FROM ubuntu:24.04
RUN apt-get update && apt-get install -y \
    wget gnupg software-properties-common \
    libgtk-3-0 libpango-1.0-0 libgl1 libxtst6 libxxf86vm1 \
    libfontconfig1 libnss3 libasound2t64 locales \
    libopenjfx-java openjfx \
    && rm -rf /var/lib/apt/lists/*

RUN wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add - && \
    add-apt-repository -y https://packages.adoptium.net/artifactory/deb && \
    apt-get update && apt-get install -y temurin-23-jre

RUN locale-gen en_US.UTF-8
ENV LANG=en_US.UTF-8
ENV LC_ALL=en_US.UTF-8

WORKDIR /app
COPY --from=build /build/final_release/ /app/

# Pull the Monocle native library from the system into the app natives folder
RUN mkdir -p /app/natives && \
    find /usr/lib -name "libglass_monocle.so" -exec cp {} /app/natives/ \;

# Generate the startup script with the explicit application mapping
RUN echo '#!/bin/bash \n\
cd /app \n\
export JPRO_CP_STR=$(find . -name "*.jar" | tr "\\n" ":" | sed "s/:$//") \n\
export ABS_NATIVE_PATH="/app/natives" \n\
export START_PROPS=$(find . -name "start.properties" | head -n 1) \n\
\n\
export LD_LIBRARY_PATH="$ABS_NATIVE_PATH:$LD_LIBRARY_PATH" \n\
\n\
exec java \
  -Dglass.platform=Monocle \
  -Dmonocle.platform=Headless \
  -Dprism.order=sw \
  -Djava.library.path="$ABS_NATIVE_PATH" \
  -Djpro.port=${PORT:-8080} \
  -Djprocp="$JPRO_CP_STR" \
  -Djpro.applications.default=com.hirakata.Hirakata \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  --add-opens java.base/java.util=ALL-UNNAMED \
  -cp "$JPRO_CP_STR:$ABS_NATIVE_PATH" \
  com.jpro.boot.JProBoot \
  "$START_PROPS"' > /app/run_jpro.sh

RUN chmod +x /app/run_jpro.sh && sed -i "s/\r$//" /app/run_jpro.sh

ENV PORT=8080
EXPOSE 8080
CMD ["/bin/bash", "/app/run_jpro.sh"]
