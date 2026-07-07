SUMMARY = "PyroEngine application orchestration and deployment"
LICENSE = "CLOSED"

inherit systemd useradd

USERADD_PACKAGES = "${PN}"
# Home on /data/home/pyro-engine-home ; -M because tmpfiles.d creates the directory at boot
USERADD_PARAM:${PN} = "--system -d /home/engine -M --shell /bin/false --user-group --groups docker engine"

RDEPENDS:${PN} += "docker-compose docker-moby"

SRC_URI = " \
    file://pyro-engine.service \
"

S = "${UNPACKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

SYSTEMD_SERVICE:${PN} = "pyro-engine.service"
SYSTEMD_AUTO_ENABLE = "enable"

do_install() {

    install -d ${D}/home
    install -d ${D}/data/home/pyro-engine-home
    install -d ${D}/data/pyro-engine-src
    
    # Symbolic link /home/engine -> /data/home/pyro-engine-home
    ln -s /data/home/pyro-engine-home ${D}/home/engine

    # Symbolic link /home/pyro-engine -> /data/pyro-engine-src
    ln -s /data/pyro-engine-src ${D}/home/pyro-engine

    # Change ownership of the directories
    chown -h engine:engine ${D}/home/engine
    chown -h engine:engine ${D}/data/home/pyro-engine-home

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/pyro-engine.service ${D}${systemd_system_unitdir}/pyro-engine.service

}

FILES:${PN} += " \
    /home/engine \
    /home/pyro-engine \
    /data/home/pyro-engine-home \
    /data/pyro-engine-src \
    ${systemd_system_unitdir}/pyro-engine.service \
"