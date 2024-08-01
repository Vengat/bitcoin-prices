# Use the official CentOS 7 base image
FROM centos:7 AS build

# Install Git and Maven
RUN yum -y install git maven

# Set the working directory
WORKDIR /app

# Copy the source code into the container
COPY . /app

# Build the Maven project and package the application as a JAR
RUN mvn clean install spring-boot:repackage

# Create a new CentOS image with the JAR file
FROM centos:7

# Copy the JAR file from the previous image
COPY --from=build /app/target/*.jar /app/app.jar

# Set the working directory
WORKDIR /app

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]