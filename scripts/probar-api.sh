#!/usr/bin/env bash
# Prueba la API desplegada de punta a punta con curl. Necesita una base recién
# creada (los datos de ejemplo sin cambios).
#   scripts/probar-api.sh [http://localhost:8080/postulantes/api]
set -uo pipefail

api="${1:-http://localhost:8080/postulantes/api}/postulantes"
fallas=0

# pedir MÉTODO RUTA [CUERPO] -> deja el código en $codigo y la respuesta en $cuerpo
pedir() {
  local salida
  salida=$(curl -s -w '\n%{http_code}' -X "$1" "$api$2" -H 'Content-Type: application/json' ${3:+-d "$3"})
  codigo=${salida##*$'\n'}
  cuerpo=${salida%$'\n'*}
}

esperar() {
  local descripcion=$1 codigo_esperado=$2 texto=$3
  if [[ "$codigo" == "$codigo_esperado" && "$cuerpo" == *"$texto"* ]]; then
    echo "ok     $descripcion"
  else
    echo "FALLA  $descripcion: $codigo $cuerpo"
    fallas=$((fallas + 1))
  fi
}

pedir GET ""
esperar "lista los ejemplos, el más reciente primero" 200 '[{"cedula":"1712345675"'

pedir GET "?estado=ENTREVISTA&q=paula"
esperar "filtra por estado y texto" 200 '"nombres":"Paula Sarmiento"'

pedir POST "" '{"cedula":"0912345675","nombres":"  Carla  Mora","correo":"CMORA@correo.ec","puesto":"Soporte técnico","experiencia":4}'
esperar "crea como POSTULADO y normaliza" 201 '"estado":"POSTULADO"'
[[ "$cuerpo" == *'"nombres":"Carla Mora"'* && "$cuerpo" == *'"correo":"cmora@correo.ec"'* ]] || { echo "FALLA  normalización: $cuerpo"; fallas=$((fallas + 1)); }

pedir POST "" '{"cedula":"0102030400","nombres":"X","correo":"x","puesto":"Analista de datos","experiencia":1}'
esperar "400 con errores por campo" 400 '"cedula":"Esta persona ya se postuló a ese puesto."'

pedir PUT "/2/estado" '{"estado":"CONTRATADO"}'
esperar "409 en una transición no permitida" 409 'No se puede pasar de Postulado a Contratado.'

pedir PUT "/2/estado" '{"estado":"ENTREVISTA"}'
esperar "avanza a entrevista" 200 '"estado":"ENTREVISTA"'

pedir PUT "/2/estado" '{"estado":"FOO"}'
esperar "400 con un estado que no existe" 400 'no es válido'

pedir PUT "/1" '{"cedula":"0102030400","nombres":"María José Ordóñez Vera","correo":"mj.ordonez@correo.ec","puesto":"Analista de datos","experiencia":4}'
esperar "edita sin tocar el estado" 200 '"estado":"ENTREVISTA"'

pedir DELETE "/4"
esperar "elimina" 204 ''

pedir GET "/4"
esperar "404 si no existe" 404 'No existe la postulación 4.'

cors=$(curl -s -i -X OPTIONS "$api" -H 'Origin: http://localhost:4200' -H 'Access-Control-Request-Method: POST')
if [[ "$cors" == *"Access-Control-Allow-Origin: *"* ]]; then echo "ok     CORS para el cliente en Angular"; else echo "FALLA  CORS"; fallas=$((fallas + 1)); fi

echo "$fallas fallas"
exit $((fallas > 0))
