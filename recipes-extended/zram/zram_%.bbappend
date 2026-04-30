FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

do_install:append() {
    install -d ${D}${sysconfdir}/default
    
    echo 'ZRAM_ALGORITHM="lzo-rle"' > ${D}${sysconfdir}/default/zram
}
