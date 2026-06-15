FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://daemon.json"

do_install:append() {
    # Install custom daemon.json configuration (overwrites the default if needed)
    install -m 0644 ${UNPACKDIR}/daemon.json ${D}${sysconfdir}/docker/daemon.json

    # Create persistent runtime directory /data/docker
    install -d ${D}/data/docker
}

FILES:${PN} += "/data/docker"
