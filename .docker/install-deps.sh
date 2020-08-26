set -e

yum install --enablerepo=ol7_optional_latest -y \
	wget \
    gcc \
    make \
    libstdc++-devel \
    libstdc++-static

yum clean -y all