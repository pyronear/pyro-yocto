#!/bin/sh

# ==============================================================================
# Script de renouvellement automatique du Token PyroNear
# ==============================================================================

ENV_FILE=".env"
CREDS_FILE="data/credentials.json"

echo "=== Début du renouvellement du token PyroNear ==="

# 1. Charger les variables de manière sécurisée (gère les caractères spéciaux)
if [ -f "$ENV_FILE" ]; then
    set -a
    . "./$ENV_FILE"
    set +a
else
    echo "Erreur : fichier $ENV_FILE introuvable."
    exit 1
fi

# Construction de l'URL de l'API (basée sur le script Python d'origine)
API_BASE="${API_URL}/api/v1"

echo "Connexion à l'API ($API_BASE) avec le compte SuperAdmin..."

# 2. Récupérer le token Administrateur
echo "Envoi de la requ  te d'authentification vers /login/creds..."
RAW_RESPONSE=$(curl -s -X POST "${API_URL}/api/v1/login/creds" \
    --data-urlencode "username=$SUPERADMIN_LOGIN" \
    --data-urlencode "password=$SUPERADMIN_PWD")

echo "R  ponse brute du serveur : $RAW_RESPONSE"

ADMIN_TOKEN=$(echo $RAW_RESPONSE | jq -r .access_token)


# Vérification de sécurité
if [ "$ADMIN_TOKEN" = "null" ] || [ -z "$ADMIN_TOKEN" ]; then
    echo "Erreur : Identifiants invalides ou API injoignable."
    exit 1
fi

# 3. Extraire la clé principale et l'ID de la caméra depuis credentials.json
if [ ! -f "$CREDS_FILE" ]; then
    echo "Erreur : fichier $CREDS_FILE introuvable."
    exit 1
fi

CAMERA_KEY=$(jq -r 'keys[0]' "$CREDS_FILE")
CAMERA_ID=$(jq -r ".\"$CAMERA_KEY\".id" "$CREDS_FILE")

echo "Caméra détectée : $CAMERA_KEY (ID: $CAMERA_ID)"
echo "Demande d'un nouveau token..."

# 4. Demander le token spécifique pour cette caméra
CAMERA_TOKEN=$(curl -s -X POST "$API_BASE/cameras/$CAMERA_ID/token" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r .access_token)

if [ "$CAMERA_TOKEN" = "null" ] || [ -z "$CAMERA_TOKEN" ]; then
    echo "Erreur : Impossible de générer le token pour la caméra $CAMERA_ID."
    exit 1
fi

# 5. Mettre à jour le fichier credentials.json avec le nouveau token
# On écrit dans un fichier temporaire puis on écrase l'ancien pour éviter toute corruption
jq ".\"$CAMERA_KEY\".token = \"$CAMERA_TOKEN\"" "$CREDS_FILE" > data/tmp.json && mv data/tmp.json "$CREDS_FILE"

echo "Succès ! Le fichier $CREDS_FILE a été mis à jour avec le nouveau token."