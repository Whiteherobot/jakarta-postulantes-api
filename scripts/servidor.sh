#!/usr/bin/env bash
# Levanta el WildFly armado con ./mvnw package -Pservidor.
#   scripts/servidor.sh          http://localhost:8080/postulantes/api
#   PUERTO=8180 scripts/servidor.sh
set -euo pipefail

raiz="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
puerto="${PUERTO:-8080}"
[[ -x "$raiz/target/server/bin/standalone.sh" ]] || { echo "Falta target/server: correr ./mvnw package -Pservidor" >&2; exit 1; }

# El servidor que arma Glow no trae el archivo jdk.serialFilter que el script busca.
export DISABLE_JDK_SERIAL_FILTER=true
exec "$raiz/target/server/bin/standalone.sh" -Djboss.socket.binding.port-offset=$((puerto - 8080))
