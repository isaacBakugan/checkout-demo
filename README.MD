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

🧩 Módulos importantes (lógicos)

security

ApiKeyFilter, SecurityConfig

Protege endpoints con X-API-KEY; excluye /h2-console/**.

security/crypto

AesGcm, CryptoConfig

Cifrado AES-GCM (PAN/CVV), clave en APP_CRYPTO_KEY (Base64).

tokenization

TokenizationController, TokenizationService, CardToken, CardTokenRepository

Luhn, marca, probabilidad de rechazo, token único persistido.

customers 

Registro/validación de email/phone únicos.

products

Product, ProductRepository, ProductController

Búsqueda por q, filtro por stock mínimo (minStockToShow).

cart

CartItem, CartItemRepository, CartService, CartController

Carrito por sesión (X-SESSION-ID), add/list.

orders & payments

Order, OrderItem, OrderRepository, OrderService, OrderController, PaymentSimulator

Crea pedido desde carrito, simula pago con probabilidad + N reintentos, descuenta stock, limpia carrito.

notifications

EmailService via JavaMailSender

MailHog en dev (SMTP 1025), SMTP real en prod (variables SMTP_USER/PASS).

audit/logs

AuditEvent, AuditEventRepository, AuditService

Registra eventos con txId de MDC. Índice por transaction_id.

bootstrap

DataSeeder

Carga 5 productos si app.seed.products=true y perfil permitido.

🛠 Infra & Delivery

Contenedor: Docker (Temurin 17, JAR fat).

Local: Docker Compose (app + MailHog opcional) u java -jar.

DB: H2 en memoria (dev/demo). Opcional Cloud SQL Postgres en despliegue serio.

CI/CD: GitHub Actions (build, test, Docker build/push).

GCP: Artifact Registry, Cloud Run, Secret Manager (keys), Cloud Logging.

Config: application.yml + env vars (APP_CRYPTO_KEY, API_KEY_DEV, APP_BUSINESS_*, APP_SEED_PRODUCTS).

🗺️ Diagrama 1 — Despliegue (Local vs Cloud)
```mermaid```
flowchart LR
  subgraph Dev_Local["Local (dev)"]
    UI["Front estático\n(index.html + JS)"] -->|HTTP 8080| App["Spring Boot 3 (Monolito)"]
    App --> H2["H2 (mem:testdb)"]
    App --> Mailhog["MailHog SMTP:1025 / UI:8025"]
  end

  subgraph GCP["Google Cloud (prod/demo)"]
    UI2["Cliente Web"] -->|HTTPS| CR["Cloud Run: checkout-api"]
    CR --> H2p["H2 (in-memory)\n*demo*"]:::warn
    CR -->|SMTP| SMTP["Proveedor SMTP\n(Gmail/SES/otro)"]
    CR --> SM["Secret Manager\n(APP_CRYPTO_KEY, SMTP creds)"]
    CR --> AR["Artifact Registry\nDocker Image"]
  end

  classDef warn fill:#fff3cd,stroke:#d39e00,color:#4b4b4b;
```mermaid```

🧱 Diagrama 2 — Contenedores/Capas internas
```mermaid```
flowchart TB
  Client["Cliente Web (HTML/JS)"]

  subgraph App["Spring Boot Monolito"]
    Sec["security\nApiKeyFilter / SecurityConfig"]
    Crypto["security.crypto\nAesGcm/CryptoConfig"]
    Tok["tokenization\nController+Service+Repo"]
    Prod["products\nController+Repo"]
    Cart["cart\nController+Service+Repo"]
    Ord["orders/payments\nController+Service+Repo\nPaymentSimulator"]
    Mail["notifications\nEmailService (JavaMailSender)"]
    Audit["audit\nAuditService+Repo"]
    Boot["bootstrap\nDataSeeder"]

    Sec --> Tok
    Sec --> Prod
    Sec --> Cart
    Sec --> Ord

    Tok --> Crypto
    Ord --> Tok
    Ord --> Mail
    Ord --> Prod
    Tok --> Audit
    Ord --> Audit
    Prod --> Audit
    Cart --> Audit
    Boot --> Prod
  end

  DB["H2 / Postgres"]:::db
  SMTP["SMTP (MailHog/Gmail/SES)"]:::ext

  App <-- JPA --> DB
  Mail --> SMTP

  Client -->|/products, /cart, /tokenize, /orders| App

  classDef db fill:#e8f4ff,stroke:#1976d2,color:#0d47a1;
  classDef ext fill:#f3e5f5,stroke:#7b1fa2,color:#4a148c;
```mermaid```
  🔁 Diagrama 3 — Secuencia de Checkout con reintentos y auditoría
```mermaid```
sequenceDiagram
  participant UI as Front (JS)
  participant API as API (Spring Boot)
  participant TOK as TokenizationService
  participant CRT as CartService
  participant ORD as OrderService
  participant PAY as PaymentSimulator
  participant AUD as AuditService
  participant DB as DB (H2/SQL)
  participant SMTP as SMTP

  UI->>API: POST /tokenize {PAN, CVV, exp,...}
  API->>TOK: tokenize()
  TOK->>DB: save(CardToken cifrado)
  TOK->>AUD: log(tokenization.create_token)
  API-->>UI: {token, last4, brand}

  UI->>API: GET /products?q=
  API->>DB: query Products (stock>=min)
  API-->>UI: lista filtrada

  UI->>API: POST /cart/{sku} (X-SESSION-ID)
  API->>CRT: addToCart()
  CRT->>DB: save(CartItem)

  UI->>API: POST /orders {name,email,address,token} (X-SESSION-ID)
  API->>ORD: createFromCart()
  ORD->>DB: read CartItems
  ORD->>DB: check CardToken
  ORD->>DB: save(Order+Items status NEW)
  loop retries 0..N
    ORD->>PAY: pay(token, amount)
    PAY-->>ORD: ok/fail
  end
  alt ok
    ORD->>DB: update Order -> PAID
    ORD->>DB: update Products (stock -= qty)
    ORD->>DB: delete CartItems
    ORD->>SMTP: send success email
    ORD->>AUD: log(orders.payment_success)
  else fail
    ORD->>DB: update Order -> DECLINED
    ORD->>SMTP: send declined email
    ORD->>AUD: log(orders.payment_failed)
  end
  API-->>UI: {orderId, status, amount}
```mermaid```

