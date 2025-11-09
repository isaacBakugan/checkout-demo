CHECKOUT BACKEND DEMO BY ISAAC OLIVARES

🧩 1️⃣ Ejecución en entorno local
🔧 Requisitos previos

Java 17 (Temurin)

Maven 3.9+

Docker Desktop en ejecución (para usar H2 o MailHog opcional)

Puerto 8080 libre

# Clonar el repositorio
git clone https://github.com/isaacBakugan/checkout-demo.git
cd checkout-demo

# Compilar y empaquetar
mvn clean package -DskipTests

# Ejecutar localmente
java -jar target/checkout-0.0.1-SNAPSHOT.jar

🌐 Endpoints locales

App: http://localhost:8080

Consola H2: http://localhost:8080/h2-console

Driver: org.h2.Driver

JDBC URL: jdbc:h2:mem:testdb

User: sa

Password: (vacío)

☁️ 2️⃣ Despliegue en Google Cloud Run
🔧 Requisitos

Proyecto GCP activo

Artifact Registry configurado

Cloud Run habilitado

SDK de Google Cloud autenticado

# Compilar sin tests
mvn clean package -DskipTests

# Autenticar docker con Artifact Registry
gcloud auth configure-docker europe-west1-docker.pkg.dev

# Construir imagen
docker build -t europe-west1-docker.pkg.dev/<TU_PROYECTO>/app-repo/checkout-app:v1 .

# Subir imagen
docker push europe-west1-docker.pkg.dev/<TU_PROYECTO>/app-repo/checkout-app:v1

# Desplegar a Cloud Run
gcloud run deploy checkout-api \
  --image=europe-west1-docker.pkg.dev/<TU_PROYECTO>/app-repo/checkout-app:v1 \
  --allow-unauthenticated \
  --region=europe-west1 \
  --port=8080 \
  --cpu=1 \
  --memory=512Mi \
  --min-instances=0 \
  --max-instances=2 \
  --timeout=600s \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod,APP_SEED_PRODUCTS=true,API_KEY_DEV=<TU_API_KEY>,APP_CRYPTO_KEY=<TU_CLAVE_AES_BASE64>"

🔑 Variables de entorno requeridas
Variable	Descripción
SPRING_PROFILES_ACTIVE	Perfil de ejecución (usar prod para despliegue)
APP_SEED_PRODUCTS	true para insertar productos automáticamente
API_KEY_DEV	API key de autenticación para el entorno cloud
APP_CRYPTO_KEY	Clave AES Base64 para encriptación de tarjetas

🌍 3️⃣ Acceso al despliegue
App desplegada en Cloud Run

https://checkout-api-858025381397.europe-west1.run.app

Consola H2 (modo lectura)

https://checkout-api-858025381397.europe-west1.run.app/h2-console

Credenciales:
Driver: org.h2.Driver
JDBC URL: jdbc:h2:mem:testdb
User: sa
Password: (vacío)

🧠 Notas importantes

El proyecto utiliza Spring Boot 3 + Java 17 + Docker + GCP Cloud Run.

La base de datos en producción es H2 en memoria, reiniciada en cada despliegue.

El DataSeeder crea 5 productos iniciales al iniciar la app cuando APP_SEED_PRODUCTS=true.

Toda la comunicación requiere el header X-API-KEY.

Las tarjetas se tokenizan y almacenan cifradas usando AES-256.

La consola H2 se deja abierta únicamente con propósito de revisión técnica.

✅ Flujo de prueba recomendado

Ingresa la api key proporcionada

Registra un usuario e inicia sesión

Ingresar a /h2-console para verificar productos iniciales.

Tokenizar una tarjeta (sección "Tokenización").

Buscar y agregar productos al carrito.

Completar datos en “Pedido y Pago” y procesar pago.

Revisar auditoría y logs por UUID de transacción.