SUMMARY = "Docker Platform Configuration for PyroNear"
DESCRIPTION = "Moves container and runtime storage to persistent partition"
LICENSE = "CLOSED"

SRC_URI = "file://daemon.json"

S = "${UNPACKDIR}"
# No source code to compile for this configuration recipe
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Create /etc/docker folder in RootFS image
    install -d ${D}${sysconfdir}/docker
    
    # Install daemon.json from extraction folder (UNPACKDIR)
    install -m 0644 ${UNPACKDIR}/daemon.json ${D}${sysconfdir}/docker/daemon.json
}

# Mapping the file to be included in the binary image
FILES:${PN} = "${sysconfdir}/docker/daemon.json"