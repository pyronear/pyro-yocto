FILESEXTRAPATHS:prepend := "${THISDIR}/../../files/key-rauc:${THISDIR}/files:"

RAUC_KEYRING_FILE = "ca.cert.pem"

SRC_URI:append = " \
    file://system.conf \
"
