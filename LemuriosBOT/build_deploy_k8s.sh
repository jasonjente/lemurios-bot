#!/usr/bin/env bash
set -euo pipefail

# === Config (change if you want defaults) ===
APP_NAME="${APP_NAME:-lemurios-bot}"          # deployment & service name
NAMESPACE="${NAMESPACE:-lemurios}"
DOCKERFILE="${DOCKERFILE:-docker/Dockerfile}" # path to Dockerfile
BUILD_CONTEXT="${BUILD_CONTEXT:-.}"           # docker build context
K8S_DIR="${K8S_DIR:-k8s}"                      # kustomize dir
CLUSTER_NAME="${CLUSTER_NAME:-wslkind}"        # kind cluster name
CONTAINER_NAME_IN_DEPLOY="${CONTAINER_NAME_IN_DEPLOY:-app}"  # container name in deployment spec
PORT_FORWARD="${PORT_FORWARD:-false}"          # true/false to PF after deploy
LOCAL_PORT="${LOCAL_PORT:-17081}"
SERVICE_PORT="${SERVICE_PORT:-17081}"

# Auto image tag = git short SHA or timestamp fallback
GIT_SHA="$(git rev-parse --short HEAD 2>/dev/null || true)"
if [[ -n "${GIT_SHA}" ]]; then
  IMAGE_TAG="${IMAGE_TAG:-${GIT_SHA}}"
else
  IMAGE_TAG="${IMAGE_TAG:-$(date +%Y%m%d%H%M%S)}"
fi
IMAGE="${IMAGE:-${APP_NAME}:${IMAGE_TAG}}"

usage() {
  cat <<EOF
Usage: $(basename "$0") [--no-build] [--no-load] [--no-apply] [--no-set-image] [--port-forward]
Environment overrides:
  APP_NAME=${APP_NAME}
  NAMESPACE=${NAMESPACE}
  DOCKERFILE=${DOCKERFILE}
  BUILD_CONTEXT=${BUILD_CONTEXT}
  K8S_DIR=${K8S_DIR}
  CLUSTER_NAME=${CLUSTER_NAME}
  CONTAINER_NAME_IN_DEPLOY=${CONTAINER_NAME_IN_DEPLOY}
  IMAGE=${IMAGE}
  LOCAL_PORT=${LOCAL_PORT}
  SERVICE_PORT=${SERVICE_PORT}

Examples:
  IMAGE_TAG=1.0 ./$(basename "$0")
  IMAGE=myrepo/lemurios-bot:dev ./$(basename "$0") --port-forward
EOF
}

NO_BUILD=false
NO_LOAD=false
NO_APPLY=false
NO_SET_IMAGE=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help) usage; exit 0;;
    --no-build) NO_BUILD=true; shift;;
    --no-load) NO_LOAD=true; shift;;
    --no-apply) NO_APPLY=true; shift;;
    --no-set-image) NO_SET_IMAGE=true; shift;;
    --port-forward) PORT_FORWARD=true; shift;;
    *) echo "Unknown arg: $1"; usage; exit 1;;
  esac
done

echo "=== Settings ==="
echo "APP_NAME=${APP_NAME}"
echo "NAMESPACE=${NAMESPACE}"
echo "IMAGE=${IMAGE}"
echo "DOCKERFILE=${DOCKERFILE}"
echo "BUILD_CONTEXT=${BUILD_CONTEXT}"
echo "K8S_DIR=${K8S_DIR}"
echo "K8S context: $(kubectl config current-context || echo 'N/A')"
echo

# --- Sanity checks ---
[[ -f "${DOCKERFILE}" ]] || { echo "Dockerfile not found at ${DOCKERFILE}"; exit 1; }
[[ -d "${K8S_DIR}" ]] || { echo "K8S_DIR not found: ${K8S_DIR}"; exit 1; }
command -v kubectl >/dev/null || { echo "kubectl not found"; exit 1; }
command -v docker >/dev/null || { echo "docker not found"; exit 1; }

# --- Build image ---
if [[ "${NO_BUILD}" == "false" ]]; then
  echo "==> Building image: ${IMAGE}"
  docker build -t "${IMAGE}" -f "${DOCKERFILE}" "${BUILD_CONTEXT}"
else
  echo "==> Skipping build (--no-build)"
fi

# --- Load into kind if applicable ---
CURRENT_CTX="$(kubectl config current-context || true)"
if [[ "${NO_LOAD}" == "false" ]]; then
  if command -v kind >/dev/null 2>&1; then
    if kind get clusters 2>/dev/null | grep -qx "${CLUSTER_NAME}"; then
      # Only load if we're on a kind context (usually starts with 'kind-')
      if [[ "${CURRENT_CTX}" == kind-* ]]; then
        echo "==> Loading image into kind cluster '${CLUSTER_NAME}'"
        kind load docker-image "${IMAGE}" --name "${CLUSTER_NAME}"
      else
        echo "==> Current context '${CURRENT_CTX}' is not a kind context; skipping kind load."
      fi
    else
      echo "==> kind cluster '${CLUSTER_NAME}' not found; skipping kind load."
    fi
  else
    echo "==> 'kind' not installed; skipping kind load."
  fi
else
  echo "==> Skipping kind load (--no-load)"
fi

# --- Apply manifests (namespace, service, deployment) ---
if [[ "${NO_APPLY}" == "false" ]]; then
  echo "==> Applying kustomize dir: ${K8S_DIR}"
  kubectl apply -k "${K8S_DIR}"
else
  echo "==> Skipping apply (--no-apply)"
fi

# --- Ensure namespace exists before next steps ---
if ! kubectl get ns "${NAMESPACE}" >/dev/null 2>&1; then
  echo "Waiting for namespace ${NAMESPACE} to exist..."
  for i in {1..10}; do
    kubectl get ns "${NAMESPACE}" >/dev/null 2>&1 && break || sleep 1
  done
fi

# --- Set image on the deployment to the freshly built tag ---
if [[ "${NO_SET_IMAGE}" == "false" ]]; then
  echo "==> Setting image on deployment/${APP_NAME} to ${IMAGE}"
  kubectl -n "${NAMESPACE}" set image deploy/"${APP_NAME}" \
    "${CONTAINER_NAME_IN_DEPLOY}"="${IMAGE}" --record=true || true
else
  echo "==> Skipping set image (--no-set-image)"
fi

# --- Wait for rollout ---
echo "==> Waiting for rollout to complete"
kubectl -n "${NAMESPACE}" rollout status deploy/"${APP_NAME}"

# --- Show status ---
echo "==> Current resources"
kubectl -n "${NAMESPACE}" get deploy,po,svc -o wide

# --- Optional port-forward ---
if [[ "${PORT_FORWARD}" == "true" ]]; then
  echo "==> Port-forwarding svc/${APP_NAME} ${LOCAL_PORT}:${SERVICE_PORT}"
  echo "Press Ctrl+C to stop port-forward."
  kubectl -n "${NAMESPACE}" port-forward svc/"${APP_NAME}" "${LOCAL_PORT}:${SERVICE_PORT}"
fi

echo "✅ Done."
