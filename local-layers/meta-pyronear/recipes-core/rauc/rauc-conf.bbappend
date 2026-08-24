FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
FILESEXTRAPATHS:prepend := "${RAUC_CREDENTIALS_DIR}:"

SRC_URI:append = " \
    file://system.conf \
    file://${RAUC_CA_FILE} \
"

do_install() {
    install -d ${D}${nonarch_libdir}/rauc
    install -m 0644 ${UNPACKDIR}/system.conf ${D}${nonarch_libdir}/rauc/system.conf

    install -d ${D}${sysconfdir}/rauc
    install -m 0644 ${UNPACKDIR}/${RAUC_CA_FILE} ${D}${sysconfdir}/rauc/ca.cert.pem
}

FILES:${PN} += " \
    ${nonarch_libdir}/rauc/system.conf \
    ${sysconfdir}/rauc/ca.cert.pem \
"
