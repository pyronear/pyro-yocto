#!/bin/sh

# 1. Agrandir la partition 2 (RootFS) pour occuper 100% de la carte SD
parted -s /dev/mmcblk0 resizepart 2 100%

# 2. Étendre le système de fichiers pour qu'il reconnaisse ce nouvel espace
resize2fs /dev/mmcblk0p2

# 3. Mission accomplie : on désactive ce script pour ne pas le refaire au prochain démarrage
systemctl disable expand-rootfs.service