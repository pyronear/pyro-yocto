# pyro-yocto-engine
This repository allows you to flash a Yocto image onto a microSD card so you can run Pyro-Engine on a Raspberry Pi 5 in dev mode.

## ⚙️ Installation

### Prerequisites

* You will need a Raspberry Pi 5 (minimum 2 GB RAM)
* You will need a micro SD card (minimum 4 GB)
* If you wish to carry out development work, you will need to install and configure the Pyronear development environment : [https://github.com/pyronear/pyro-envdev/tree/main](https://github.com/pyronear/pyro-envdev/tree/main)

* Install the prerequisites for installing Yocto :
```
sudo apt update
sudo apt install build-essential chrpath cpio debianutils diffstat file gawk gcc git iputils-ping libacl1 liblz4-tool locales python3 python3-git python3-jinja2 python3-pexpect python3-pip python3-subunit socat texinfo unzip wget xz-utils zstd
```
* If you haven't installed bmaptool :
```
sudo apt install bmap-tools
```

We will use the “Scarthgap” version of Yocto.

#### Clone poky

[https://git.yoctoproject.org/poky](https://git.yoctoproject.org/poky)

```
git clone -b scarthgap https://git.yoctoproject.org/poky
```
Navigate to the poky folder
```
cd poky
```
#### Clone meta-raspberrypi

[https://git.yoctoproject.org/meta-raspberrypi](https://git.yoctoproject.org/meta-raspberrypi)

```
cd poky
git clone -b scarthgap https://git.yoctoproject.org/meta-raspberrypi
```

#### Clone meta-openembedded

[https://git.openembedded.org/meta-openembedded](https://git.openembedded.org/meta-openembedded)

```
git clone -b scarthgap https://git.openembedded.org/meta-openembedded
```
#### Clone meta-virtualization

[https://git.yoctoproject.org/meta-virtualization](https://git.yoctoproject.org/meta-virtualization)

```
git clone -b scarthgap https://git.yoctoproject.org/meta-virtualization
```

### Add meta-pyronear

* Clone git repository

```
git clone -b https://github.com/yolec-sml/pyro-yocto-engine.git
```
* Rename the “pyro-yocto-engine” folder to “meta-pyronear”

## 🧩 Configuration

* Go to the root of the poky folder
* Run the following command
```
source oe-init-build-env
```
This will automatically place it in the build folder

### Conf folder

* We are going to edit the file bblayers.conf --> **build/conf/bblayers.conf**
```
bitbake-layers add-layer ../meta-raspberrypi
bitbake-layers add-layer ../meta-openembedded/meta-oe
bitbake-layers add-layer ../meta-openembedded/meta-python
bitbake-layers add-layer ../meta-openembedded/meta-networking
bitbake-layers add-layer ../meta-openembedded/meta-multimedia
bitbake-layers add-layer ../meta-openembedded/meta-filesystems
bitbake-layers add-layer ../meta-virtualization
bitbake-layers add-layer ../meta-pyronear
```
* Check that all layers are installed correctly :
```
bitbake-layers show-layers
```
* We are going to edit the file local.conf --> **build/conf/local.conf**

Copy and paste the following text at the end of the file :
```
MACHINE = "raspberrypi5"

INIT_MANAGER = "systemd"

IMAGE_FSTYPES = "wic.bz2"
IMAGE_FSTYPES:append = " wic.bmap"

ENABLE_UART = "1"

RPI_EXTRA_CONFIG:append = "\\ndtoverlay=uart0-pi5\\n"

SERIAL_CONSOLES = "115200;ttyAMA0"

LICENSE_FLAGS_ACCEPTED = "synaptics-killswitch"

EXTRA_IMAGE_FEATURES:append = " ssh-server-dropbear"

EXTRA_IMAGE_FEATURES:remove = "debug-tweaks"

#IMAGE_INSTALL:append = " sudo"
IMAGE_INSTALL:append = " net-tools"
#IMAGE_INSTALL:append = " pyro-engine"

DISTRO_FEATURES:append = " virtualization"
IMAGE_INSTALL:append = " docker-moby"
IMAGE_INSTALL:append = " pyro-setup"
IMAGE_INSTALL:append = " docker docker-compose"
IMAGE_INSTALL:append = " curl jq"
#IMAGE_INSTALL:append = " nano"
IMAGE_INSTALL:append = " zram"

MACHINE_FEATURES:remove = " screen touchscreen alsa bluetooth"
DISTRO_FEATURES:remove = " x11 wayland opengl vulkan alsa bluetooth nfc pcmcia ptest debuginfod zeroconf nfs usbgadget multiarch"

INHERIT += "rm_work"
RM_WORK_EXCLUDE += "pyro-setup"

PACKAGECONFIG:pn-curl = "basic-auth bearer-auth openssl random threaded-resolver zlib"
PACKAGECONFIG:pn-jq = ""
PACKAGECONFIG:pn-parted = ""

KERNEL_MODULE_AUTOLOAD += "zram"

# Interdire l'installation 
BAD_RECOMMENDATIONS += "udev-hwdb"
BAD_RECOMMENDATIONS += "file"
```
We are going to enable the Shared State Cache feature.
To use sscache, uncomment the below lines :

```
BB_HASHSERVE_UPSTREAM = 'wss://hashserv.yoctoproject.org/ws'
SSTATE_MIRRORS ?= "file://.* http://sstate.yoctoproject.org/all/PATH;downloadfilename=PATH"
BB_HASHSERVE = "auto"
BB_SIGNATURE_HANDLER = "OEEquivHash"
```
### credentials.json
* Go to this location --> **poky/meta-pyronear/recipes-apps/pyro-setup/files**
* Create the "data" folder
* Create and edit the credentials.json file with :

```
{
  "mock_camera_1": {
    "name": "mock_camera_1",
    "adapter": "mock",
    "type": "static",
    "pose_ids": [
      36
    ],
    "id": "14",
    "poses": [],
    "bbox_mask_url": "",
    "token": ""
  }
}
```
Here, we are configuring the camera for development purposes, hence the use of the term "mock".
To configure other types of camera, please refer to the pyro-engine Readme ([https://github.com/pyronear/pyro-engine/blob/develop/README.md](https://github.com/pyronear/pyro-engine/blob/develop/README.md)).

### Environment variables

* Go to this location --> **poky/meta-pyronear/recipes-apps/pyro-setup/files**
* Create the .env file :
```
cp .env.example .env
```
### Edit user and password (optional)

* If you haven't installed whois, run this command :
```
sudo apt install whois
```
* Generate your own password and copy the output of this command :
```
printf "%q" $(mkpasswd -m sha256crypt new_password)
```
* Edit the file "core-image-minimal.bbappend", which is located here --> **poky/meta-pyronear/recipes-core/images**
```
# Votre mot de passe chiffré
PASSWD = "new_password"

# Création de l'utilisateur
EXTRA_USERS_PARAMS = "useradd -p '${PASSWD}' -d /home/new_user -m -s /bin/sh -G docker new_user;"
```
Replace "new_password" with your new encrypted password and "new_user" with the new username.

* Finally, edit the "pyro-setup.bb" file by replacing all instances of "/home/dev" with "/home/new_user". You can find this file here --> **poky/meta-pyronear/recipes-apps/pyro-setup**

## 💻 Build

* Run from the Poky root directory :

```
source oe-init-build-env
```

* Before building, make sure that the ".bbappend" files in the "meta-pyronear" layer are correctly detected and linked to the source recipes :
```
bitbake-layers show-appends | grep -E 'zram|core-image-minimal'
```

* Then run the build :
```
bitbake core-image-minimal
```

## 💾 Flash micro-sd card

* Insert the micro SD card into your computer.
* Note down the path to your microSD card (e.g. /dev/sdb or /dev/mmcblk0) to avoid any write errors :
```
lsblk
```
* Unmount the Bootfs and Rootfs partitions on the microSD card.
* Go to the folder --> /poky/build/tmp/deploy/images/raspberrypi5
* Run the following command and replace "location_microSD" with your own location :
```
sudo bmaptool copy core-image-minimal-raspberrypi5.rootfs.wic.bz2 /dev/location_microSD
```
* Run the command "sync" before removing your microSD card :
```
sync
```

## 🔌 Set up Running

* **UART (Serial)** : Connect your Raspberry Pi 5 to your PC using a USB-to-TTL (3.3V) adapter. This allows you to access the debug console without a screen.
* **Ethernet** : Connect the Pi 5 to your PC
* Open a serial terminal on your PC (e.g. Minicom, Screen or PuTTY) set to a baud rate of 115,200. (It is recommended that you power the Pi 5 via your PC for this operation)
```
# Replace /dev/ttyUSB0 with your UART device
sudo screen /dev/ttyUSB0 115200
```
**How to identify your UART adapter ?**

Run "ls /dev/ttyUSB*" or "ls /dev/ttyACM*" before and after plugging in your adapter to see which name appears.

* Once you've logged in, log in as ‘dev’
* Run the following command to retrieve the IP address:
```
if config
```

* Finally, switch off the Pi 5, exit the UART connection interface and disconnect your UART connection tool.

## ▶️ Run

* Run pyro-envdev
* Connect to your Pi 5 via SSH :
```
ssh dev@IP_Pi5
```
* Go to the pyro-engine folder :
```
cd /home/dev/pyro-engine/
```
* Refresh the API token :
```
./refresh_token.sh
```
* Activate the fire detection system :
```
docker compose up -d
```

If you want to disable the fire detection system, run this command :
```
docker compose down
```
