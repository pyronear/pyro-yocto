#!/bin/sh
echo "=== Physical resizing of the DATA partition ==="

# 1. Unmount the partition to avoid the interactive parted warning
umount /data

# 2. Push the walls of partition 3 to 100% of the SD card (Script Mode without prompt)
parted -s /dev/mmcblk0 resizepart 3 100%

# 3. Remount the now giant partition
mount /dev/mmcblk0p3 /data

# 4. Instantly apply the ext4 file system expansion
resize2fs /dev/mmcblk0p3

# 5. Creation of the permanent witness
touch /data/.resized
echo "=== Partition DATA and file system initialized successfully ==="