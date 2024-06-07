# Use uma imagem base otimizada para Java e Maven
FROM maven:3.8.3-openjdk-17-slim

# Defina argumentos de build-time
ARG JAR_FILE=target/bluehope.jar
ARG PROJECT_HOME=/usr/src/bluehope

# Defina variáveis de ambiente
ENV PROJECT_HOME=$PROJECT_HOME
ENV JAR_NAME=bluehope.jar

# Cria um grupo e um usuário não privilegiado
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Cria o diretório de destino e ajusta permissões
RUN mkdir -p $PROJECT_HOME && chown -R appuser:appgroup $PROJECT_HOME

# Cria o diretório .m2 e ajusta permissões
RUN mkdir -p /home/appuser/.m2 && chown -R appuser:appgroup /home/appuser/.m2

# Define o diretório de trabalho
WORKDIR $PROJECT_HOME

# Copia o código fonte para o contêiner
COPY . .

# Muda para o usuário não privilegiado antes de construir a aplicação
USER appuser

# Empacota a aplicação como um arquivo JAR
RUN mvn clean package -DskipTests

# Adiciona um comando de depuração para listar o conteúdo do diretório target
RUN ls -l $PROJECT_HOME/target

# Muda novamente para o usuário root para mover o arquivo JAR e ajustar permissões
USER root
RUN mv $PROJECT_HOME/target/*.jar $PROJECT_HOME/$JAR_NAME && chown appuser:appgroup $PROJECT_HOME/$JAR_NAME

# Muda para o usuário não privilegiado para execução
USER appuser

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILE}", "bluehope.jar"]
