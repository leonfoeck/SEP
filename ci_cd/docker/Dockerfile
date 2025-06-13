# Verwende die spezifische Maven- und JDK-Version
FROM maven:3.9.7-eclipse-temurin-22

# Aktualisiere die Paketliste und installiere benötigte Pakete
RUN apt-get update && \
    apt-get install -y --no-install-recommends zip=3.0-11build1 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Erstelle eine Gruppe und einen Benutzer mit spezifischen IDs
RUN groupadd --system --gid 1000 test && \
    useradd --system --gid test --uid 1000 --shell /bin/bash --create-home test && \
    chown -R test:test /home/test

# Wechsel zum neu erstellten Benutzer
USER test

# Setze das Arbeitsverzeichnis
WORKDIR /home/test

# Add Healthcheck instruction
HEALTHCHECK --interval=30s --timeout=10s --retries=3 CMD curl -f http://localhost:8080 || exit 1
