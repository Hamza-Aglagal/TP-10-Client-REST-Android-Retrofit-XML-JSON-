# Guide de Développement - Application REST Client Android

## Table des Matières
1. [Vue d'ensemble](#vue-densemble)
2. [Architecture de l'application](#architecture-de-lapplication)
3. [Configuration du projet](#configuration-du-projet)
4. [Composants principaux](#composants-principaux)
5. [Flux de données](#flux-de-données)
6. [Guide d'utilisation](#guide-dutilisation)
7. [Déploiement](#déploiement)

## Vue d'ensemble

Cette application Android démontre l'intégration d'un service REST pour la gestion de comptes bancaires. Elle illustre les bonnes pratiques de développement Android moderne :

- **Architecture en couches** (Presentation, Repository, API)
- **Retrofit** pour les appels réseau
- **Material Design** pour l'interface utilisateur
- **RecyclerView** pour les listes performantes
- **Support multi-format** (JSON et XML)

## Architecture de l'application

### Structure des packages

```
ma.projet.restclient/
│
├── entities/              # Couche de données
│   ├── Compte.java       # Modèle de compte bancaire
│   └── CompteList.java   # Wrapper pour liste XML
│
├── api/                  # Couche d'interface API
│   └── CompteService.java # Définition des endpoints REST
│
├── config/               # Configuration
│   └── RetrofitClient.java # Configuration Retrofit
│
├── repository/           # Couche d'accès aux données
│   └── CompteRepository.java # Gestion des opérations CRUD
│
├── adapter/              # Adaptateurs UI
│   └── CompteAdapter.java # Adaptateur RecyclerView
│
└── MainActivity.java     # Activité principale

```

### Flux de données

```
UI (MainActivity) 
    ↓
Adapter (CompteAdapter)
    ↓
Repository (CompteRepository)
    ↓
API Interface (CompteService)
    ↓
Network (Retrofit)
    ↓
REST Service
```

## Configuration du projet

### 1. Dépendances Gradle

Le fichier `app/build.gradle` contient toutes les dépendances nécessaires :

**Retrofit et convertisseurs** :
- `retrofit:2.9.0` - Client HTTP
- `converter-gson:2.9.0` - Conversion JSON
- `converter-simplexml:2.9.0` - Conversion XML

**UI Components** :
- `material:1.11.0` - Material Design
- `recyclerview:1.3.2` - Listes optimisées
- `constraintlayout:2.1.4` - Layout flexible

### 2. Permissions réseau

Dans `AndroidManifest.xml` :
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 3. Configuration de sécurité réseau

Le fichier `res/xml/network_security_config.xml` permet les connexions HTTP non chiffrées vers le serveur local de l'émulateur (`10.0.2.2`).

## Composants principaux

### 1. Entities (Modèles de données)

**Compte.java**
- Représente un compte bancaire
- Annotations pour JSON (`@SerializedName`) et XML (`@Element`)
- Attributs : id, solde, type, dateCreation

**CompteList.java**
- Wrapper pour les réponses XML contenant plusieurs comptes
- Utilise `@ElementList` pour la désérialisation

### 2. API Interface

**CompteService.java**
Définit les endpoints REST avec les annotations Retrofit :

| Méthode | Endpoint | Action |
|---------|----------|--------|
| `@GET` | `/banque/comptes` | Récupérer tous les comptes |
| `@GET` | `/banque/comptes/{id}` | Récupérer un compte par ID |
| `@POST` | `/banque/comptes` | Créer un nouveau compte |
| `@PUT` | `/banque/comptes/{id}` | Mettre à jour un compte |
| `@DELETE` | `/banque/comptes/{id}` | Supprimer un compte |

### 3. Configuration Retrofit

**RetrofitClient.java**
- Pattern Singleton pour l'instance Retrofit
- Configuration dynamique du convertisseur (JSON/XML)
- URL de base : `http://10.0.2.2:8082/`

```java
public static Retrofit getClient(String converterType) {
    if ("JSON".equals(converterType)) {
        builder.addConverterFactory(GsonConverterFactory.create());
    } else if ("XML".equals(converterType)) {
        builder.addConverterFactory(SimpleXmlConverterFactory.createNonStrict());
    }
    return retrofit;
}
```

### 4. Repository

**CompteRepository.java**
Couche d'abstraction entre l'UI et l'API :
- Gère les appels asynchrones
- Convertit les réponses XML en listes
- Gère les callbacks de succès/échec

### 5. Adapter

**CompteAdapter.java**
- Gère l'affichage des comptes dans le RecyclerView
- Implémente le pattern ViewHolder
- Interfaces de callback pour les actions (edit, delete)

### 6. MainActivity

Point d'entrée de l'application :
- Gestion du cycle de vie
- Coordination entre Repository et UI
- Gestion des dialogs d'ajout/modification
- Gestion des événements utilisateur

## Flux de données

### 1. Chargement des données

```
1. User sélectionne JSON/XML
2. MainActivity → loadData(format)
3. CompteRepository → getAllCompte()
4. CompteService → API Call
5. Retrofit → HTTP Request
6. API Response → Callback
7. CompteAdapter → updateData()
8. RecyclerView → Affichage
```

### 2. Ajout d'un compte

```
1. User clique sur FAB
2. Dialog s'affiche
3. User entre les données
4. MainActivity → addCompte()
5. CompteRepository → addCompte()
6. API POST → Serveur
7. Success → loadData() pour rafraîchir
```

### 3. Modification d'un compte

```
1. User clique sur "Edit"
2. Callback → onUpdateClick()
3. Dialog pré-rempli
4. User modifie les données
5. MainActivity → updateCompte()
6. API PUT → Serveur
7. Success → loadData() pour rafraîchir
```

### 4. Suppression d'un compte

```
1. User clique sur "Delete"
2. Callback → onDeleteClick()
3. Dialog de confirmation
4. User confirme
5. MainActivity → deleteCompte()
6. API DELETE → Serveur
7. Success → loadData() pour rafraîchir
```

## Guide d'utilisation

### Lancement de l'application

1. **Préparer le serveur REST** :
   ```bash
   # Assurez-vous que votre serveur REST est accessible sur le port 8082
   # L'endpoint doit être : http://localhost:8082/banque/comptes
   ```

2. **Ouvrir le projet dans Android Studio** :
   - File → Open → Sélectionner le dossier
   - Attendre la synchronisation Gradle

3. **Configurer l'émulateur** :
   - Tools → AVD Manager
   - Créer un nouvel émulateur ou utiliser un existant
   - API Level 24 minimum recommandé

4. **Lancer l'application** :
   - Run → Run 'app' (Shift + F10)
   - Sélectionner l'émulateur ou l'appareil

### Tests fonctionnels

#### Test 1 : Affichage des comptes
- ✅ Lancer l'app → Vérifier l'affichage de la liste
- ✅ Basculer JSON/XML → Vérifier le rafraîchissement

#### Test 2 : Ajout de compte
- ✅ Cliquer sur le FAB (+)
- ✅ Entrer un solde (ex: 5000)
- ✅ Sélectionner un type (COURANT/EPARGNE)
- ✅ Cliquer "Ajouter"
- ✅ Vérifier l'apparition dans la liste

#### Test 3 : Modification de compte
- ✅ Cliquer sur "Edit" d'un compte
- ✅ Modifier le solde
- ✅ Changer le type
- ✅ Cliquer "Modifier"
- ✅ Vérifier la mise à jour

#### Test 4 : Suppression de compte
- ✅ Cliquer sur "Delete" d'un compte
- ✅ Confirmer la suppression
- ✅ Vérifier la disparition de la liste

## Déploiement

### Build de l'APK

1. **Build Debug APK** :
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```
   Fichier généré : `app/build/outputs/apk/debug/app-debug.apk`

2. **Build Release APK** :
   ```
   Build → Generate Signed Bundle / APK
   → APK → Create new keystore
   → Remplir les informations
   → Build
   ```

### Installation sur un appareil

1. **Via USB** :
   ```bash
   adb install app-debug.apk
   ```

2. **Via fichier** :
   - Transférer l'APK sur l'appareil
   - Ouvrir avec le gestionnaire de fichiers
   - Autoriser l'installation depuis des sources inconnues

## Dépannage

### Problème : "Unable to resolve host"
**Solution** : Vérifier que le serveur REST est en cours d'exécution et accessible

### Problème : "cleartext traffic not permitted"
**Solution** : Vérifier la configuration `network_security_config.xml`

### Problème : "Failed to parse XML/JSON"
**Solution** : 
- Vérifier les annotations dans les classes Entity
- Consulter les logs Logcat pour plus de détails
- Vérifier le format de réponse du serveur

### Problème : RecyclerView ne s'affiche pas
**Solution** :
- Vérifier que l'adaptateur est bien configuré
- Vérifier que les données sont bien reçues (logs)
- Vérifier les contraintes du layout

## Bonnes Pratiques

1. **Gestion des erreurs** :
   - Toujours gérer les cas `onFailure()`
   - Afficher des messages utilisateur clairs
   - Logger les erreurs pour le débogage

2. **Performance** :
   - Utiliser `notifyItemChanged()` au lieu de `notifyDataSetChanged()` pour les mises à jour individuelles
   - Éviter les appels réseau dans le thread principal
   - Mettre en cache les données si possible

3. **Sécurité** :
   - Utiliser HTTPS en production
   - Ne jamais stocker de données sensibles en clair
   - Valider toutes les entrées utilisateur

4. **UX** :
   - Afficher des indicateurs de chargement
   - Gérer les états vides (liste vide)
   - Fournir un feedback immédiat sur les actions

## Ressources supplémentaires

- [Documentation Retrofit](https://square.github.io/retrofit/)
- [Material Design Guidelines](https://material.io/design)
- [Android RecyclerView Guide](https://developer.android.com/guide/topics/ui/layout/recyclerview)
- [REST API Best Practices](https://restfulapi.net/)

---

**Auteur** : Développé dans le cadre d'un projet académique  
**Date** : Novembre 2024  
**Version** : 1.0
