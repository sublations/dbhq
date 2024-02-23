# Stage 1: Build the application using a Debian-based JDK image
FROM openjdk:17-slim as builder

WORKDIR /app

# Copy the source code and build scripts into the container
COPY . .

# Use the Gradle Wrapper to execute the ShadowJar task, ensuring a consistent build environment
# The ShadowJar task generates a fat JAR which includes all dependencies
RUN ./gradlew clean shadowJar --no-daemon

# Stage 2: Setup the runtime environment
FROM openjdk:17-slim

# Create a non-root user and group for running the application securely
RUN groupadd -r botgroup && useradd -r -g botgroup botuser

WORKDIR /app

# Create the logs directory and ensure it's writable by the botuser
RUN mkdir logs && chown botuser:botgroup logs && chmod 755 logs

# Copy the fat JAR built in the previous stage to the container
COPY --from=builder /app/build/libs/dbhq-*.jar ./app.jar

# Switch to the non-root user for enhanced security
USER botuser

# Command to run the application
# Use environment variables to configure your application at runtime
ENTRYPOINT ["java", "-jar", "app.jar"]
