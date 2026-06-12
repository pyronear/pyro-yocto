#!/bin/sh
set -e

# Binary declarations
LSBLK="/usr/bin/lsblk"
SFDISK="/usr/sbin/sfdisk"
RESIZE2FS="/usr/sbin/resize2fs"

LABEL="data"
PART_LINK="/dev/disk/by-partlabel/$LABEL"

# 1. Wait for udev (early-boot safety)
TIMEOUT=5
while [ ! -e "$PART_LINK" ]; do
    if [ "$TIMEOUT" -le 0 ]; then
        echo "Error: PARTLABEL '$LABEL' not found after 5s."
        exit 1
    fi
    sleep 1
    TIMEOUT=$((TIMEOUT - 1))
done

# 2. Clean retrieval of disk information via lsblk
DISK_NAME=$("$LSBLK" -no pkname "$PART_LINK" | tr -d ' ')
PARTNUM=$("$LSBLK" -no partn "$PART_LINK" | tr -d ' ')
PART_NAME=$("$LSBLK" -no name "$PART_LINK" | tr -d ' ')

DISK="/dev/$DISK_NAME"
PART_DEV="/dev/$PART_NAME"

echo "=== Online physical resizing of $DISK partition $PARTNUM ==="

# 3. sfdisk extends the partition boundaries live (hot)
echo ", +" | "$SFDISK" --force "$DISK" -N "$PARTNUM"

# 4. Force the Linux kernel to re-read the main disk size
echo 1 > "/sys/class/block/$DISK_NAME/device/rescan" || true

echo "=== Online logical resizing of ext4 filesystem ==="

# 5. resize2fs performs the online extension while the filesystem is mounted
"$RESIZE2FS" "$PART_DEV"

touch /data/.resized

echo "=== Partition DATA ($LABEL) expanded successfully at live boot ==="
