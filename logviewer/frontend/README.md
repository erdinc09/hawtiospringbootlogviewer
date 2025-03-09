windows:

fnm install --corepack-enabled
fnm use v22.13.1
(remove .yarn folder, .yarnrc.yml, node_modules)
yarn set version --yarn-path 4.6.0
yarn config set nodeLinker node-modules
yarn config set enableGlobalCache false
yarn config set compressionLevel mixed
yarn install

ubuntu:
nvm install 22.13.1
nvm use 22.13.1
corepack enable
(remove .yarn folder, .yarnrc.yml, node_modules)
yarn set version --yarn-path 4.6.0
yarn config set nodeLinker node-modules
yarn config set enableGlobalCache false
yarn config set compressionLevel mixed
yarn install


TODO:
I think adding .yarn\releases\yarn-4.6.0.cjs to repo will solve the problem and many steps aboe will be unnecesssary

Reference:

https://v5-archive.patternfly.org/extensions/log-viewer/react-demos