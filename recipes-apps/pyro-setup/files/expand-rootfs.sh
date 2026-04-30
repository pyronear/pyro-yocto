#!/bin/sh

# Expand partition 2 (RootFS) to use 100% of the SD card
parted -s /dev/mmcblk0 resizepart 2 100%

# Extend the file system so that it recognises this new space
resize2fs /dev/mmcblk0p2

systemctl disable expand-rootfs.service