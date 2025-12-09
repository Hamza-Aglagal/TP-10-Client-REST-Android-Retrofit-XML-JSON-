# 🧪 Guide de Test - API REST

## Test du Serveur REST avec Postman/cURL

Avant de lancer l'application Android, testez votre serveur REST pour vous assurer qu'il fonctionne correctement.

## 📝 Prérequis

- Serveur REST en cours d'exécution sur `http://localhost:8082`
- Postman installé (ou utiliser cURL)

## 🔍 Tests des Endpoints

### 1. GET - Récupérer tous les comptes (JSON)

**Request:**
```http
GET http://localhost:8082/banque/comptes
Accept: application/json
```

**cURL:**
```bash
curl -X GET "http://localhost:8082/banque/comptes" -H "Accept: application/json"
```

**Réponse attendue (200 OK):**
```json
[
  {
    "id": 1,
    "solde": 7113.18,
    "type": "COURANT",
    "dateCreation": "2024-11-14"
  },
  {
    "id": 2,
    "solde": 8908.20,
    "type": "EPARGNE",
    "dateCreation": "2024-11-14"
  }
]
```

---

### 2. GET - Récupérer tous les comptes (XML)

**Request:**
```http
GET http://localhost:8082/banque/comptes
Accept: application/xml
```

**cURL:**
```bash
curl -X GET "http://localhost:8082/banque/comptes" -H "Accept: application/xml"
```

**Réponse attendue (200 OK):**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<List>
    <item>
        <id>1</id>
        <solde>7113.18</solde>
        <type>COURANT</type>
        <dateCreation>2024-11-14</dateCreation>
    </item>
    <item>
        <id>2</id>
        <solde>8908.20</solde>
        <type>EPARGNE</type>
        <dateCreation>2024-11-14</dateCreation>
    </item>
</List>
```

---

### 3. GET - Récupérer un compte par ID

**Request:**
```http
GET http://localhost:8082/banque/comptes/1
Accept: application/json
```

**cURL:**
```bash
curl -X GET "http://localhost:8082/banque/comptes/1" -H "Accept: application/json"
```

**Réponse attendue (200 OK):**
```json
{
  "id": 1,
  "solde": 7113.18,
  "type": "COURANT",
  "dateCreation": "2024-11-14"
}
```

---

### 4. POST - Créer un nouveau compte

**Request:**
```http
POST http://localhost:8082/banque/comptes
Content-Type: application/json

{
  "solde": 5000.00,
  "type": "EPARGNE",
  "dateCreation": "2024-11-14"
}
```

**cURL:**
```bash
curl -X POST "http://localhost:8082/banque/comptes" \
  -H "Content-Type: application/json" \
  -d '{
    "solde": 5000.00,
    "type": "EPARGNE",
    "dateCreation": "2024-11-14"
  }'
```

**Réponse attendue (201 Created):**
```json
{
  "id": 3,
  "solde": 5000.00,
  "type": "EPARGNE",
  "dateCreation": "2024-11-14"
}
```

---

### 5. PUT - Mettre à jour un compte

**Request:**
```http
PUT http://localhost:8082/banque/comptes/1
Content-Type: application/json

{
  "id": 1,
  "solde": 8000.00,
  "type": "COURANT",
  "dateCreation": "2024-11-14"
}
```

**cURL:**
```bash
curl -X PUT "http://localhost:8082/banque/comptes/1" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "solde": 8000.00,
    "type": "COURANT",
    "dateCreation": "2024-11-14"
  }'
```

**Réponse attendue (200 OK):**
```json
{
  "id": 1,
  "solde": 8000.00,
  "type": "COURANT",
  "dateCreation": "2024-11-14"
}
```

---

### 6. DELETE - Supprimer un compte

**Request:**
```http
DELETE http://localhost:8082/banque/comptes/3
```

**cURL:**
```bash
curl -X DELETE "http://localhost:8082/banque/comptes/3"
```

**Réponse attendue (200 OK ou 204 No Content)**

---

## 🧪 Collection Postman

Créez une collection Postman avec ces tests :

1. Ouvrir Postman
2. Créer une nouvelle Collection : "REST Client Tests"
3. Ajouter les 6 requêtes ci-dessus
4. Sauvegarder la collection

### Variables d'environnement Postman

Créez un environnement "REST Client - Local" :

```json
{
  "baseUrl": "http://localhost:8082",
  "contentType": "application/json"
}
```

Utilisation dans les requêtes :
```
{{baseUrl}}/banque/comptes
```

---

## ✅ Checklist de Test

Avant de lancer l'application Android, vérifiez que :

- [ ] Le serveur REST démarre sans erreur
- [ ] GET /banque/comptes retourne une liste (JSON)
- [ ] GET /banque/comptes retourne une liste (XML)
- [ ] GET /banque/comptes/{id} retourne un compte
- [ ] POST /banque/comptes crée un nouveau compte
- [ ] PUT /banque/comptes/{id} met à jour un compte
- [ ] DELETE /banque/comptes/{id} supprime un compte
- [ ] Les codes de statut HTTP sont corrects (200, 201, 204, etc.)
- [ ] Les formats JSON et XML sont valides

---

## 🐛 Codes d'Erreur Courants

### 404 Not Found
- Endpoint incorrect
- Serveur non démarré
- Route non configurée

### 400 Bad Request
- JSON mal formé
- Champs obligatoires manquants
- Type de données incorrect

### 500 Internal Server Error
- Erreur côté serveur
- Base de données inaccessible
- Exception non gérée

---

## 📊 Tests de Performance

### Test de Charge (avec Apache Bench)

```bash
# 100 requêtes, 10 en parallèle
ab -n 100 -c 10 http://localhost:8082/banque/comptes
```

### Temps de Réponse Attendu

| Endpoint | Temps (ms) |
|----------|-----------|
| GET /comptes | < 100 |
| GET /comptes/{id} | < 50 |
| POST /comptes | < 200 |
| PUT /comptes/{id} | < 200 |
| DELETE /comptes/{id} | < 100 |

---

## 🔍 Débogage

### Activer les logs Retrofit dans l'app Android

Ajoutez dans `RetrofitClient.java` :

```java
HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
logging.setLevel(HttpLoggingInterceptor.Level.BODY);

OkHttpClient client = new OkHttpClient.Builder()
    .addInterceptor(logging)
    .build();

Retrofit.Builder builder = new Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client);
```

### Voir les requêtes dans Logcat

```
Filter: "OkHttp"
```

---

## 📝 Exemple de Données de Test

### Comptes pour les tests

```json
[
  {
    "solde": 7113.18,
    "type": "COURANT",
    "dateCreation": "2024-11-14"
  },
  {
    "solde": 8908.20,
    "type": "EPARGNE",
    "dateCreation": "2024-11-14"
  },
  {
    "solde": 2399.85,
    "type": "COURANT",
    "dateCreation": "2024-11-14"
  }
]
```

### Types de compte valides
- `COURANT`
- `EPARGNE`

### Validation des données
- `solde` : nombre décimal positif
- `type` : COURANT ou EPARGNE
- `dateCreation` : format ISO 8601 (YYYY-MM-DD)

---

## 🚀 Tests Automatisés (Optionnel)

### JUnit Test (Java)

```java
@Test
public void testGetAllComptes() {
    // Arrange
    CompteRepository repo = new CompteRepository("JSON");
    
    // Act & Assert
    repo.getAllCompte(new Callback<List<Compte>>() {
        @Override
        public void onResponse(Call<List<Compte>> call, Response<List<Compte>> response) {
            assertTrue(response.isSuccessful());
            assertNotNull(response.body());
            assertTrue(response.body().size() > 0);
        }
        
        @Override
        public void onFailure(Call<List<Compte>> call, Throwable t) {
            fail("Request should not fail");
        }
    });
}
```

---

## 📚 Ressources

- [Postman Documentation](https://learning.postman.com/)
- [cURL Manual](https://curl.se/docs/manual.html)
- [HTTP Status Codes](https://httpstatuses.com/)
- [REST API Testing](https://www.softwaretestinghelp.com/api-testing-tutorial/)

---

**Note** : Assurez-vous que tous ces tests passent avant de lancer l'application Android pour garantir une expérience utilisateur optimale.
