#!/usr/bin/env bash

SCRIPT_DIR="$(realpath "$(dirname "$0")")"
if [[ ! -f "${SCRIPT_DIR}/image-info" ]]; then
    echo "Error: 'image-info' file not found."
    exit 1
fi
source "${SCRIPT_DIR}/image-info"

DOCKERFILE_PATH="${SCRIPT_DIR}/dockerfile"

# build docker image
echo "building docker image '${IMAGE_NAME}' ..."
docker build -t "${IMAGE_NAME}" -f "${DOCKERFILE_PATH}" "${SCRIPT_DIR}"
