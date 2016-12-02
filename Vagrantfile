# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version.
VAGRANTFILE_API_VERSION = "2"

# This is a single-vm vagrant file that will install OpenDaylight and all the tools needed to run
# basic upstream CSIT
Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  # NFS is fragile, disable it and use rsync
  config.nfs.functional = false

  # Explicitly using the "insecure" private key below, so don't need the insert_key
  config.ssh.insert_key = false

  # Set x11 forwarding so we can run GUIs on host from ssh sessions to guest
  config.ssh.forward_x11 = true

  # Box setup (specs, tools, etc)
  config.vm.define "nn" do |nn|

    nn.vm.hostname = "nn"

    # Configure 4G RAM for virtualbox or libvirt.
    nn.vm.provider :virtualbox do |c|
      c.memory = 2048
      c.cpus = 2
      c.name = "NN1"
    end

    # Build Vagrant box based on Fedora 25
    nn.vm.box = "fedora/25-cloud-base"

    # Install dependencies and tools from dnf
    nn.vm.provision "shell",
      inline: "dnf install -y --nogpgcheck git git-review java-openjdk java-1.8.0-openjdk-devel xauth ant"

    nn.vm.provision "shell", inline: "git clone https://github.com/jluhrsen/NN.git"
    nn.vm.provision "shell", inline: "git clone https://github.com/jluhrsen/nfl_examples"
    nn.vm.provision "shell", inline: "git clone https://github.com/simbrain/simbrain.git"
    nn.vm.provision "shell", inline: "cd simbrain; ant"
    nn.vm.provision "shell", inline: "echo 'export CLASSPATH=/home/vagrant/simbrain/build/main/Simbrain.jar:./' >> ~vagrant/.bashrc"
    nn.vm.provision "shell", inline: "mkdir -p networks/autonets"
    nn.vm.provision "shell", inline: "chown -R vagrant:vagrant *"
    nn.vm.provision "shell", inline: "cd NN/src; ln -s ../../nfl_examples/resources/data/ data; ln -s ../../networks networks"

  end

end
