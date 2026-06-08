# pyro-yocto-engine

This repository allows you to flash a Yocto image onto a microSD card so you can run Pyro-Engine on a Raspberry Pi 5 in dev mode.

## ⚙️ Installation

### Prerequisites

* You will need a Raspberry Pi 5 (minimum 2 GB RAM)
* You will need a micro SD card (minimum 4 GB)
* If you wish to carry out development work, you will need to install and configure the Pyronear development environment : [https://github.com/pyronear/pyro-envdev/tree/main](https://github.com/pyronear/pyro-envdev/tree/main)

* Install the prerequisites for installing Yocto:

```bash
sudo apt update
sudo apt install build-essential chrpath cpio debianutils diffstat file gawk gcc git iputils-ping libacl1 liblz4-tool locales python3 python3-git python3-jinja2 python3-pexpect python3-pip python3-subunit socat texinfo unzip wget xz-utils zstd
```

* If you haven't installed bmaptool:

```bash
sudo apt install bmap-tools
```

We will use the "Wrynose" version of Yocto (6.0 LTS).

#### Setup the Yocto Build Environment:

The project uses the official `bitbake-setup` tool (introduced in Yocto 5.3+) to automate the environment initialization using the `yocto-pyro.conf.json` file.

* **Method 1: Using VS Code (Recommended)**
  1. Open the project root folder in VS Code.
  2. Install the official **Yocto Project BitBake** extension.
  3. The extension will automatically detect `yocto-pyro.conf.json` and prompt you to initialize/configure the environment. Select the configuration named **"Pyro conf for rpi5"**.

* **Method 2: Using the Command Line**
  1. Clone the `bitbake` repository at the root of the project to access the `bitbake-setup` utility:

     ```bash
     git clone https://git.openembedded.org/bitbake
     ```

  2. Run the initialization command:

     ```bash
     ./bitbake/bin/bitbake-setup init yocto-pyro.conf.json
     ```

     When prompted, select **"Pyro conf for rpi5"** to initialize the layers and configure the build directory.

## 🧩 Configuration

* Go to the root folder of the project.
* We are going to configure the layer files under `local-layers/meta-pyronear/recipes-apps/pyro-engine/files`:

  ```bash
  cd local-layers/meta-pyronear/recipes-apps/pyro-engine/files
  ```

### credentials.json:

* Create the "data" folder:

  ```bash
  mkdir -p data
  ```

* Create and edit the `data/credentials.json` file with:

```json
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

* In the same folder (`local-layers/meta-pyronear/recipes-apps/pyro-engine/files`), create the `.env` file:

  ```bash
  cp .env.example .env
  ```

* Edit your `.env` file and replace `Ip_PC` with your PC's local IP address:

  ```env
  # Pyronear API
  API_URL=http://Ip_PC:5050
  ```

### Edit user and password (optional)

**Default User** : dev

**Default Password** : salut

* If you haven't installed whois, run this command :

```bash
sudo apt install whois
```

* Generate your own password and copy the output of this command :

```bash
printf "%q" $(mkpasswd -m sha256crypt new_password)
```

* Edit the file "core-image-minimal.bbappend", which is located here --> **local-layers/meta-pyronear/recipes-core/images**

```bash
cd recipes-core/images
```

```file
# Encrypted password
PASSWD = "new_password"

# User creation
EXTRA_USERS_PARAMS = "useradd -p '${PASSWD}' -d /home/new_user -m -s /bin/sh -G docker new_user;"
```

Replace "new_password" with your new encrypted password and "new_user" with the new username.

* Finally, edit the `pyro-setup.bb` and `pyro-engine.service` files by replacing all instances of `/home/dev` with `/home/new_user`. 

* You can find these files here:

  * **`pyro-setup.bb`**: `local-layers/meta-pyronear/recipes-apps/pyro-engine/pyro-setup.bb`
  * **`pyro-engine.service`**: `local-layers/meta-pyronear/recipes-apps/pyro-engine/files/pyro-engine.service`

## 💻 Build

* Source the Yocto/BitBake build environment:

  ```bash
  source bitbake-builds/pyro-yocto/layers/wrynose/oe-init-build-env bitbake-builds/pyro-yocto/build
  ```

* Start the compilation of the `pyronear-image` custom image:

  ```bash
  bitbake pyronear-image
  ```

## 💾 Flash micro-sd card

* Insert the micro SD card into your computer.
* Note down the path to your microSD card (e.g. /dev/sdb or /dev/mmcblk0) to avoid any write errors :

```bash
lsblk
```

* Unmount the Bootfs and Rootfs partitions on the microSD card.
* Go to the deploy folder:

  ```bash
  cd bitbake-builds/pyro-yocto/build/tmp/deploy/images/pyronear-rpi5
  ```

* Run the following command (replace `/dev/location_microSD` with your microSD card device, e.g., `/dev/sdb` or `/dev/mmcblk0`):

  ```bash
  sudo bmaptool copy pyronear-image-pyronear-rpi5.rootfs.wic.bz2 /dev/location_microSD
  ```

* Run the command "sync" before removing your microSD card :

```bash
sync
```

## 🔌 Set up Running

* **UART (Serial)** : Connect your Raspberry Pi 5 to your PC using a USB-to-TTL (3.3V) adapter. This allows you to access the debug console without a screen.
* **Ethernet** : Connect the Pi 5 to your PC
* Open a serial terminal on your PC (e.g. Minicom, Screen or PuTTY) set to a baud rate of 115,200. (It is recommended that you power the Pi 5 via your PC for this operation)

```bash
# Replace /dev/ttyUSB0 with your UART device
sudo screen /dev/ttyUSB0 115200
```

**How to identify your UART adapter ?**

Run "ls /dev/ttyUSB*" or "ls /dev/ttyACM*" before and after plugging in your adapter to see which name appears.

* Once you've logged in, log in as ‘dev’
* Run the following command to retrieve the IP address:

```bash
ifconfig
```

* Finally, switch off the Pi 5, exit the UART connection interface and disconnect your UART connection tool.

## ▶️ Run

* Run pyro-envdev
* Connect to your Pi 5 via SSH :

```bash
ssh dev@IP_Pi5
```

* Go to the pyro-engine folder :

```bash
cd pyro-engine/
```

* Refresh the API token :

```bash
./refresh_token.sh
```

* Activate the fire detection system :

```bash
docker compose up -d
```

If you want to disable the fire detection system, run this command :

```bash
docker compose down
```

### ⚠️ Warning

The method for launching the Pyronear AI described above must be used the first time you launch Docker after flashing the microSD card.

In fact, the next time you restart the Raspberry Pi 5 (without flashing the microSD card), it will refresh its token and launch Docker automatically.

Run this command to check that everything is working properly :

```bash
cd pyro-engine/
docker logs -f engine
```

If you see that nothing has started, you can restart the AI manually by following the instructions above.
