windows:

fnm install --corepack-enabled
fnm use v22.13.1
yarn set version --yarn-path 4.6.0
yarn config set nodeLinker node-modules
yarn config set enableGlobalCache false
yarn config set compressionLevel mixed
yarn install

ubuntu:
