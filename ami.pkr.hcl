packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = ">= 1.0.0"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "ap-northeast-2"
}

variable "source_ami" {
  type    = string
  default = "ami-0662f4965dfc70aca"  # Ubuntu 24.04 기준 (서울 리전)
}

variable "instance_type" {
  type    = string
  default = "t3.micro"
}

variable "s3_bucket" {
  type    = string
}

source "amazon-ebs" "mysite_ami" {
  region                  = var.aws_region
  source_ami              = var.source_ami
  instance_type           = var.instance_type
  ssh_username            = "ubuntu"
  ami_name                = "mysite-app-{{timestamp}}"
  associate_public_ip_address = true
  iam_instance_profile    = "PackerInstanceProfile"

  tags = {
    Name = "mysite-packer-ami"
  }
}

build {
  sources = ["source.amazon-ebs.mysite_ami"]

  provisioner "shell" {
    inline = [
      "sudo apt update",
      "sudo apt install -y openjdk-17-jdk",
      "sudo apt install unzip -y",
      "curl 'https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip' -o '/tmp/awscliv2.zip'",
      "unzip /tmp/awscliv2.zip -d /tmp",
      "sudo /tmp/aws/install",
      "sudo mkdir -p /home/ubuntu/myapp",
      "sudo chown -R ubuntu:ubuntu /home/ubuntu/myapp",
      "cd /home/ubuntu/myapp",
      "aws s3 cp s3://${var.s3_bucket}/mysite.jar /home/ubuntu/myapp/mysite.jar",
      "sudo chown -R ubuntu:ubuntu /home/ubuntu/myapp",
      "sudo tee /etc/systemd/system/mysite.service > /dev/null <<EOF",
      "[Unit]",
      "Description=Spring Boot App",
      "After=network.target",
      "",
      "[Service]",
      "User=ubuntu",
      "WorkingDirectory=/home/ubuntu/myapp",
      "ExecStart=/usr/bin/java -jar /home/ubuntu/myapp/mysite.jar",
      "Restart=always",
      "RestartSec=5",
      "",
      "[Install]",
      "WantedBy=multi-user.target",
      "EOF",
      "sudo systemctl daemon-reload",
      "sudo systemctl enable mysite"
    ]
  }
}
