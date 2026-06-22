SUMMARY = "Data partition directory provisioning for PyroNear"
DESCRIPTION = "Provides native systemd-tmpfiles configuration to safely provision directories on /data at boot time."
SECTION = "core"
LICENSE = "CLOSED"

SRC_URI = "file://data-provisioning.conf"

S = "${UNPACKDIR}"

# Pas de compilation nécessaire
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Emplacement standard pour les configurations tmpfiles système
    install -d ${D}${nonarch_libdir}/tmpfiles.d
    install -m 0644 ${S}/data-provisioning.conf ${D}${nonarch_libdir}/tmpfiles.d/data-provisioning.conf
}

# Emballage du fichier dans le paquet final
FILES:${PN} += "${nonarch_libdir}/tmpfiles.d/data-provisioning.conf"

# Sécurité : On s'assure que cette recette ne s'exécute pas si systemd n'est pas le gestionnaire d'init
REQUIRED_DISTRO_FEATURES = "systemd"
inherit features_check