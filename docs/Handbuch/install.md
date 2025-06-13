# Installationsanleitung

## 1. Voraussetzungen
- **Administrative Rechte**: Bestätigen Sie, dass Sie über administrative Rechte auf dem Server verfügen.
- **Netzwerkberechtigungen**: Stellen Sie sicher, dass alle notwendigen Netzwerkberechtigungen konfiguriert sind, bevor Sie fortfahren.

## 2. Installationsschritte

### 2.1 SMTP-Serverkonfiguration (Mailserver)
- **Anforderung**: Zugriff auf den SMTP-Server ist erforderlich für E-Mail-Funktionen.
- **Einrichtung**:
  - Bestätigen Sie die Erreichbarkeit des SMTP-Servers vom Anwendungsserver über den festgelegten Port.
  - Detaillierte Konfiguration folgt in Abschnitt 5.

### 2.2 PostgreSQL-Datenbank-Setup (Datenbankserver)
- **Anforderung**: Verwendet PostgreSQL 16.2 zur Datenspeicherung.
- **Einrichtung**:
  - Installieren Sie PostgreSQL 16.2 gemäß der Anleitung in der [offiziellen Dokumentation](https://www.postgresql.org/docs/16/index.html).
  - Erstellen Sie eine Datenbank und einen Benutzer mit vollen Rechten speziell für die Anwendung.
  - Überprüfen Sie die Verbindungsgeschwindigkeit (mindestens 1 Gbit/s) zwischen Datenbank- und Anwendungsserver.
  - Spezifische Verbindungsdetails werden in Abschnitt 5 bereitgestellt.

### 2.3 Java-Umgebungseinrichtung (Anwendungsserver)
- **Anforderung**: Java 21 ist notwendig.
- **Einrichtung**:
  - Laden Sie OpenJDK 21 von [OpenJDK 21](https://jdk.java.net/21/) herunter und installieren Sie es.
  - Bestätigen Sie die Installation durch Ausführen von `java -version` in der Kommandozeile.

### 2.4 Apache Tomcat-Installation (Anwendungsserver)
- **Anforderung**: Kompatibel mit Apache Tomcat 10.1.20.
- **Einrichtung**:
  - Laden Sie Tomcat von [Tomcat 10.1.20](https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.20/bin/) herunter und installieren Sie es.
  - Implementieren Sie SSL/TLS gemäß der [Tomcat SSL/TLS-Konfiguration](https://tomcat.apache.org/tomcat-10.1-doc/ssl-howto.html).
  - Weitere Einzelheiten finden Sie im [Tomcat-Setup-Leitfaden](https://tomcat.apache.org/tomcat-10.1-doc/setup.html) und in der [allgemeinen Dokumentation](https://tomcat.apache.org/tomcat-10.1-doc/index.html).

### 2.5 Anwendungsbereitstellung (Anwendungsserver)
- **Einrichtung**:
  - Platzieren Sie die `.war`-Datei im Verzeichnis `$CATALINA_HOME/webapps` unter einem neuen Ordner `talent_tauscher`.
  - Entpacken Sie die `.war`-Datei an diesem Ort.

## 3. Konfiguration

### 3.1 SMTP-Serverkonfiguration
- **Dateiort**: Geben Sie Details in `mail_config.properties` unter `$CATALINA_HOME/webapps/talent_tauscher/WEB-INF/config` ein.
- **Parameter**:
  - `smtp.host`: SMTP-Server-Hostname oder IP.
  - `smtp.port`: SMTP-Server-Port (25, 465 oder 587).
  - `smtp.username` & `smtp.password`: Authentifizierungsdaten.
  - `smtp.auth`: Authentifizierungsbedarf (`true` falls ja).
  - `smtp.starttls.enable`: TLS-Bedarf (`true` falls ja).
- **Testen**: Überprüfen Sie, indem Sie eine Test-E-Mail über die Anwendung oder ein Befehlszeilen-Tool senden.

### 3.2 Datenbankkonfiguration
- **Dateiort**: Geben Sie Details in `db_config.properties` am gleichen Ort wie die SMTP-Einstellungen ein.
- **Parameter**:
  - Verbindungsdaten (Benutzername, Passwort) und Serverdetails (Hostname, Datenbankname).
  - `db.ssl`: Ob SSL erforderlich ist (`true` falls ja).
  - `db.pool.size`: Größe des Verbindungspools (Standard: `10`).
- **Validierung**: Überprüfen Sie Tomcat-Logs auf Konfigurationsfehler.

## 4. Anwendung starten
- **Server starten**: Führen Sie `$CATALINA_HOME/bin/startup.sh` aus, um den Server zu starten, üblicherweise innerhalb von 8 Sekunden.

## 5. Zugriff auf die Web-Oberfläche
- **URL**: Greifen Sie auf die App unter `https://<hostname>:<port>/talent_tauscher` zu.
- **Erstes Login**: Verwenden Sie Standardanmeldedaten und ändern Sie sofort das Passwort.

## 6. Herunterfahren
- **Verfahren**: Verwenden Sie `$CATALINA_HOME/bin/shutdown.sh` für ein schnelles Herunterfahren (typischerweise unter 60 Sekunden).

## 7. Unterstützung
- **Hilfe und Fehlerbehebung**: Wenn Sie Hilfe bei der Navigation auf der Website benötigen oder auf Probleme stoßen, klicken Sie auf das Fragezeichen-Symbol in der Kopfzeile. Dies bietet Ihnen schrittweise Anleitungen und Tipps zur Fehlerbehebung, um gängige Probleme effektiv zu lösen.

# Installation Guide

## 1. Prerequisites
- **Administrative Rights**: Confirm you have administrative rights on the server.
- **Network Permissions**: Ensure all necessary network permissions are configured before proceeding.

## 2. Installation Steps

### 2.1 SMTP Server Configuration (Mail Server)
- **Requirement**: SMTP server access is required for email functionalities.
- **Setup**:
    - Confirm the SMTP server's reachability from the application server using the designated port.
    - Detailed configuration will follow in Section 5.

### 2.2 PostgreSQL Database Setup (Database Server)
- **Requirement**: Uses PostgreSQL 16.2 for data storage.
- **Setup**:
    - Install PostgreSQL 16.2, following the guidance in the [official documentation](https://www.postgresql.org/docs/16/index.html).
    - Create a database and user with full privileges specifically for the application.
    - Verify the connection speed (minimum of 1 Gbit/s) between the database and application servers.
    - Connection specifics are provided in Section 5.

### 2.3 Java Environment Setup (Application Server)
- **Requirement**: Java 21 is necessary.
- **Setup**:
    - Download and install OpenJDK 21 from [OpenJDK 21](https://jdk.java.net/21/).
    - Confirm installation by executing `java -version` in the command line.

### 2.4 Apache Tomcat Installation (Application Server)
- **Requirement**: Compatible with Apache Tomcat 10.1.20.
- **Setup**:
    - Download and install Tomcat from [Tomcat 10.1.20](https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.20/bin/).
    - Implement SSL/TLS by following the [Tomcat SSL/TLS Configuration](https://tomcat.apache.org/tomcat-10.1-doc/ssl-howto.html).
    - Refer to the [Tomcat Setup Guide](https://tomcat.apache.org/tomcat-10.1-doc/setup.html) and [General Documentation](https://tomcat.apache.org/tomcat-10.1-doc/index.html) for further details.

### 2.5 Application Deployment (Application Server)
- **Setup**:
    - Place the `.war` file in the `$CATALINA_HOME/webapps` directory under a new folder `talent_tauscher`.
    - Unpack the `.war` file in this location.

## 3. Configuration

### 3.1 SMTP Server Configuration
- **File Location**: Enter details in `mail_config.properties` at `$CATALINA_HOME/webapps/talent_tauscher/WEB-INF/config`.
- **Parameters**:
    - `smtp.host`: SMTP server hostname or IP.
    - `smtp.port`: SMTP server port (25, 465, or 587).
    - `smtp.username` & `smtp.password`: Authentication credentials.
    - `smtp.auth`: Authentication requirement (`true` if yes).
    - `smtp.starttls.enable`: TLS requirement (`true` if yes).
- **Testing**: Verify by sending a test email through the application or a command-line tool.

### 3.2 Database Configuration
- **File Location**: Enter details in `db_config.properties` at the same location as SMTP settings.
- **Parameters**:
    - Connection credentials (username, password) and server details (hostname, database name).
    - `db.ssl`: Whether SSL is required (`true` if yes).
    - `db.pool.size`: Connection pool size (default: `10`).
- **Validation**: Check Tomcat logs for configuration errors.

## 4. Launching the Application
- **Start Server**: Run `$CATALINA_HOME/bin/startup.sh` to start the server, usually within 8 seconds.

## 5. Accessing the Web Interface
- **URL**: Access the app at `https://<hostname>:<port>/talent_tauscher`.
- **Initial Login**: Use default credentials and change the password immediately.

## 6. Shutting Down
- **Procedure**: Use `$CATALINA_HOME/bin/shutdown.sh` for a quick shutdown (typically under 60 seconds).

## 7. Support
- **Assistance and Troubleshooting**: If you need help navigating the website or encounter any issues, click the question mark icon in the header. This will provide you with step-by-step guidance and troubleshooting tips to resolve common problems effectively.