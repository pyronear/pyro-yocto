FILESEXTRAPATHS:prepend:rpi := "${THISDIR}/files:"

SRC_URI += "file://fstab.fragment"

do_install:append() {
    cat ${UNPACKDIR}/fstab.fragment >> ${D}${sysconfdir}/fstab
}