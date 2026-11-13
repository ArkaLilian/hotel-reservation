# hotel-reservation

Projet final du cours de Programmation Orientée Objet en Java (M1, JUNIA ISEN 4).
Lilian Clabecq

## Objectif de l'application

hotel-reservation est une application console qui permet de gérer les réservations d'un petit hôtel. 
Au démarrage, elle charge les chambres, les clients et les réservations existantes depuis trois fichiers CSV. 
Ensuite, un menu permet de chercher les chambres libres, de réserver une chambre, d'annuler une réservation et de voir les réservations d'un client.

L'application détecte les conflits de dates : une chambre ne peut pas être réservée deux fois sur des dates qui se chevauchent. Elle calcule aussi le prix d'un séjour selon le type de chambre, et elle applique une réduction de 10 % à partir de 7 nuits. Enfin, elle vérifie ce que l'utilisateur demande : dates incohérentes, chambre inconnue, trop de personnes pour la chambre, réservation dans le passé, etc.


## Arborescence du projet

```
hotel-reservation/
├── data/
│   ├── rooms.csv
│   ├── customers.csv
│   └── reservations.csv
├── src/main/java/org/example/
│   ├── Main.java
│   ├── ConsoleMenu.java
│   ├── model/       Room, StandardRoom, Suite, Customer, Reservation, ReservationStatus
│   ├── service/     PricingRule, LongStayDiscount, PricingService, ReservationService
│   ├── exception/   RoomNotAvailableException, ReservationNotFoundException
│   └── utils/       RoomFactory, CsvLoader, DateFormats
└── src/test/java/org/example/
    ├── model/       ReservationTest
    ├── service/     PricingServiceTest, ReservationServiceTest
    └── utils/       RoomFactoryTest
```


## Comment lancer l'application

```
mvn compile
java -cp target/classes org.example.Main
```

Dans IntelliJ, il suffit d'ouvrir le projet, puis de lancer `Main` avec la flèche verte en haut de l'app.

L'application lit ses fichiers avec un chemin relatif (`data/rooms.csv`). Il faut donc la lancer depuis la racine du projet. Sinon, l'application ne trouve pas les fichiers, affiche un message clair (sans trace d'erreur Java) et s'arrête : "Impossible de lire le fichier data\rooms.csv : lancez l'application depuis la racine du projet (dossier data/)."


## Fonctionnalités principales

Au démarrage, l'application affiche ce qu'elle a chargé, par exemple « 5 chambres, 2 clients et 3 réservations chargés. ». Si des lignes des CSV sont fausses, elle les signale juste en dessous. Ensuite, le menu s'affiche :


```
===== Hôtel : réservations =====
1. Lister toutes les chambres
2. Chercher les chambres libres
3. Réserver une chambre
4. Annuler une réservation
5. Voir les réservations d'un client
0. Quitter
```

Pour choisir un client, l'application affiche d'abord la liste des clients avec leur id, car sinon l'utilisateur ne peut pas le deviner.

Les dates se tapent et s'affichent au format JJ/MM/AAAA, par exemple `23/12/2026`. Le jour et le mois s'écrivent sur deux chiffres (`01/12/2026` et pas `1/12/2026`). Une date qui n'existe pas, comme `31/02/2026`, est refusée.

L'application ne plante jamais sur une mauvaise saisie. Si on tape `abc` à la place d'un nombre, ou `45/13/2026` à la place d'une date, la question est simplement reposée. Si une action est refusée (chambre déjà prise, réservation introuvable…), un message « Erreur : … » explique pourquoi et le menu revient.

## Règles métier

- La date de départ est strictement après la date d'arrivée (au moins une nuit). `IllegalArgumentException` 
- On ne peut pas réserver dans le passé. Une arrivée le jour même est acceptée. `IllegalArgumentException` 
- Le nombre de personnes ne dépasse pas la capacité de la chambre. `IllegalArgumentException` 
- La chambre demandée doit exister. `IllegalArgumentException`
- Une chambre ne peut pas avoir deux réservations confirmées qui se chevauchent. `RoomNotAvailableException` 
- Une réservation annulée ne bloque plus la chambre.
- On ne peut pas annuler une réservation qui n'existe pas. `ReservationNotFoundException` 
- On ne peut pas annuler deux fois la même réservation. `IllegalStateException`
- Un séjour de 7 nuits ou plus a 10 % de réduction.
- Une suite coûte son prix de base plus 30 € par nuit.

Pour détecter un conflit, la méthode `overlaps(start, end)` de `Reservation` renvoie `start.isBefore(checkOut) && end.isAfter(checkIn)`. Les comparaisons sont strictes, donc deux séjours bord à bord (du 20 au 24 et du 24 au 27) ne sont pas un conflit. En effet, le premier client part le matin du 24 et le second arrive l'après-midi du 24 aussi. Donc un départ et une arrivée le même jour dans la même chambre sont possibles.

## Classes principales

### Package `model`

`Room` (abstraite) : Ce que toutes les chambres ont en commun : numéro, capacité, prix de base, `canHost(guests)`. `equals()` et `hashCode()` sont basés sur le numéro de chambre.  
`StandardRoom` : Chambre classique : son prix par nuit est le prix de base.  
`Suite` : Suite : son prix par nuit est le prix de base plus un supplément fixe de 30 €.  
`Customer` : Un client : id, prénom, nom, email. `equals()` et `hashCode()` sont basés sur l'id.  
`Reservation` : Une réservation : client, chambre, dates, nombre de personnes et statut. Elle sait calculer son nombre de nuits, dire si elle chevauche une période et s'annuler.  
`ReservationStatus` (enum) : `CONFIRMED` ou `CANCELLED`.

`Customer` et `Reservation` vérifient leurs données dans le constructeur (id positif, pas de `null`, départ après l'arrivée) et lancent une `IllegalArgumentException` sinon. Tous les champs sont `final`, sauf le statut d'une réservation, qui change quand on l'annule.

### Package `service`

Classes et leur rôle

`PricingRule` (interface) : Une règle de prix : `apply(Reservation, double currentPrice)`. 
`LongStayDiscount` : La réduction de 10 % à partir de 7 nuits. 
`PricingService` : Calcule le prix : prix par nuit × nombre de nuits, puis applique les règles. 
`ReservationService` : Réserver, annuler, chercher les chambres libres, lister les réservations d'un client. 

`ReservationService` n'affiche rien : il renvoie des valeurs ou lance des exceptions. Ainsi, il est facile à tester.

### Package `exception`

Classes et leur rôle

`RoomNotAvailableException` : La chambre est déjà prise sur ces dates. 
`ReservationNotFoundException` : Aucune réservation n'a ce numéro. 

### Package `utils`

Classes et leur rôle

`RoomFactory` : Crée une `StandardRoom` ou une `Suite` selon le type lu dans le CSV. 
`CsvLoader` : Lit les trois fichiers CSV et les transforme en objets. 
`DateFormats` : Le format des dates à l'écran (JJ/MM/AAAA). 

### Package `org.example`

Classes et leur rôle

`Main` : Charge les CSV, crée les services et lance le menu. 
`ConsoleMenu` : La seule classe qui lit le clavier et affiche les résultats. 

## Décisions techniques

### 1. Une ligne de CSV fausse est ignorée et signalée

Je pouvais tout arrêter ou ignorer la ligne. J'ai choisi de l'ignorer et d'afficher un avertissement au démarrage, avec le nom du fichier et le numéro de ligne. Ainsi, une seule faute de frappe ne bloque pas toute l'application.

### 2. Les prix sont des `double`

Avec des `int`, la réduction de 10 % serait tronquée (85,5 deviendrait 85). `BigDecimal` serait plus précis, mais on ne l'a pas vu en cours. Les prix sont arrondis à 2 décimales seulement à l'affichage.

### 3. Une réservation dans le passé est refusée par `ReservationService`

La règle est dans `book` et pas dans le constructeur de `Reservation`, car `reservations.csv` contient des séjours passés qui doivent continuer à se charger. Une arrivée le jour même est acceptée.

### 4. Annuler deux fois est une erreur

`cancel()` lance une `IllegalStateException` si la réservation est déjà annulée. J'ai préféré signaler l'erreur plutôt que de ne rien faire.

### 5. Rien n'est sauvegardé

Les CSV sont seulement lus, jamais réécrits. Ainsi, chaque lancement repart des mêmes données et les essais ne modifient pas les fichiers. Le sujet demande seulement de charger les données.

## Où est l'héritage

`StandardRoom` et `Suite` héritent de la classe abstraite `Room`. Elles gardent ses données et ses méthodes, et redéfinissent seulement `getNightlyPrice()` et `getType()`.

## Où est le polymorphisme

- `PricingService` appelle `getNightlyPrice()` sur une `Room` sans savoir si c'est une `StandardRoom` ou une `Suite` : c'est le vrai type de l'objet qui donne le prix.
- `PricingService` appelle `apply(...)` sur chaque `PricingRule` de sa liste sans connaître sa classe.

## Classe abstraite et interface

`Room` est une classe abstraite, car toutes les chambres ont des données et du code en commun. `PricingRule` est une interface, car une règle de prix est seulement un comportement (pattern Strategy vu en cours).

## Exceptions

J'ai créé deux exceptions vérifiées : `RoomNotAvailableException` et `ReservationNotFoundException`. Elles sont vérifiées, car ce sont des cas normaux et pas des bugs : un client peut demander une chambre déjà prise. Ainsi, le menu est obligé de les traiter.

Une demande invalide (dates inversées, trop de personnes, chambre inconnue) donne une `IllegalArgumentException`. Le menu attrape toutes ces erreurs, affiche le message et revient au menu.

## Chargement des données

Au démarrage, `CsvLoader` lit `rooms.csv`, `customers.csv` puis `reservations.csv` dans le dossier `data/` avec `Files.readAllLines`. Les valeurs sont séparées par des points-virgules, par exemple :

```
type;number;capacity;basePrice
STANDARD;101;2;80.0
SUITE;201;4;180.0
```

Lees réservations sont chargées en dernier, car elles contiennent seulement l'id du client et le numéro de la chambre. Les chambres sont créées par `RoomFactory` selon leur type. Si un fichier manque, `Main` affiche un message clair et l'application s'arrête.

## SOLID

- **S** : chaque classe a un seul rôle (`ConsoleMenu` affiche, `CsvLoader` lit, `ReservationService` réserve, `PricingService` calcule).
- **O** : pour ajouter une règle de prix, il suffit d'ajouter une classe qui implémente `PricingRule`, sans modifier `PricingService`.
- **L** : une `StandardRoom` ou une `Suite` peut remplacer une `Room` partout sans rien casser.
- **I** : `PricingRule` n'a qu'une seule méthode.
- **D** : les services reçoivent leurs dépendances par le constructeur, et tout est créé dans `Main`.

## Lancer les tests

```bash
mvn test
```

Dans IntelliJ : clic droit sur `src/test/java` puis Run 'All Tests'.

Il y a 43 tests JUnit 5, répartis dans `ReservationTest`, `PricingServiceTest`, `ReservationServiceTest` et `RoomFactoryTest`. Ils vérifient les cas normaux, les cas limites (7 nuits pile, séjours bord à bord) et les erreurs.