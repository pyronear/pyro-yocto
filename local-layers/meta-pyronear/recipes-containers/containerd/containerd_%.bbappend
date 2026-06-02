FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://config.toml"

do_install:append() {
    install -d ${D}${sysconfdir}/containerd
    install -m 0644 ${UNPACKDIR}/config.toml ${D}${sysconfdir}/containerd/config.toml
}