FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://system.conf \
    file://ca.cert.pem \
"

# Replace installation to point to /etc/rauc
do_install() {

    install -d ${D}${sysconfdir}/rauc
    
    install -m 0644 ${UNPACKDIR}/system.conf ${D}${sysconfdir}/rauc/system.conf
    install -m 0644 ${UNPACKDIR}/ca.cert.pem ${D}${sysconfdir}/rauc/ca.cert.pem
}

# Ensure the package integrates these paths
FILES:${PN} += " \
    ${sysconfdir}/rauc/system.conf \
    ${sysconfdir}/rauc/ca.cert.pem \
"
