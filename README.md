# Factx

**Frontend** para el sistema de facturación electrónica conectado con el SRI (Servicio de Rentas Internas) en un ambiente de producción.

Este proyecto está basado en **Java** con **WebFlux** para construir las entidades y recibir datos mediante una API. El frontend está construido con **JavaScript** y utiliza **Bootstrap** para ofrecer una interfaz de usuario interactiva, que permite gestionar las facturas electrónicas de forma fácil y rápida, integrándose con los servicios del SRI para la validación y envío de facturas.


## Requisitos previos

Asegúrate de tener instalados los siguientes requisitos:

- **Java 17** o superior
- **Maven** para gestión de dependencias y construcción del proyecto

## Instalación

1. **Clona el repositorio en tu máquina local:**

    ```sh
    git clone https://github.com/tu-usuario/Factx-Frontend.git
    cd Factx-Frontend
    ```

2. **Instala las dependencias del proyecto con Maven:**

    Si no tienes Maven instalado, puedes seguir las instrucciones [aquí](https://maven.apache.org/install.html). Luego ejecuta:

    ```sh
    mvn install
    ```

    O si ya tienes Maven, solo ejecuta el comando:

    ```sh
    mvn clean install
    ```

3. **Compila el proyecto para obtener el archivo WAR:**

    ```sh
    mvn clean package
    ```

    Esto generará un archivo `.war` en la carpeta `target`.

## Configuración de la Aplicación

### Conexión con el SRI

El frontend interactúa con el SRI para la validación y envío de facturas electrónicas. Para configurar la conexión, asegúrate de que la API del SRI esté correctamente configurada en tu entorno y las credenciales sean válidas.

## Ejecución

1. **Inicia la aplicación:**

    Si deseas ejecutar la aplicación localmente con Tomcat embebido, puedes usar el siguiente comando:

    ```sh
    mvn spring-boot:run
    ```

2. **Accede a la aplicación:**

    Una vez que la aplicación esté en ejecución, puedes acceder a ella mediante tu navegador visitando:

    ```
    http://localhost:8080/factx
    ```

## Archivos Importantes

- **`pom.xml`:** Contiene las dependencias y configuraciones de Maven para el proyecto.
- **`src/main/resources/application.properties`:** Configuración del entorno de Spring Boot, como la base de datos y parámetros de servidor.
- **`src/main/java/com/levting/factx`:** Contiene los controladores, servicios y lógica de negocio.
- **`src/main/resources/static/`:** Archivos estáticos (CSS, JS, imágenes).
- **`src/main/resources/templates/`:** Plantillas Thymeleaf utilizadas para generar las vistas del frontend.
- **`target/`:** Carpeta que contiene el archivo `.war` generado para el despliegue en Tomcat.

## Características

- **Interfaz de usuario** interactiva desarrollada con Thymeleaf.
- **Integración con el SRI** para la validación y envío de facturas electrónicas.
- **Desplegable en Tomcat** en un entorno de producción para facturación electrónica segura.

## Dependencias

El proyecto utiliza las siguientes dependencias principales:

- **Spring Boot Starter WebFlux:** Para construir aplicaciones web reactivas.
- **Lombok:** Para facilitar la creación de clases Java con menos código boilerplate.
- **Spring Boot Starter Test:** Para realizar pruebas unitarias y de integración.
- **Thymeleaf:** Motor de plantillas para generar las vistas del frontend.

## Contribuciones

Si deseas contribuir a este proyecto, sigue los pasos para instalar y configurar el entorno, luego haz un pull request con los cambios.

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo LICENSE para más detalles.
