#!/usr/bin/env bash

PROJECT_PATH=$(dirname "$(realpath "$0")")
DOCKER_DIR="${PROJECT_PATH}/support/docker"

if [[ ! -f "${DOCKER_DIR}/image-info" ]]; then
    echo "Error: 'image-info' file not found."
    exit 1
fi
source "${DOCKER_DIR}/image-info"

# (Re)builds the image only if needed: missing, or dockerfile changed.
"${DOCKER_DIR}/build-docker.sh"

# Run docker image
echo "starting docker image ${IMAGE_NAME}..."
docker run --rm -it \
    --user "$(id -u):$(id -g)" \
    -v $(readlink -f $SSH_AUTH_SOCK):/ssh-agent -e SSH_AUTH_SOCK=/ssh-agent \
    -v "${HOME}/.gitconfig:/etc/gitconfig" \
    -v "${PROJECT_PATH}:${PROJECT_PATH}" \
    -w "${PROJECT_PATH}" \
    "${IMAGE_NAME}"
