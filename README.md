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

#### Yocto Cooker :

[https://github.com/cpb-/yocto-cooker.git](https://github.com/cpb-/yocto-cooker.git)

* Run `python3 .venv` in your project folder.
```
python3 -m venv .venv
source .venv/bin/activate
```
* Install Yocto Coocker.
```
python3 -m pip install --upgrade git+https://github.com/cpb-/yocto-cooker.git
```
* Exit `python3 .venv`.
```
deactivate
```

#### Clone Pyro Engine Yocto :

[https://git.smile.fr/stages-ecs-2026-yocto-pyronear/pyro-engine-yocto.git](https://git.smile.fr/stages-ecs-2026-yocto-pyronear/pyro-engine-yocto.git)

```
git clone https://git.smile.fr/stages-ecs-2026-yocto-pyronear/pyro-engine-yocto.git
```

## 🧩 Configuration

* Go to the root folder of the project
* We are going to configure the layer `meta-pyronear`
```
cd local-layers/meta-pyronear
```

### credentials.json :
* Go to this location --> **recipes-apps/pyro-setup/files**
```
cd recipes-apps/pyro-setup/files
```
* Create the "data" folder
* Create and edit the `credentials.json` file with :

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

* Go to this location --> **recipes-apps/pyro-setup/files**
```
cd recipes-apps/pyro-setup/files
```
* Create the .env file :
```
cp .env.example .env
```
* Edit your .env file :
```
# Pyronear API
API_URL=http://Ip_PC:5050
```

### Edit user and password (optional)

**Default User** : dev

**Default Password** : salut

* If you haven't installed whois, run this command :
```
sudo apt install whois
```
* Generate your own password and copy the output of this command :
```
printf "%q" $(mkpasswd -m sha256crypt new_password)
```
* Edit the file "core-image-minimal.bbappend", which is located here --> **local-layers/meta-pyronear/recipes-core/images**
```
cd recipes-core/images
```
```
# Encrypted password
PASSWD = "new_password"

# User creation
EXTRA_USERS_PARAMS = "useradd -p '${PASSWD}' -d /home/new_user -m -s /bin/sh -G docker new_user;"
```
Replace "new_password" with your new encrypted password and "new_user" with the new username.

* Finally, edit the "pyro-setup.bb" and "pyro-engine.service" files by replacing all instances of "/home/dev" with "/home/new_user". 

* You can find this file here :

**local-layers/meta-pyronear/recipes-apps/pyro-setup**
```
cd recipes-apps/pyro-setup
```
**local-layers/meta-pyronear/recipes-apps/pyro-setup/files**
```
cd recipes-apps/pyro-setup/files
```

## 💻 Build

* Go back to the root directory of your project, where you installed `Yocto Cooker`.
* Run `python3 .venv`.
```
source .venv/bin/activate
```
* Start the build with `Yocto Cooker`.
```
cooker cook menu.json
```

## 💾 Flash micro-sd card

* Insert the micro SD card into your computer.
* Note down the path to your microSD card (e.g. /dev/sdb or /dev/mmcblk0) to avoid any write errors :
```
lsblk
```
* Unmount the Bootfs and Rootfs partitions on the microSD card.
* Go to the folder --> **pyro-yocto-engine/builds/build-dev-pyro-rpi5/tmp/deploy/images/raspberrypi5**

```
cd builds/build-dev-pyro-rpi5/tmp/deploy/images/raspberrypi5
```

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
ifconfig
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
cd pyro-engine/
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
### ⚠️ Warning

The method for launching the Pyronear AI described above must be used the first time you launch Docker after flashing the microSD card.

In fact, the next time you restart the Raspberry Pi 5 (without flashing the microSD card), it will refresh its token and launch Docker automatically.

Run this command to check that everything is working properly :
```
cd pyro-engine/
docker logs -f engine
```
If you see that nothing has started, you can restart the AI manually by following the instructions above.

