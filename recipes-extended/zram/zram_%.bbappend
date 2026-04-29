FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

do_install:append() {
    # 1. On s'assure que le dossier /etc/default existera bien sur la carte SD
    install -d ${D}${sysconfdir}/default
    
    # 2. Création du fichier zram et on y injecte l'algorithme compatible avec la Pi 5
    echo 'ZRAM_ALGORITHM="lzo-rle"' > ${D}${sysconfdir}/default/zram
}
